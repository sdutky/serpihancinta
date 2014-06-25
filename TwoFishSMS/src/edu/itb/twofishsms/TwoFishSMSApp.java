package edu.itb.twofishsms;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import edu.itb.twofishsms.provider.DataProvider;

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
	
}
