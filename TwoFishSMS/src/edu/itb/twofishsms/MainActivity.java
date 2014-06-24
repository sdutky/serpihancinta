package edu.itb.twofishsms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import edu.itb.twofishsms.adapter.RecipientThreadAdapter;
import edu.itb.twofishsms.constant.IntentKey;
import edu.itb.twofishsms.model.RecipientThread;
import edu.itb.twofishsms.provider.Contact;
import edu.itb.twofishsms.provider.Recipient;
import edu.itb.twofishsms.util.ContactUtil;
import edu.itb.twofishsms.util.DatabaseUtil;

public class MainActivity extends Activity {
	
	private ListView recipientThreadListView;
	private RecipientThreadAdapter recipientThreadAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			// Get all contact
			new GetContactThread().start();
			
			// Init view
			init();
		}
	}
	
	public void onResume(){
		super.onResume();
		
		// Update recipient thread list
		recipientThreadAdapter.setItemList(DatabaseUtil.getRecipientThreadDatabase(getApplicationContext()));
		recipientThreadAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
	    	if(requestCode == IntentKey.ComposeMessageRequest){
	    		// Show multiple sending toast
	    		Toast.makeText(getApplicationContext(), "Multiple sending message", Toast.LENGTH_SHORT).show();
	    	}
	        
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}
	
	public void init(){
		// Init compose button
		ImageButton composeButton = (ImageButton) findViewById(R.id.main_compose_button);
		composeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				redirectToComposePage(true, null);
			}
		});
		
		// Init recipient thread list view
		recipientThreadListView = (ListView) findViewById(R.id.main_recipient_listview);
		recipientThreadAdapter = new RecipientThreadAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, DatabaseUtil.getRecipientThreadDatabase(getApplicationContext()));
		recipientThreadListView.setAdapter(recipientThreadAdapter);
		recipientThreadListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Recipient recipient = recipientThreadAdapter.getItemList().get(position).getRecipient();
				redirectToComposePage(false, recipient);
			}
		});
		recipientThreadListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				RecipientThread recipientThread = recipientThreadAdapter.getItemList().get(position);
				showThreadDialog(recipientThread);
				return false;
			}
		});
	}
	
	final CharSequence[] dialogThreadItems = {"Delete threads"};
	
	public void showThreadDialog(final RecipientThread recipientThread){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(recipientThread.getRecipient().getName());
        builder.setItems(dialogThreadItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                	// Delete threads
                	deleteThread(recipientThread);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	public void deleteThread(RecipientThread recipientThread){
		// Delete recipient thread in database
		DatabaseUtil.deleteRecipientThreadDatabase(getApplicationContext(), recipientThread);
		
		// Update recipient thread data in listview
		List<RecipientThread> recipientThreadList = DatabaseUtil.getRecipientThreadDatabase(getApplicationContext());
		recipientThreadAdapter.setItemList(recipientThreadList);
		recipientThreadAdapter.notifyDataSetChanged();
	}
	
	public void redirectToComposePage(boolean newMessage, Recipient recipient){
		Intent intent = new Intent(this, ComposeMessageActivity.class);
		intent.putExtra(IntentKey.NewMessage, newMessage);
		intent.putExtra(IntentKey.Recipient, recipient);
		startActivityForResult(intent, IntentKey.ComposeMessageRequest);
	}
	
	public class GetContactThread extends Thread {
	
		public GetContactThread(){}
		
		@Override
		public void run() {
			Calendar cal = Calendar.getInstance();
			Date versionDate = cal.getTime();
			ArrayList<Contact> contactList = ContactUtil.getAllContacts(getApplicationContext());
			DatabaseUtil.updateContactRecordDatabase(getApplicationContext(), contactList, versionDate);
		}
	}

}
