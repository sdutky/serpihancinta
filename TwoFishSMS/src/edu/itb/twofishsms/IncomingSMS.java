package edu.itb.twofishsms;

import edu.itb.twofishsms.provider.Message;
import edu.itb.twofishsms.util.DatabaseUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSMS extends BroadcastReceiver {

	// Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		 // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
             
            if (bundle != null) {
                 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                 
                for (int i = 0; i < pdusObj.length; i++) {
                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     
                    String senderNum = phoneNumber;
                    String messageContent = currentMessage.getDisplayMessageBody();
 
                    Log.i(TwoFishSMSApp.TAG, "senderNum: "+ senderNum + "; message: " + messageContent);
                    
                    // Insert incoming message to database
                    Message message = new Message(messageContent, "", senderNum, Message.SUCCESS, Message.INCOMING);
                    DatabaseUtil.insertMessageToDatabase(context, message);
                    
                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, 
                                 "New message from "+ senderNum, duration);
                    toast.show();
                } 
                
                // Update page
                if(TwoFishSMSApp.isRecipientThreadPageVisible())
                	MainActivity.updateRecipientThreadListView();
                
                if(TwoFishSMSApp.isMessagePageVisible())
                	ComposeMessageActivity.updateMessageListView();
            } 
        } catch (Exception e) {
            Log.e(TwoFishSMSApp.TAG, "Exception smsReceiver : " + e);
        }
	}

}
