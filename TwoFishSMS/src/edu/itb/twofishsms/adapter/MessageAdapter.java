package edu.itb.twofishsms.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.itb.twofishsms.R;
import edu.itb.twofishsms.provider.Message;

public class MessageAdapter extends ArrayAdapter<Message> {
	private LayoutInflater inflater;
	private List<Message> itemList;
	
	public MessageAdapter(Context context, int textViewResourceId,
				List<Message> objects) {
		super(context, textViewResourceId, objects);
		this.itemList = objects;
	    this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	        convertView = inflater.inflate(R.layout.message_item,
	                parent, false);
	    }
	
		Message item = itemList.get(position);
		
		RelativeLayout messageOutgoingLayout = (RelativeLayout) convertView
				.findViewById(R.id.message_item_outgoing_layout);
		
		RelativeLayout messageIncomingLayout = (RelativeLayout) convertView
				.findViewById(R.id.message_item_incoming_layout);
		
		if(item.getSent() == Message.OUTGOING){
			messageOutgoingLayout.setVisibility(View.VISIBLE);
			messageIncomingLayout.setVisibility(View.GONE);
		}else if(item.getSent() == Message.INCOMING){
			messageOutgoingLayout.setVisibility(View.GONE);
			messageIncomingLayout.setVisibility(View.VISIBLE);
		}
		
		TextView tvOutgoingMessage = (TextView) convertView
				.findViewById(R.id.message_item_outgoing_message);
		tvOutgoingMessage.setText(item.getMessage());
		
		TextView tvOutgoingStatus = (TextView) convertView
				.findViewById(R.id.message_item_outgoing_status);
		
		TextView tvIncomingMessage = (TextView) convertView
				.findViewById(R.id.message_item_incoming_message);
		tvIncomingMessage.setText(item.getMessage());
		
		TextView tvIncomingStatus = (TextView) convertView
				.findViewById(R.id.message_item_incoming_status);
		
		if(item.getStatus() == Message.FAILED){
			tvOutgoingStatus.setTextColor(Color.parseColor("#ff0000"));
			tvOutgoingStatus.setText("Failed");
			
			tvIncomingStatus.setTextColor(Color.parseColor("#ff0000"));
			tvIncomingStatus.setText("Failed");
		}else{
			tvOutgoingStatus.setTextColor(Color.parseColor("#000000"));
			tvIncomingStatus.setTextColor(Color.parseColor("#000000"));
			if(item.getStatus() == Message.SUCCESS){
				tvOutgoingStatus.setText(item.getModified());
				tvIncomingStatus.setText(item.getModified());
			}else if(item.getStatus() == Message.PENDING){
				tvOutgoingStatus.setText("Pending");
				tvIncomingStatus.setText("Pending");
			}
		}
		
	    return convertView;
	}
	
	@Override
	public int getCount(){
		return itemList.size();
	}
   
	public List<Message> getItemList(){
		 return this.itemList;
	}
	 
	public void setItemList(List<Message> _itemList){
		 this.itemList = _itemList;
	}
}
