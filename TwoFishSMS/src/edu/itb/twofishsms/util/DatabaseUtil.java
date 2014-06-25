package edu.itb.twofishsms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import edu.itb.twofishsms.TwoFishSMSApp;
import edu.itb.twofishsms.comparator.MessageComparator;
import edu.itb.twofishsms.comparator.RecipientThreadComparator;
import edu.itb.twofishsms.model.RecipientThread;
import edu.itb.twofishsms.provider.Contact;
import edu.itb.twofishsms.provider.Message;
import edu.itb.twofishsms.provider.Recipient;

public class DatabaseUtil {
	
	/****************************************************************************
	 * 	CONTACT DATABASE OPERATION
	 ****************************************************************************/
	public static void updateContactRecordDatabase(Context context, ArrayList<Contact> contactList, Date versionDate){
		Uri contactURL = Contact.CONTENT_URI;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String version = sdf.format(versionDate);
		
		for(Contact contact : contactList){
			Cursor c = context.getContentResolver().query(contactURL, null, 
					Contact.Columns.MOBILENUMBER + "=?", 
					new String [] { contact.getMobileNumber() }, 
					null);
			
			if(c.moveToFirst()){
				Contact dbContact = new Contact(c);
				if(!dbContact.getName().equals(contact.getName()) && !contact.getName().isEmpty()){
					dbContact.setVersion(version);
				}
				dbContact.setVersion(version);
				dbContact.updateRecord(context);
			} else {
				contact.setVersion(version);
				contact.addRecord(context);
			}
			if (c != null && !c.isClosed()) { c.close(); }
		}
		context.getContentResolver().delete(contactURL, Contact.Columns.VERSION + "!=?", new String [] { version });
	}
	
	public static List<Contact> getContactRecordDatabase(Context context){
		List<Contact> contacts = new ArrayList<Contact>();
		
		// Get all record from contact table on database
		Cursor c = context.getContentResolver().query(Contact.CONTENT_URI, null, 
				null, null, null);
		Log.d(TwoFishSMSApp.TAG, "Contact count = " + c.getCount());
		if (c.moveToFirst()) {
	        do {
	            Contact contact = new Contact(c);
	            // Adding contact to list
	            contacts.add(contact);
	        } while (c.moveToNext());
	    }
	    if (c != null && !c.isClosed()) { c.close(); }
	    
		return contacts;
	}
	
	/****************************************************************************
	 * 	MESSAGE DATABASE OPERATION
	 ****************************************************************************/
	public static void insertMessageToDatabase(Context context, Message message){
		
		// Get all record from recipient table on database
		Cursor c = context.getContentResolver().query(Recipient.CONTENT_URI, null, 
				null, null, null);
		
		Recipient recipient = new Recipient(message.getName(), message.getMobileNumber());
		if (c.moveToFirst()) {
	        boolean found = false;
			do {
	            Recipient dbRecipient = new Recipient(c);
	            if(TwoFishSMSApp.isSameNumber(dbRecipient.getMobileNumber(), message.getMobileNumber())){
	            	// Update message data
	            	message.setName(dbRecipient.getName());
	            	message.setMobileNumber(dbRecipient.getMobileNumber());
	            	found = true;
	            }
	        } while (c.moveToNext() && !found);
			
			if(!found){
				recipient.addRecord(context);
			}
	    }
	    if (c != null && !c.isClosed()) { c.close(); }
		
	    // Insert message to database
 		message.addRecord(context);
		
	}
	
	public static void updateMessageStatusToDatabase(Context context, Message message, int status){
		// Get record from message table by recipient
		Cursor c = context.getContentResolver().query(Message.CONTENT_URI, null, 
				Message.Columns.MOBILENUMBER + "=?", 
				new String [] { message.getMobileNumber() }, 
				null);
		if (c.moveToFirst()) {
	        do {
	            Message dbMessage = new Message(c);
	            if(dbMessage.getId() == message.getId()){
	            	dbMessage.setStatus(status);
	            	dbMessage.updateRecord(context);
	            }
	        } while (c.moveToNext());
	    }
		if (c != null && !c.isClosed()) { c.close(); }
	}
	
	public static List<Message> getMessageRecordDatabase(Context context, String mobileNumber){
		List<Message> messageList = new ArrayList<Message>();
		
		// Get record from message table by recipient
		Cursor c = context.getContentResolver().query(Message.CONTENT_URI, null, 
				Recipient.Columns.MOBILENUMBER + "=?", 
				new String [] { mobileNumber }, 
				null);
		if (c.moveToFirst()) {
	        do {
	            Message message = new Message(c);
	            // Adding message to list
	            messageList.add(message);
	        } while (c.moveToNext());
	    }
	    if (c != null && !c.isClosed()) { c.close(); }
	    
	    // Sort message list by modified date
	    Collections.sort(messageList, new MessageComparator());
	    
		return messageList;
	}
	
	public static void deleteMessageRecordDatabase(Context context, Message message){
		// Delete message in table message
		context.getContentResolver().delete(Message.CONTENT_URI, 
				Message.Columns._ID + "=?", 
				new String [] { Integer.toString(message.getId()) });
	}
	
	/****************************************************************************
	 * 	RECIPIENT THREAD DATABASE OPERATION
	 ****************************************************************************/
	public static List<RecipientThread> getRecipientThreadDatabase(Context context){
		List<RecipientThread> recipientThreadList = new ArrayList<RecipientThread>();
		
		// Get all record from recipient table on database
		Cursor c = context.getContentResolver().query(Recipient.CONTENT_URI, null, 
				null, null, null);
		if (c.moveToFirst()) {
	        do {
				ArrayList<Message> messageList = new ArrayList<Message>();
	            Recipient recipient = new Recipient(c);
	            // Get last message from certain recipient
	            Cursor cur = context.getContentResolver().query(Message.CONTENT_URI, null, 
	    				Message.Columns.MOBILENUMBER + "=?", 
	    				new String [] { recipient.getMobileNumber() }, 
	    				null);
	            if (cur.moveToFirst()) {
	            	do {
			            Message message = new Message(cur);
			            // Adding message to list
			            messageList.add(message);
			        } while (cur.moveToNext());
	            }
	            
	            if (cur != null && !cur.isClosed()) { cur.close(); }
	            
	            // Sort message list by modified date
	    	    Collections.sort(messageList, new MessageComparator());
	    	    
	    	    if(messageList.size() > 0){
	    	    	// Get last message
	    	    	Message message = messageList.get(messageList.size() - 1);
	    			RecipientThread recipientThread = new RecipientThread(recipient, message);
	    			recipientThreadList.add(recipientThread);
	    	    }
	   
	        } while (c.moveToNext());
	    }
	    if (c != null && !c.isClosed()) { c.close(); }
	    
	    // Sort recipient thread list by last message modified date
	    Collections.sort(recipientThreadList, new RecipientThreadComparator());
	    
		return recipientThreadList;
	}
	
	public static void deleteRecipientThreadDatabase(Context context, RecipientThread recipientThread){
		// Delete message in table message which is related with this recipient
		context.getContentResolver().delete(Message.CONTENT_URI, 
				Message.Columns.MOBILENUMBER + "=?", 
				new String [] { recipientThread.getRecipient().getMobileNumber() });
		
		// Delete recipient in recipient table
		context.getContentResolver().delete(Recipient.CONTENT_URI, 
				Recipient.Columns.MOBILENUMBER + "=?", 
				new String [] { recipientThread.getRecipient().getMobileNumber() });
	}
}
