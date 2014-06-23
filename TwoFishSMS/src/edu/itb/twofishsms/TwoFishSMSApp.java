package edu.itb.twofishsms;

import java.util.ArrayList;

import android.app.Application;
import android.util.Log;
import edu.itb.twofishsms.provider.Contact;
import edu.itb.twofishsms.provider.DataProvider;

public class TwoFishSMSApp extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Application is starting");
		
		// Check database upgrade version
		DataProvider.mOpenHelper.getReadableDatabase();
	}
	
	public static String TAG = "TwoFishSMS";
	
}
