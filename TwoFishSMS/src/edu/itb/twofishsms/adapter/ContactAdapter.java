package edu.itb.twofishsms.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import edu.itb.twofishsms.R;
import edu.itb.twofishsms.provider.Contact;

public class ContactAdapter extends ArrayAdapter<Contact> {
	
	private LayoutInflater inflater;
	private List<Contact> itemList;
    private List<Contact> allItemList;
   
    private Filter filter = new Filter() {		
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if(results != null){	
				itemList = (List<Contact>) results.values;
				
				if (itemList.size() > 0){
					notifyDataSetChanged();
				}else{
					notifyDataSetInvalidated();
				}
			}
		}
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();   
	        ArrayList<Contact> tempList = new ArrayList<Contact>();
	        // Constraint is the result from text you want to filter against. 
	        // Objects is your data set you will filter from
	        if(constraint != null && allItemList != null) {
	        	if(!constraint.toString().isEmpty()){
	        		int length = allItemList.size();
		        	int i = 0;
	            	while(i < length){
	                    Contact item = allItemList.get(i);     
	                    if(item.getName().toLowerCase().
	                    		contains(constraint.toString().toLowerCase())
                    		|| item.getMobileNumber().contains(constraint.toString())){
	                    	tempList.add(item);
	                    }
	                    i++;
	            	}
	        	}
	        	
	            //Following two lines is very important
	            //as publish result can only take FilterResults objects
	            filterResults.values = tempList;
	            filterResults.count = tempList.size();
	        }
      		return filterResults;  
		}
	};
    
    public ContactAdapter(Context context, int textViewResourceId,
			List<Contact> objects) {
		super(context, textViewResourceId, objects);
		this.itemList = objects;
		this.allItemList = objects;
	    this.inflater = LayoutInflater.from(context);
	}
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_item,
                    parent, false);
        }

    	Contact item = itemList.get(position);
    	
    	TextView tvName = (TextView) convertView
                .findViewById(R.id.contact_item_name);
        tvName.setText(item.getName());

        TextView tvNumber = (TextView) convertView
        		.findViewById(R.id.contact_item_number);
        tvNumber.setText(item.getMobileNumber());
        	
        return convertView;
    }
    
    @Override
    public Filter getFilter() {
    	return filter;
    }
    
    @Override
    public int getCount(){
		return itemList.size();
    }
    
    public List<Contact> getItemList(){
    	return this.itemList;
    }
    
    public List<Contact> getAllItemList(){
    	return this.allItemList;
    }
    
    public void setItemList(List<Contact> _allItemList){
    	this.allItemList = _allItemList;
    	this.itemList = _allItemList;
    }
    
    public void setAllItemList(List<Contact> _allItemList){
    	this.allItemList = _allItemList;
    }
}
