package edu.itb.twofishsms.crypto;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;
import edu.itb.twofishsms.TwoFishSMSApp;

public class TwoFish {
	 
	private final byte[][] RS = {
			{0x01, (byte)0xA4, 0x55, (byte)0x87, 0x5A, 0x58, (byte)0xDB, (byte)0x9E},
			{(byte) 0xA4, 0x56, (byte) 0x82, (byte) 0xF3, 0x1E, (byte) 0xC6, 0x68, (byte) 0xE5},
			{0x02, (byte) 0xA1, (byte) 0xFC, (byte) 0xC1, 0x47, (byte) 0xAE, 0x3D, 0x19},
			{(byte) 0xA4, 0x55, (byte) 0x87, 0x5A, 0x58, (byte) 0xDB, (byte) 0x9E, 0x03}	
	};
	
	private final byte[][] MDS = {
			{0x01, (byte) 0xEF, 0x5B, 0x5B},
			{0x5B, (byte) 0xEF, (byte) 0xEF, 0x01},
			{(byte) 0xEF, 0x5B, 0x01, (byte) 0xEF},
			{(byte) 0xEF, 0x01, (byte) 0xEF, 0x5B}
	};
	
	private final byte[][] q0 = {
			{0x8, 0x1, 0x7, 0xD, 0x6, 0xF, 0x3, 0x2, 0x0, 0xB, 0x5, 0x9, 0xE, 0xC, 0xA, 0x4},
			{0xE, 0xC, 0xB, 0x8, 0x1, 0x2, 0x3, 0x5, 0xF, 0x4, 0xA, 0x6, 0x7, 0x0, 0x9, 0xD},
			{0xB, 0xA, 0x5, 0xE, 0x6, 0xD, 0x9, 0x0, 0xC, 0x8, 0xF, 0x3, 0x2, 0x4, 0x7, 0x1},
			{0xD, 0x7, 0xF, 0x4, 0x1, 0x2, 0x6, 0xE, 0x9, 0xB, 0x3, 0x0, 0x8, 0x5, 0xC, 0xA}
	};
	
	private final byte[][] q1 = {
			{0x2, 0x8, 0xB, 0xD, 0xF, 0x7, 0x6, 0xE, 0x3, 0x1, 0x9, 0x4, 0x0, 0xA, 0xC, 0x5},
			{0x1, 0xE, 0x2, 0xB, 0x4, 0xC, 0x3, 0x7, 0x6, 0xD, 0xA, 0x5, 0xF, 0x9, 0x0, 0x8},
			{0x4, 0xC, 0x7, 0x5, 0x1, 0x6, 0x9, 0xA, 0x0, 0xE, 0xD, 0x8, 0x2, 0xB, 0x3, 0xF},
			{0xB, 0x9, 0x5, 0x1, 0xC, 0x3, 0xD, 0xE, 0x6, 0x4, 0x7, 0xF, 0x2, 0x0, 0x8, 0xA}
	};
	
	private int[] Me;
	private int[] Mo;
	private int[] S;
	
	private String plainText;
	private String keyText;
	public static int lengthByte = 16;
	
	public TwoFish(String _plainText, String _keyText){
		this.plainText = _plainText;
		this.keyText = _keyText;
		
	}
	
	public String encrypt() throws UnsupportedEncodingException{
		String cipherString = "";
		
		// Initialize plain bytes and divide the plain text to list of 16 byte
		byte[] plainBytes = plainText.getBytes("UTF-16");
		ArrayList<byte[]> blockBytes = blockPlainBytes(plainBytes);
		
		// Initialize key bytes and prepare the key by add padding if the length is not
		// sufficient
		byte[] keyBytes = keyText.getBytes("UTF-16");
		Log.d(TwoFishSMSApp.TAG, "Key before padding : " + keyBytes.length);
		keyBytes = paddingKeyBytes(keyBytes);
		if(keyBytes != null){
			// Preparing the key schedule
			Log.d(TwoFishSMSApp.TAG, "Key after padding : " + keyBytes.length);
			// Generate key Me, Mo and S
			generateKey(keyBytes);
			
			// Core of the encryption process
			for(byte[] p : blockBytes){
				int[] P = new int[4];
				int[][] R = new int[17][4];
				
				// Divide plain text from 16 byte to 4 words (1 word = 32 byte)
				for(int i = 0; i < 4; ++i){
					int result = 0;
					for(int j = 0; j < 4; ++j){
						result += p[4 * i + j] * (int) Math.pow(2.0f, 8.0 * j);
					}
					P[i] = result;
				}
				
				for(int i = 0; i < 4; ++i){
					if(i % 2 == 0){
						R[0][i] = P[i] ^ functionKEven(i);
					}else{
						R[0][i] = P[i] ^ functionKOdd(i);
					}
				}
				
				for(int r = 0; r < 16; ++r){
					int[] F = functionF(R[r][0], R[r][1], r);
					R[r+1][0] = Integer.rotateRight(R[r][2] ^ F[0], 1);
					R[r+1][1] = Integer.rotateLeft(R[r][3], 1) ^ F[1];
					R[r+1][2] = R[r][0];
					R[r+1][3] = R[r][1];
				}
				
				int[] C = new int[4];
				for(int i = 0; i < 4; ++i){
					if((i + 4) % 2 == 0)
						C[i] = R[16][(i + 2) % 4] ^ functionKEven(i + 4);
					else
						C[i] = R[16][(i + 2) % 4] ^ functionKOdd(i + 4);
				}
				
				byte[] c = new byte[16];
				for(int i = 0; i < 16; ++i){
					c[i] = (byte)( (C[i/4] / (int) Math.pow(2.0f, 8.0 * (i * 4))) % (int) Math.pow(2.0f, 8.0f));
				}
				
				for(int i = 0; i < 16; ++i){
					cipherString += Integer.toHexString(c[i] & 0xFF) + " ";
				}
			}
		}
		return cipherString;
	}
	
	// Generate key Me, Mo and S
	private void generateKey(byte[] key){
		// Generate key M
		int k = key.length / 8;
		Me = new int[k];
		Mo = new int[k];
		for(int i = 0; i < k*2; ++i){
			int M = functionM(key, i);
			int mod = i % 2;
			int div = i / 2;
			if(mod == 0)
				Me[div] = M;
			else
				Mo[div] = M;
		}
		
		S = new int[k];
		for(int i = 0; i < k; ++i){
			int Si = functionS(key, k-1-i);
			S[i] = Si;
		}
		//printKey();
	}
	
	private int functionM(byte[] m, int i){
		int M = 0;
		for(int j = 0; j < 4; ++j){
			M += m[4*i + j] * (int) Math.pow(2.0f, 8.0*j);
		}
		return M;
	}
	
	private int functionS(byte[] m, int i){
		int Si = 0;
		byte[] s = new byte[4];
		for(int j = 0; j < 4; ++j){
			byte result = 0;
			for(int k = 0; k < 8; k++){
				result += RS[j][k] * m[8*i + k];
			}
			s[j] = result;
		}
		
		for(int l = 0; l < 4; ++l){
			Si += s[l] * (int) Math.pow(2.0f, 8.0*l);
		}
		return Si;
	}
	
	private byte functionQ0(byte x){
		byte y = 0;
		byte a0 = (byte)((x & 240) >> 4);
		byte b0 = (byte)(x & 15);
		byte a1 = (byte)(a0 ^ b0);
		byte b1 = (byte)(a0 ^ (ROTR4(b0)) ^ ((8 * a0) % 16));
		//Log.d(TwoFishSMSApp.TAG, "q0 : " + a1 + " " + b1);
		byte a2 = q0[0][a1]; byte b2 = q0[1][b1];
		byte a3 = (byte)(a2 ^ b2);
		byte b3 = (byte)(a2 ^ (ROTR4(b2)) ^ ((8 * a2) % 16));
		byte a4 = q0[2][a3]; byte b4 = q0[3][b3];
		y = (byte)(((a4 << 4) & 240) | b4);
		return y;
	}
	
	private byte ROTR4(byte x){
		return (byte)(x >> 1 | ((x << 3) & 15));
	}
	
	private byte functionQ1(byte x){
		byte y = 0;
		byte a0 = (byte)((x & 240) >> 4);
		byte b0 = (byte)(x & 15);
		byte a1 = (byte)(a0 ^ b0);
		byte b1 = (byte)(a0 ^ (ROTR4(b0)) ^ ((8 * a0) % 16));
		//Log.d(TwoFishSMSApp.TAG, "q1 : " + a1 + " " + b1);
		byte a2 = q1[0][a1]; byte b2 = q1[1][b1];
		byte a3 = (byte)(a2 ^ b2);
		byte b3 = (byte)(a2 ^ (ROTR4(b2)) ^ ((8 * a2) % 16));
		byte a4 = q1[2][a3]; byte b4 = q1[3][b3];
		y = (byte)(((a4 << 4) & 240) | b4);
		return y;
	}
	
	private int functionH(int X, int[] L){
		int Z = 0;
		byte[] y = new byte[4];
		byte[][] l = new byte[L.length][4];
		
		for(int i = 0; i < 4; ++i){
			y[i] = (byte) ((byte) (X / (int)Math.pow(2.0f, 8.0 * i)) % ((int) Math.pow(2.0f, 8.0f)));
		}
		
		for(int i = 0; i < L.length; ++i){
			for(int j = 0; j < 4; ++j){
				l[i][j] = (byte) ((byte) (L[i] / (int)Math.pow(2.0f, 8.0 * j)) % ((int) Math.pow(2.0f, 8.0f)));
			}
		}
		
		if(L.length == 4){
			y[0] = (byte) (functionQ1(y[0]) ^ l[3][0]);
			y[1] = (byte) (functionQ0(y[1]) ^ l[3][1]);
			y[2] = (byte) (functionQ0(y[2]) ^ l[3][2]);
			y[3] = (byte) (functionQ1(y[3]) ^ l[3][3]);
			
			y[0] = (byte) (functionQ1(y[0]) ^ l[2][0]);
			y[1] = (byte) (functionQ1(y[1]) ^ l[2][1]);
			y[2] = (byte) (functionQ0(y[2]) ^ l[2][2]);
			y[3] = (byte) (functionQ0(y[3]) ^ l[2][3]);
		}else if(L.length == 3){
			y[0] = (byte) (functionQ1(y[0]) ^ l[2][0]);
			y[1] = (byte) (functionQ1(y[1]) ^ l[2][1]);
			y[2] = (byte) (functionQ0(y[2]) ^ l[2][2]);
			y[3] = (byte) (functionQ0(y[3]) ^ l[2][3]);
		}
		
		y[0] = (byte) (functionQ0(y[0]) ^ l[1][0]);
		y[1] = (byte) (functionQ1(y[1]) ^ l[1][1]);
		y[2] = (byte) (functionQ0(y[2]) ^ l[1][2]);
		y[3] = (byte) (functionQ1(y[3]) ^ l[1][3]);
		
		y[0] = (byte) (functionQ0(y[0]) ^ l[0][0]);
		y[1] = (byte) (functionQ0(y[1]) ^ l[0][1]);
		y[2] = (byte) (functionQ1(y[2]) ^ l[0][2]);
		y[3] = (byte) (functionQ1(y[3]) ^ l[0][3]);
		
		y[0] = (byte) (functionQ1(y[0]));
		y[1] = (byte) (functionQ0(y[1]));
		y[2] = (byte) (functionQ1(y[2]));
		y[3] = (byte) (functionQ0(y[3]));
		
		byte[] z = new byte[4];
		for(int j = 0; j < 4; ++j){
			byte result = 0;
			for(int k = 0; k < 4; k++){
				result += MDS[j][k] * y[k];
			}
			z[j] = result;
		}
		
		for(int i = 0; i < 4; ++i){
			Z += z[i] * (int) Math.pow(2.0f, 8.0*i);
		}
		
		return Z;
	}
	
	private int functionG(int X){
		return functionH(X, S);
	}
	
	private int functionKEven(int i){
		i = i/2;
		int result = 0;
		int rho = (int)(Math.pow(2.0f, 24.0f)) + (int)(Math.pow(2.0f, 16.0f)) + (int)(Math.pow(2.0f, 8.0f)) + (int)(Math.pow(2.0f, 0.0f));
		int A = functionH(2 * i * rho, Me);
		int B = Integer.rotateLeft((functionH(((2 * i + 1) * rho), Mo)), 8);
		result = (A + B) % (int) Math.pow(2.0f, 32.0);
		return result;
	}
	
	private int functionKOdd(int i){
		i = (i - 1)/2;
		int result = 0;
		int rho = (int)(Math.pow(2.0f, 24.0f)) + (int)(Math.pow(2.0f, 16.0f)) + (int)(Math.pow(2.0f, 8.0f)) + (int)(Math.pow(2.0f, 0.0f));
		int A = functionH(2 * i * rho, Me);
		int B = Integer.rotateLeft((functionH(((2 * i + 1) * rho), Mo)), 8);
		result = Integer.rotateLeft((A + (2*B)) % (int) Math.pow(2.0f, 32.0), 9);
		return result;
	}
	
	private int[] functionF(int R0, int R1, int r){
		int[] F = new int[2];
		int T0 = functionG(R0);
		int T1 = functionG(Integer.rotateLeft(R1, 8));
		F[0] = (T0 + T1 + functionKEven(2 * r + 8)) % (int) Math.pow(2.0f, 32.0);
		F[1] = (T0 + (2*T1) + functionKOdd(2 * r + 9)) % (int) Math.pow(2.0f, 32.0);
		return F;
	}
	
	private void printKey(){
		Log.d(TwoFishSMSApp.TAG, "----- Printing key Mo -----");
		for(int i = 0; i < Mo.length; ++i){
			Log.d(TwoFishSMSApp.TAG, "index " + i + " : " + Mo[i] + " " + Integer.toBinaryString(Mo[i]));
		}
		
		Log.d(TwoFishSMSApp.TAG, "----- Printing key Me -----");
		for(int i = 0; i < Me.length; ++i){
			Log.d(TwoFishSMSApp.TAG, "index " + i + " : " + Me[i] + " "+ Integer.toBinaryString(Me[i]));
		}
		
		Log.d(TwoFishSMSApp.TAG, "----- Printing key S -----");
		for(int i = 0; i < S.length; ++i){
			Log.d(TwoFishSMSApp.TAG, "index " + i + " : " + S[i] + " "+ Integer.toBinaryString(S[i]));
		}
	}
	
	private ArrayList<byte[]> blockPlainBytes(byte[] plainBytes){
		ArrayList<byte[]> blockBytes = new ArrayList<byte[]>();
		
		// Copy bytes and add to block
		int blockSize = (plainBytes.length / lengthByte);
		for(int i = 0; i < blockSize; ++i){
			byte[] copyBytes = new byte[lengthByte];
			System.arraycopy(plainBytes, i*lengthByte, copyBytes, 0, lengthByte);
			blockBytes.add(copyBytes);
		}
		
		int mod = plainBytes.length % lengthByte;
		if(mod != 0){
			// Add padding to last block
			byte[] copyBytes = new byte[mod];
			System.arraycopy(plainBytes, blockSize*lengthByte, copyBytes, 0, mod);
			copyBytes = paddingBytes(copyBytes);
			blockBytes.add(copyBytes);
		}
		
		return blockBytes;
	}
	
	private byte[] paddingBytes(byte[] bytes){
		byte[] copyBytes = new byte[lengthByte];
		if(bytes.length < lengthByte){
			System.arraycopy(bytes, 0, copyBytes, 0, bytes.length);
			for(int i = bytes.length; i < lengthByte; ++i){
				copyBytes[i] = 0;
			}
		}
		return copyBytes;
	}
	
	private byte[] paddingKeyBytes(byte[] bytes){
		byte[] copyBytes = null;
		
		if(bytes.length == 16 || bytes.length == 24 || bytes.length == 32){
			// No need to add padding
			copyBytes = new byte[bytes.length];
			System.arraycopy(bytes, 0, copyBytes, 0, bytes.length);
		}else{
			int lengthBytes = -1;
			if(bytes.length < 16)
				lengthBytes = 16;
			else if(bytes.length > 16 && bytes.length < 24)
				lengthBytes = 24;
			else if(bytes.length > 24 && bytes.length < 32)
				lengthBytes = 32;
			
			if(lengthBytes != -1){
				// Add padding
				copyBytes = new byte[lengthBytes];
				System.arraycopy(bytes, 0, copyBytes, 0, bytes.length);
				for(int i = bytes.length; i < lengthBytes; ++i){
					copyBytes[i] = 0;
				}
			}
		}
		return copyBytes;
	}
	
	/*final byte[][] MDS = {
			{0x01, (byte) 0xEF, 0x5B, 0x5B},
			{0x5B, (byte) 0xEF, (byte) 0xEF, 0x01},
			{(byte) 0xEF, 0x5B, 0x01, (byte) 0xEF},
			{(byte) 0xEF, 0x01, (byte) 0xEF, 0x5B}
	};
	
	final byte[][] MDSInvers = {
			{(byte) 0x8F, 0x0A, 0x3B, 0x32},
			{0x08, 0x3B, 0x3E, 0x2C},
			{0x73, 0x32, 0x2C,  (byte)0xA0},
			{0x44, (byte) 0x8F, 0x08, 0x73}
	};
	
	byte a = (byte) 0x01;
	byte b = (byte) 0xEF;
	byte c = (byte) 0x5B;
	
	byte detA = (byte)((a*b*a*c) + (a*b*b*a) + (a*a*c*b)
			+ (b*c*b*b) + (b*b*b*c) + (b*a*a*b)
			+ (c*c*c*c) + (c*b*b*b) + (c*a*b*a)
			+ (c*c*a*a) + (c*b*b*b) + (c*b*c*b)
			- (a*b*b*b) - (a*b*c*c) - (a*a*a*a)
			- (b*c*a*c) - (b*b*b*b) - (b*a*b*b)
			- (c*c*b*a) - (c*b*b*c) - (c*a*c*b)
			- (c*c*c*b) - (c*b*a*b) - (c*b*b*a));
	byte factor = (byte) (1/detA);
	/*byte b11 = (byte)((MDS[2][2]*MDS[3][3]*MDS[4][4] + MDS[2][3]*MDS[3][4]*MDS[4][2] + MDS[2][4]*MDS[3][2]*MDS[4][3]
			- MDS[2][2]*MDS[3][4]*MDS[4][3] - MDS[2][3]*MDS[3][2]*MDS[4][4] - MDS[2][4]*MDS[3][3]*MDS[4][2]) / detA);
	
	byte b12 = (byte)((MDS[1][2]*MDS[3][4]*MDS[4][3] + MDS[1][3]*MDS[3][2]*MDS[4][4] + MDS[1][4]*MDS[3][3]*MDS[4][2]
			- MDS[1][2]*MDS[3][3]*MDS[4][4] - MDS[1][3]*MDS[3][4]*MDS[4][2] - MDS[1][4]*MDS[3][2]*MDS[4][3]) / detA);
	
	byte b13 = (byte)((MDS[1][2]*MDS[2][3]*MDS[4][4] + MDS[1][3]*MDS[2][4]*MDS[4][2] + MDS[1][4]*MDS[2][2]*MDS[4][3]
			- MDS[1][2]*MDS[2][4]*MDS[4][3] - MDS[1][3]*MDS[2][2]*MDS[4][4] - MDS[1][4]*MDS[2][3]*MDS[4][2]) / detA);
	
	byte b14 = (byte)((MDS[1][2]*MDS[2][4]*MDS[3][3] + MDS[1][3]*MDS[2][2]*MDS[3][4] + MDS[1][4]*MDS[2][3]*MDS[3][2]
			- MDS[1][2]*MDS[2][3]*MDS[3][4] - MDS[1][3]*MDS[2][4]*MDS[3][2] - MDS[1][4]*MDS[2][2]*MDS[3][3]) / detA);
	
	byte b21 = (byte)((MDS[2][1]*MDS[3][4]*MDS[4][3] + MDS[2][3]*MDS[3][1]*MDS[4][4] + MDS[2][4]*MDS[3][3]*MDS[4][1]
			- MDS[2][1]*MDS[3][3]*MDS[4][4] - MDS[2][3]*MDS[3][4]*MDS[4][1] - MDS[2][4]*MDS[3][1]*MDS[4][3]) / detA);
	
	byte b22 = (byte)((MDS[1][1]*MDS[3][3]*MDS[4][4] + MDS[1][3]*MDS[3][4]*MDS[4][1] + MDS[1][4]*MDS[3][1]*MDS[4][3]
			- MDS[1][1]*MDS[3][4]*MDS[4][3] - MDS[1][3]*MDS[3][1]*MDS[4][4] - MDS[1][4]*MDS[3][3]*MDS[4][1]) / detA);
	
	byte b23 = (byte)((MDS[1][1]*MDS[2][4]*MDS[4][3] + MDS[1][3]*MDS[2][1]*MDS[4][4] + MDS[1][4]*MDS[2][3]*MDS[4][1]
			- MDS[1][1]*MDS[2][3]*MDS[4][4] - MDS[1][3]*MDS[2][4]*MDS[4][1] - MDS[1][4]*MDS[2][1]*MDS[4][3]) / detA);
	
	byte b24 = (byte)((MDS[1][1]*MDS[2][3]*MDS[3][4] + MDS[1][3]*MDS[2][4]*MDS[3][1] + MDS[1][4]*MDS[2][1]*MDS[3][3]
			- MDS[1][1]*MDS[2][4]*MDS[3][3] - MDS[1][3]*MDS[2][1]*MDS[3][4] - MDS[1][4]*MDS[2][3]*MDS[3][1]) / detA);
	
	byte b31 = (byte)((MDS[2][1]*MDS[3][2]*MDS[4][4] + MDS[2][2]*MDS[3][4]*MDS[4][1] + MDS[2][4]*MDS[3][1]*MDS[4][2]
			- MDS[2][1]*MDS[3][4]*MDS[4][2] - MDS[2][2]*MDS[3][1]*MDS[4][4] - MDS[2][4]*MDS[3][2]*MDS[4][1]) / detA);
	
	byte b32 = (byte)((MDS[1][1]*MDS[3][4]*MDS[4][2] + MDS[1][2]*MDS[3][1]*MDS[4][4] + MDS[1][4]*MDS[3][2]*MDS[4][1]
			- MDS[1][1]*MDS[3][2]*MDS[4][4] - MDS[1][2]*MDS[3][4]*MDS[4][1] - MDS[1][4]*MDS[3][1]*MDS[4][2]) / detA);
	
	byte b33 = (byte)((MDS[1][1]*MDS[2][2]*MDS[4][4] + MDS[1][2]*MDS[2][4]*MDS[4][1] + MDS[1][4]*MDS[2][1]*MDS[4][2]
			- MDS[1][1]*MDS[2][4]*MDS[4][2] - MDS[1][2]*MDS[2][1]*MDS[4][4] - MDS[1][4]*MDS[2][2]*MDS[4][1]) / detA);
	
	byte b34 = (byte)((MDS[1][1]*MDS[2][4]*MDS[3][2] + MDS[1][2]*MDS[2][1]*MDS[3][4] + MDS[1][4]*MDS[2][2]*MDS[3][1]
			- MDS[1][1]*MDS[2][2]*MDS[3][4] - MDS[1][2]*MDS[2][4]*MDS[3][1] - MDS[1][4]*MDS[2][1]*MDS[3][2]) / detA);
	
	byte b41 = (byte)((MDS[2][1]*MDS[3][3]*MDS[4][2] + MDS[2][2]*MDS[3][1]*MDS[4][3] + MDS[2][3]*MDS[3][2]*MDS[4][1]
			- MDS[2][1]*MDS[3][2]*MDS[4][3] - MDS[2][2]*MDS[3][3]*MDS[4][1] - MDS[2][3]*MDS[3][1]*MDS[4][2]) / detA);
	
	byte b42 = (byte)((MDS[1][1]*MDS[3][2]*MDS[4][3] + MDS[1][2]*MDS[3][3]*MDS[4][1] + MDS[1][3]*MDS[3][1]*MDS[4][2]
			- MDS[1][1]*MDS[3][3]*MDS[4][2] - MDS[1][2]*MDS[3][1]*MDS[4][3] - MDS[1][3]*MDS[3][2]*MDS[4][1]) / detA);
	
	byte b43 = (byte)((MDS[1][1]*MDS[2][3]*MDS[4][2] + MDS[1][2]*MDS[2][1]*MDS[4][3] + MDS[1][3]*MDS[2][2]*MDS[4][1]
			- MDS[1][1]*MDS[2][2]*MDS[4][3] - MDS[1][2]*MDS[2][3]*MDS[4][1] - MDS[1][3]*MDS[2][1]*MDS[4][2]) / detA);
	
	byte b44 = (byte)((MDS[1][1]*MDS[2][2]*MDS[3][3] + MDS[1][2]*MDS[2][3]*MDS[3][1] + MDS[1][3]*MDS[2][1]*MDS[3][2]
			- MDS[1][1]*MDS[2][3]*MDS[3][2] - MDS[1][2]*MDS[2][1]*MDS[3][3] - MDS[1][3]*MDS[2][2]*MDS[3][1]) / detA);
	
	Log.d(TwoFishSMSApp.TAG, "b11 = " + b11 + " " + Integer.toHexString(b11 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b12 = " + b12 + " " + Integer.toHexString(b12 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b13 = " + b13 + " " + Integer.toHexString(b13 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b14 = " + b14 + " " + Integer.toHexString(b14 & 0xFF)); 
	
	Log.d(TwoFishSMSApp.TAG, "b21 = " + b21 + " " + Integer.toHexString(b21 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b22 = " + b22 + " " + Integer.toHexString(b22 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b23 = " + b23 + " " + Integer.toHexString(b23 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b24 = " + b24 + " " + Integer.toHexString(b24 & 0xFF)); 
	
	Log.d(TwoFishSMSApp.TAG, "b31 = " + b31 + " " + Integer.toHexString(b31 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b32 = " + b32 + " " + Integer.toHexString(b32 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b33 = " + b33 + " " + Integer.toHexString(b33 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b34 = " + b34 + " " + Integer.toHexString(b34 & 0xFF)); 
	
	Log.d(TwoFishSMSApp.TAG, "b41 = " + b41 + " " + Integer.toHexString(b41 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b42 = " + b42 + " " + Integer.toHexString(b42 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b43 = " + b43 + " " + Integer.toHexString(b43 & 0xFF)); 
	Log.d(TwoFishSMSApp.TAG, "b44 = " + b44 + " " + Integer.toHexString(b44 & 0xFF)); 
	
	
	byte[] x = {0x04, 0x07, 0x10, 0x44};
	byte[] z = new byte[4];
	for(int j = 0; j < 4; ++j){
		byte result = 0;
		for(int k = 0; k < 4; k++){
			result += MDS[j][k] * x[k];
		}
		z[j] = result;
	}
	
	for(int i = 0; i < z.length; ++i){
		Log.d(TwoFishSMSApp.TAG, "z" + i + " = " + z[i] + " " + Integer.toHexString(z[i] & 0xFF));  
	}
	
	byte[] y = new byte[4];
	for(int j = 0; j < 4; ++j){
		byte result = 0;
		for(int k = 0; k < 4; k++){
			result += MDSInvers[j][k] * z[k];
		}
		y[j] = result;
	}
	
	for(int i = 0; i < y.length; ++i){
		Log.d(TwoFishSMSApp.TAG, "y" + i + " = " + y[i] + " " + Integer.toHexString(y[i] & 0xFF));  
	}*/
	
}
