package edu.itb.twofishsms;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import edu.itb.twofishsms.provider.DataProvider;
import gnu.crypto.cipher.Twofish;

public class TwoFishSMSApp extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Application is starting");
		
		context = getApplicationContext();
			
		// Check database upgrade version
		DataProvider.mOpenHelper.getReadableDatabase();
		
	}
	
	public static String TAG = "TwoFishSMS";
	
	private static Context context;
	
	private static boolean recipientThreadPageVisible = false;
	private static boolean messagePageVisible = false;
	
	public static Context getContext(){
		return context;
	}
	
	public static boolean isRecipientThreadPageVisible(){
		return recipientThreadPageVisible;
	}
	
	public static void setRecipientThreadPageVisible(boolean value){
		recipientThreadPageVisible = value;
	}
	
	public static boolean isMessagePageVisible(){
		return messagePageVisible;
	}
	
	public static void setMessagePageVisible(boolean value){
		messagePageVisible = value;
	}
	
	public static boolean isSameNumber(String number1, String number2){
		if(number1.contains("+") || number2.contains("+")){
			if(number1.contains("+") && !number2.contains("+")){
				String chunk1 = number1.substring(3, number1.length());
				String chunk2 = number2.substring(1, number2.length());
				Log.d(TAG, "chunk 1 = " + chunk1 + " chunk 2 = " + chunk2);
				return chunk1.equalsIgnoreCase(chunk2);
			}else if (!number1.contains("+") && number2.contains("+")){
				String chunk1 = number1.substring(1, number1.length());
				String chunk2 = number2.substring(3, number2.length());
				Log.d(TAG, "chunk 1 = " + chunk1 + " chunk 2 = " + chunk2);
				return chunk1.equalsIgnoreCase(chunk2);
			}
		}
		return (number1.equalsIgnoreCase(number2));
	}
	
	public static String encrypt (String cookieValue, String key) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] plainText;
		byte[] encryptedText;
		Twofish twofish = new Twofish();
		// Create a key
		byte[] keyBytes = key.getBytes();
		keyBytes = paddingKeyBytes(keyBytes);
		Object keyObject = twofish.makeKey(keyBytes, 16);
		// Make the length of the text a multiple of the block size
		if ((cookieValue.length() % 16) != 0) {
			while ((cookieValue.length() % 16) != 0) {
				cookieValue += " ";
			}
		}
		// Initialize byte arrays for plain/encrypted text
		plainText = cookieValue.getBytes("UTF8");
		encryptedText = new byte[cookieValue.length()];
		// Encrypt text in 8-byte chunks
		for (int i=0; i<Array.getLength(plainText); i+=16) {
			twofish.encrypt(plainText, i, encryptedText, i, keyObject, 16);
		}
		String encryptedString = Base64Coder.encodeLines(encryptedText);
		return encryptedString;
	}
	
	public static String decrypt (String cookieValue, String key) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] encryptedText;
		byte[] decryptedText;
		Twofish twofish = new Twofish();
		// Create the key
		byte[] keyBytes = key.getBytes();
		keyBytes = paddingKeyBytes(keyBytes);
		Object keyObject = twofish.makeKey(keyBytes, 16);
		// Make the length of the string a multiple of
		// the block size
		if ((cookieValue.length() % 16) != 0) {
			while ((cookieValue.length() % 16) != 0) {
				cookieValue += " ";
			}
		}
		// Initialize byte arrays that will hold encrypted/decrypted
		// text
		encryptedText = Base64Coder.decodeLines(cookieValue);
		decryptedText = new byte[cookieValue.length()];
		// Iterate over the byte arrays by 16-byte blocks and decrypt.
		for (int i=0; i<Array.getLength(encryptedText); i+=16) {
			twofish.decrypt(encryptedText, i, decryptedText, i, keyObject, 16);
		}
		String decryptedString = new String(decryptedText, "UTF8");
		return decryptedString;
	}
	
	private static byte[] paddingKeyBytes(byte[] bytes){
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
		
}
