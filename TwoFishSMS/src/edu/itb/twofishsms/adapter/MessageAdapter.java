package edu.itb.twofishsms.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import edu.itb.twofishsms.R;
import edu.itb.twofishsms.TwoFishSMSApp;
import edu.itb.twofishsms.provider.Message;
import edu.itb.twofishsms.util.ViewUtil;

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
		
		RelativeLayout messageLayout = (RelativeLayout) convertView
				.findViewById(R.id.message_item_layout);
		if(item.getSent() == Message.OUTGOING){
			LayoutParams messageLayoutParam = (LayoutParams) messageLayout.getLayoutParams();
			messageLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			messageLayoutParam.leftMargin = (int) ViewUtil.convertDpToPixel(80.f, getContext());
			messageLayoutParam.rightMargin = (int) ViewUtil.convertDpToPixel(20.f, getContext());
			messageLayout.setBackground(getContext().getResources().getDrawable(R.drawable.blue_rounded_corner));
			messageLayout.setLayoutParams(messageLayoutParam);
		}else if(item.getSent() == Message.INCOMING){
			LayoutParams messageLayoutParam = (LayoutParams) messageLayout.getLayoutParams();
			messageLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			messageLayoutParam.leftMargin = (int) ViewUtil.convertDpToPixel(20.f, getContext());
			messageLayoutParam.rightMargin = (int) ViewUtil.convertDpToPixel(80.f, getContext());
			messageLayout.setBackground(getContext().getResources().getDrawable(R.drawable.red_rounded_corner));
			messageLayout.setLayoutParams(messageLayoutParam);
		}
		
		TextView tvMessage = (TextView) convertView
				.findViewById(R.id.message_item_message);
		tvMessage.setText(item.getMessage());
		
		TextView tvStatus = (TextView) convertView
				.findViewById(R.id.message_item_status);
		
		if(item.getStatus() == Message.FAILED){
			tvStatus.setTextColor(Color.parseColor("#ff0000"));
			tvStatus.setText("Failed");
		}else{
			tvStatus.setTextColor(Color.parseColor("#000000"));
			if(item.getStatus() == Message.SUCCESS){
				tvStatus.setText(item.getModified());
			}else if(item.getStatus() == Message.PENDING){
				tvStatus.setText("Pending");
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
