package edu.itb.twofishsms.view;

import edu.itb.twofishsms.R;
import edu.itb.twofishsms.provider.Message;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class DialogKey extends Dialog implements View.OnClickListener{
	
	public interface OnDialogClickListener{
		public void onYesClick(String key, Message message, int position);
		public void onNoClick();
	}
	
	OnDialogClickListener mCallback;
	
	EditText keyEditText;
	
	private Message message;
	private int position;
	
    public DialogKey(Context context){
    	super(context);
    	setCancelable(false);
    	
    	mCallback = (OnDialogClickListener) context;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_key);
        getWindow().getDecorView().setBackgroundResource(
                android.R.color.transparent);

        TextView titleTv = (TextView) findViewById(R.id.dialog_title);
        titleTv.setText("Input Key");

        keyEditText = (EditText) findViewById(R.id.dialog_key_edittext);
        LayoutParams keyEditTextParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        keyEditTextParam.setMargins(50, 40, 50, 40);
        keyEditText.setLayoutParams(keyEditTextParam);
        keyEditText.setHint("Input key");
        
        Button noButton = (Button) findViewById(R.id.dialog_no_button);
        noButton.setTag("no");
        noButton.setOnClickListener(this);
			
        Button yesButton = (Button) findViewById(R.id.dialog_yes_button);
        yesButton.setTag("yes");
        yesButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		String tag = (String) v.getTag();
		if(tag.equalsIgnoreCase("no")){
			if(mCallback != null)
				mCallback.onNoClick();
		}else if(tag.equalsIgnoreCase("yes")){
			if(mCallback != null){
				String key = keyEditText.getText().toString();
				if(!key.isEmpty())
					mCallback.onYesClick(key, message, position);
			}
				
		}
	}
	
	public void setData(Message _message, int _position){
		this.message = _message;
		this.position = _position;
	}
}
