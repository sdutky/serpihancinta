package edu.itb.twofishsms;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.itb.twofishsms.adapter.ContactAdapter;
import edu.itb.twofishsms.adapter.MessageAdapter;
import edu.itb.twofishsms.constant.IntentKey;
import edu.itb.twofishsms.provider.Contact;
import edu.itb.twofishsms.provider.Message;
import edu.itb.twofishsms.provider.Recipient;
import edu.itb.twofishsms.util.DatabaseUtil;
import edu.itb.twofishsms.view.DialogKey;
import edu.itb.twofishsms.view.DialogKey.OnDialogClickListener;

public class ComposeMessageActivity extends Activity implements OnDialogClickListener {
	
	private Button sendButton;
	private Button encryptButton;
	private EditText recipientEditText;
	private EditText keyEditText;
	private EditText messageEditText;
	
	private ContactAdapter contactAdapter;
	private ListView contactListView;
	
	private TextView titleTextView;
	private ListView messageListView;
	private static MessageAdapter messageAdapter;
	private LinearLayout composeMessageLayout;
	private LinearLayout composeRecipientLayout;
	private LinearLayout recipientSelectedlayout;
	private DialogKey dialogKey;
	
	private static Recipient recipient = null;
	private static boolean newMessage;
		
	private ArrayList<Recipient> recipientSelectedList = new ArrayList<Recipient>();
	private int counterSMSReceived = 0;
	private BroadcastReceiver receiver;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_message);
        
        // Init view
        init();        
    }
	
	public void onDestroy(){
		super.onDestroy();
		dialogKey.dismiss();
		if(receiver != null)
			unregisterReceiver(receiver);
	}
	
	public void onResume(){
		super.onResume();
		
		// Set visiblilty of message page
		TwoFishSMSApp.setMessagePageVisible(true);
		
		// Update message list view
		updateMessageListView();
	}
	
	public void onPause(){
		super.onPause();
		
		// Set visiblilty of message page
		TwoFishSMSApp.setMessagePageVisible(false);
	}
	
	public void onBackPressed(){
		if(contactListView.getVisibility() == View.VISIBLE){
			composeMessageLayout.setVisibility(View.VISIBLE);
			contactListView.setVisibility(View.GONE);
		}else
			super.onBackPressed();
	}
	
	private void initSMSReceiver(String key){
		receiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            int status = Message.FAILED;
	            switch (getResultCode()) {
		            case Activity.RESULT_OK:
					status = Message.SUCCESS;
		                break;
		            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
		            case SmsManager.RESULT_ERROR_NO_SERVICE:
					break;
		            case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
		            case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
            	}
	            Message msg = intent.getParcelableExtra(IntentKey.Message);
	            if(msg != null){
	            	Log.d(TwoFishSMSApp.TAG, "SMS result : " + msg.getId() + " " + msg.getMessage());
	            	// Update message status in database
	            	DatabaseUtil.updateMessageStatusToDatabase(getApplicationContext(), msg, status);
	            	
	            	// Update message list view
	            	updateMessageListView(msg.getMobileNumber());
	            }
	            
	            // Handle finish activity action for multiple sending message
	            if(recipientSelectedList.size() > 1){
	            	counterSMSReceived++;
		            if(counterSMSReceived == recipientSelectedList.size()){
		            	Log.d(TwoFishSMSApp.TAG, "Multiple sending message");
		            	ComposeMessageActivity.this.setResult(RESULT_OK);
		            	finish();
		            }
	            }
	            
            }
        };
		registerReceiver(receiver, new IntentFilter(key));
	}
	
	private void init(){
		// Init dialog key
		dialogKey = new DialogKey(this);
		
		// Init compose message layout
		composeMessageLayout = (LinearLayout) findViewById(R.id.compose_message_layout);
		
		// Init recipient selected layout
		recipientSelectedlayout = (LinearLayout) findViewById(R.id.compose_recipient_selected_layout);
		
		// Init message list view
		messageListView = (ListView) findViewById(R.id.compose_message_listview);
		
		messageAdapter = new MessageAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<Message>());
		messageListView.setAdapter(messageAdapter);
		messageListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				// Show message dialog
				Message message = messageAdapter.getItemList().get(position);
				showMessageDialog(message, position);
				
				return false;
			}
		});
		
		// Init recipient edit text
		recipientEditText = (EditText) findViewById(R.id.compose_recipient_edittext);
		recipientEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().isEmpty()){
					contactListView.setVisibility(View.GONE);
					composeMessageLayout.setVisibility(View.VISIBLE);
					
				}else{
					contactAdapter.getFilter().filter(s);
					contactListView.setVisibility(View.VISIBLE);
					composeMessageLayout.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Init contact list view
		contactListView = (ListView) findViewById(R.id.compose_contact_listview);
		contactAdapter = new ContactAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<Contact>());
		contactAdapter.setAllItemList(DatabaseUtil.getContactRecordDatabase(getApplicationContext()));
		contactListView.setAdapter(contactAdapter);
		
		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Contact contact = contactAdapter.getItemList().get(position);
				Recipient recipient = new Recipient(contact.getName(), contact.getMobileNumber());
				if(!isExistRecipient(recipient)){
					recipientSelectedList.add(recipient);
					
					// Add button to selected recipient layout
					Button recipientButton = new Button(ComposeMessageActivity.this);
					LayoutParams recipientButtonParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					recipientButton.setText(recipient.getName());
					recipientButton.setTextSize(12.0f);
					recipientButton.setLayoutParams(recipientButtonParam);
					recipientButton.setTag(recipient);
					recipientButton.setOnClickListener(new OnClickListener() {	
						@Override
						public void onClick(View v) {
							Recipient recipient = (Recipient) v.getTag();
							if(recipient != null){
								showSelectedRecipientDialog(recipient);
							}
						}
					});
					recipientSelectedlayout.addView(recipientButton);
				}
			}
		});
		
		// Init send button
		sendButton = (Button) findViewById(R.id.compose_send_button);
		sendButton.setEnabled(false);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = messageEditText.getText().toString();
				if(recipientSelectedList.size() > 0){
					sendSMS(message);
				}else{
					if(!recipientEditText.getText().toString().isEmpty()){
						sendSMS(message);
					}
				}
			}
		});
		
		// Init encrypt button
		encryptButton = (Button) findViewById(R.id.compose_encrypt_button);
		encryptButton.setEnabled(false);
		encryptButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String cookieText = messageEditText.getText().toString();
				String keyText = keyEditText.getText().toString();
				try {
					String cipherText = TwoFishSMSApp.encrypt(cookieText, keyText);
					messageEditText.setText(cipherText);
				} catch (InvalidKeyException e) {
					// Show an error
					Toast.makeText(getApplicationContext(), "Key invalid", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					Toast.makeText(getApplicationContext(), "Encoding error occured", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		
		// Init key edittext
		keyEditText = (EditText) findViewById(R.id.compose_key_edittext);
		keyEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().length() > 0)
					encryptButton.setEnabled(true);
				else
					encryptButton.setEnabled(false);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Init message edit text
		messageEditText = (EditText) findViewById(R.id.compose_message_edittext);
		messageEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().length() > 0)
					sendButton.setEnabled(true);
				else
					sendButton.setEnabled(false);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		// Init compose recipient layout
		composeRecipientLayout = (LinearLayout) findViewById(R.id.compose_recipient_layout);
		titleTextView = (TextView) findViewById(R.id.compose_message_title);
		
		// Get intent extra from main activity
		newMessage = getIntent().getExtras().getBoolean(IntentKey.NewMessage, true);
		if(newMessage){
			composeRecipientLayout.setVisibility(View.VISIBLE);
		}else{
			composeRecipientLayout.setVisibility(View.GONE);
			
			recipient = getIntent().getExtras().getParcelable(IntentKey.Recipient);
			if(recipient != null){
				recipientSelectedList.add(recipient);
				
				// Set title text
				setTitle(recipient.getName(), recipient.getMobileNumber());
				
				// Update message
				updateMessageListView();
				messageListView.setSelection(messageAdapter.getCount() - 1);
			}
		}
	}
	
	private void sendSMS(String message){
		messageEditText.setText("");
		counterSMSReceived = 0;
		
		if(recipientSelectedList.size() == 0){
			String number = recipientEditText.getText().toString();
			
			 if(number.matches("\\d+")){
				 Log.d(TwoFishSMSApp.TAG, "number valid");
				 Recipient recipient = new Recipient("", number);
				 recipientSelectedList.add(recipient);
			 }else{
				 Toast.makeText(getApplicationContext(), "Number invalid", Toast.LENGTH_SHORT).show();
			 }
		}
		
		for(int i = 0; i < recipientSelectedList.size(); ++i){
			Recipient recipient = recipientSelectedList.get(i);
			// Insert message to database
			Message msg = new Message(message, recipient.getName(), recipient.getMobileNumber(), Message.PENDING, Message.OUTGOING);
			DatabaseUtil.insertMessageToDatabase(getApplicationContext(), msg);
			
			// Init sms receiver
			initSMSReceiver(Integer.toString(msg.getId()));	
			
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(msg.getMobileNumber(), null, msg.getMessage(), PendingIntent.getBroadcast(
                    this, 0, new Intent(Integer.toString(msg.getId())).putExtra(IntentKey.Message, msg), 0), null);
	       
		}
		
		if(recipientSelectedList.size() == 1){
			Recipient recipientSelected = recipientSelectedList.get(0);
			newMessage = false;
			recipient = new Recipient(recipientSelected);
			
			// Update message list
			updateMessageListView();
			messageListView.setSelection(messageAdapter.getCount() - 1);
			
			composeRecipientLayout.setVisibility(View.GONE);
			setTitle(recipient.getName(), recipient.getMobileNumber());
			
		}
	}
	
	private void resendMessage(Message message){
		for(int i = 0; i < recipientSelectedList.size(); ++i){
			// Init sms receiver
			initSMSReceiver(Integer.toString(message.getId()));	
			
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(message.getMobileNumber(), null, message.getMessage(), PendingIntent.getBroadcast(
                    this, 0, new Intent(Integer.toString(message.getId())).putExtra(IntentKey.Message, message), 0), null);
	       
		}
		
	}
	
	final CharSequence[] dialogSelectedRecipientItems = {"Remove", "View Contact"};
	
	private void showSelectedRecipientDialog(final Recipient recipient){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(recipient.getName() + " (" + recipient.getMobileNumber() + ")" );
        builder.setItems(dialogSelectedRecipientItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                	// Remove selected recipient
                	removeSelectedRecipient(recipient);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void removeSelectedRecipient(Recipient recipient){
		Log.d(TwoFishSMSApp.TAG, "Remove selected recipient");
		int position = -1;
		int i = 0;
		while(i < recipientSelectedList.size() && position == -1){
			Recipient recipientSelected = recipientSelectedList.get(i);
			if(recipientSelected.getMobileNumber().equalsIgnoreCase(recipient.getMobileNumber())){
				position = i;
			}
			++i;
		}
		
		if(position != -1){
			Log.d(TwoFishSMSApp.TAG, "Selected recipient position = " + position);
			recipientSelectedList.remove(position);
			recipientSelectedlayout.removeViewAt(position);
		}
	}
	
	private void showMessageDialog(final Message message, final int position){
		CharSequence[] dialogMessageItems = {"Delete message", "Decrypt", "Resend message"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message options");
        builder.setItems(dialogMessageItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                	// Delete message
                	deleteMessage(message);
                }else if(item == 1){
                	// Show key dialog
                	dialogKey.setData(message, position);
                	dialogKey.show();
                }else if(item == 2){
                	// Resend message
                	resendMessage(message);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void deleteMessage(Message message){
		// Delete message in database
		DatabaseUtil.deleteMessageRecordDatabase(getApplicationContext(), message);
		
		// Update message data in listview
		updateMessageListView(message.getMobileNumber());
	}
	
	private boolean isExistRecipient(Recipient recipient){
		boolean found = false;
		int i = 0;
		while(i < recipientSelectedList.size() && !found){
			Recipient recipientSelected = recipientSelectedList.get(i);
			if(recipientSelected.getMobileNumber().equalsIgnoreCase(recipient.getMobileNumber())){
				found = true;
			}
			++i;
		}
		return found;
	}
	
	public void setTitle(String name, String mobileNumber){
		String title = "";
		if(!name.isEmpty())
			title = name + " (" + mobileNumber + ")";
		else
			title = mobileNumber;
		titleTextView.setText(title);
	}
	
	public static void updateMessageListView(String mobileNumber){
		messageAdapter.setItemList(DatabaseUtil.getMessageRecordDatabase(TwoFishSMSApp.getContext(), mobileNumber));
		messageAdapter.notifyDataSetChanged();
	}
	
	public static void updateMessageListView(){
		if(recipient != null && !newMessage){
			messageAdapter.setItemList(DatabaseUtil.getMessageRecordDatabase(TwoFishSMSApp.getContext(), recipient.getMobileNumber()));
			messageAdapter.notifyDataSetChanged();
		}
	}

	// Interface OnDialogClick Listener
	@Override
	public void onYesClick(String key, Message message, int position) {
		// Decrypt message
    	try {
			String plainText = TwoFishSMSApp.decrypt(message.getMessage(), key);
			messageAdapter.getItemList().get(position).setMessage(plainText);;
			messageAdapter.notifyDataSetChanged();
			dialogKey.hide();
		} catch (InvalidKeyException e) {
			// Show an error
			Toast.makeText(getApplicationContext(), "Key invalid", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(getApplicationContext(), "Encoding error occured", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onNoClick() {
		dialogKey.hide();
	}
	
	/* Test Case
	 * String message1 = null;
		String message2 = null;
		String message3 = null;
		try {
			message1 = TwoFishSMSApp.encrypt("nur adi", "tes");
			message2 = TwoFishSMSApp.encrypt("susliawan", "yudhis");
			message3 = TwoFishSMSApp.encrypt("dwi caksono", "susliawan");
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Message messageTest1 = new Message(message1, "tes", "0812", Message.SUCCESS, Message.INCOMING);
		Message messageTest2 = new Message(message2, "tes", "0812", Message.SUCCESS, Message.INCOMING);
		Message messageTest3 = new Message(message3, "tes", "0812", Message.SUCCESS, Message.INCOMING);
		ArrayList<Message> messages = new ArrayList<Message>();
		messages.add(messageTest1);
		messages.add(messageTest2);
		messages.add(messageTest3);
	 * 
	 * 
	 */
}
