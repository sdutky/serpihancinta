package edu.itb.twofishsms.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import edu.itb.twofishsms.provider.Contact;

public class ContactUtil {
	
	public static ArrayList<Contact> getAllContacts(Context context){
		ArrayList<Contact> result = new ArrayList<Contact>();
		
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
		
		if (cur.getCount() > 0) {
		    while (cur.moveToNext()) {
		        String id = cur.getString(
	                        cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(
	                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		 		if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
		 			Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		 		 		    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
		 		 		    new String[]{id}, null);
 		 	        while (pCur.moveToNext()) {
 		 	        	String mobileNumber = pCur.getString(pCur
 		 	        	            		.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
 		 	        	Contact contact = new Contact(name, mobileNumber);
 		 	        	result.add(contact);
 		 	        } 
 		 	        pCur.close();
	 	        }
            }
	 	}
		cur.close();
		return result;
	}

}
