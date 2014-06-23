package edu.itb.twofishsms.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.itb.twofishsms.R;
import edu.itb.twofishsms.model.RecipientThread;

public class RecipientThreadAdapter extends ArrayAdapter<RecipientThread> {
	
	private LayoutInflater inflater;
	private List<RecipientThread> itemList;
	
	 public RecipientThreadAdapter(Context context, int textViewResourceId,
				List<RecipientThread> objects) {
		super(context, textViewResourceId, objects);
		this.itemList = objects;
	    this.inflater = LayoutInflater.from(context);
	}
	 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	        convertView = inflater.inflate(R.layout.recipient_thread_item,
	                parent, false);
	    }
	
		RecipientThread item = itemList.get(position);
		
		TextView tvRecipient = (TextView) convertView
	            .findViewById(R.id.recipient_thread_item_recipient);
	    String recipientString = "";
	    if(!item.getRecipient().getName().isEmpty()){
	    	recipientString += item.getRecipient().getName() + " (" + item.getRecipient().getMobileNumber() + ")";
	    }else{
	    	recipientString += item.getRecipient().getMobileNumber();
	    }
		tvRecipient.setText(recipientString);
	    
	    TextView tvLastMessage = (TextView) convertView
	    		.findViewById(R.id.recipient_thread_item_last_message);
	    tvLastMessage.setText(item.getLastMessage().getMessage());
	    	
	    return convertView;
	}
	 
	 @Override
	 public int getCount(){
		return itemList.size();
	 }
    
	 public List<RecipientThread> getItemList(){
    	return this.itemList;
	 }
	 
	 public void setItemList(List<RecipientThread> _itemList){
    	this.itemList = _itemList;
	 }
}
