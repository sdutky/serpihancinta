package edu.itb.twofishsms.provider;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.util.Log;
import edu.itb.twofishsms.TwoFishSMSApp;

public class Message extends DbTable{
	
	public static final String TABLE_NAME = "Message";
	public static final String PACKAGE = "twofishsms.provider";
	public static final String AUTHORITY = "edu.itb." + PACKAGE;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + PACKAGE + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + PACKAGE + "." + TABLE_NAME;

	public static final int SUCCESS = 2;
	public static final int FAILED = 3;
	public static final int PENDING = 4;
	
	public static final int OUTGOING = 1;
	public static final int INCOMING = 0;
	
	public int _id;
	public String created;
	public String modified;
	private String message;
	private int sent;
	private String name;
	private String mobileNumber;
	private int status;
	
	public Message(){}
	
	public Message(String _message, String _name, String _mobileNumber, int _status, int _sent){
		this._id = new Random().nextInt(Integer.MAX_VALUE) + 1;
		Log.d(TwoFishSMSApp.TAG, "random : " + this._id);
		this.message = _message;
		this.sent = _sent;
		this.name = _name;
		this.mobileNumber = _mobileNumber;
		this.status = _status;
	}
	
	public Message(Cursor c){
		_id = c.getInt(c.getColumnIndex(Columns._ID));
		created = c.getString(c.getColumnIndex(Columns.CREATED));
		modified = c.getString(c.getColumnIndex(Columns.MODIFIED));
		message = c.getString(c.getColumnIndex(Columns.MESSAGE));
		sent = c.getInt(c.getColumnIndex(Columns.SENT));
		name = c.getString(c.getColumnIndex(Columns.NAME));
		mobileNumber = c.getString(c.getColumnIndex(Columns.MOBILENUMBER));
		status = c.getInt(c.getColumnIndex(Columns.STATUS));
	}
	
	public Message(JSONObject j){
		try {
			created = j.getString("created");
			modified = j.getString("modified");
			message = j.getString("message");
			sent = j.getInt("sent");
			name = j.getString("name");
			mobileNumber = j.getString("mobileNumber");
			status = j.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Message(Parcel p){
		_id = p.readInt();
		created = p.readString();
		modified = p.readString();
		message = p.readString();
		sent = p.readInt();
		name = p.readString();
		mobileNumber = p.readString();
		status = p.readInt();
	}
	
	public static class Columns implements BaseColumns {
		private Columns() {}
		public static final String CREATED = "created";
		public static final String MODIFIED = "modified";
		public static final String MESSAGE = "message";
		public static final String SENT = "sent";
		public static final String NAME = "name";
		public static final String MOBILENUMBER = "number";
		public static final String STATUS = "status";
	}
	
	public static final void getCreatedScript( SQLiteDatabase db, Context context ) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Message.TABLE_NAME + " (" +
			Columns._ID + " INTEGER PRIMARY KEY," +
			Columns.CREATED + " INTEGER," + 
			Columns.MODIFIED + " INTEGER, " +
			Columns.MESSAGE + " TEXT, " +
			Columns.SENT + " INTEGER, " +
			Columns.NAME + " TEXT, " +
			Columns.MOBILENUMBER + " TEXT, " +
			Columns.STATUS + " INTEGER " +
		");");
	}
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(Columns._ID, _id);
		values.put(Columns.CREATED, created);
		values.put(Columns.MODIFIED, modified);
		values.put(Columns.MESSAGE, message);
		values.put(Columns.SENT, sent);
		values.put(Columns.NAME, name);
		values.put(Columns.MOBILENUMBER, mobileNumber);
		values.put(Columns.STATUS, status);
		return values;
	}
	
	public static String[] getStringArray( ContentValues values ){
		String[] array =  {
			values.getAsString(String.valueOf(Columns._ID)),
			values.getAsString(Columns.CREATED),
			values.getAsString(Columns.MODIFIED),
			values.getAsString(Columns.MESSAGE),
			values.getAsString(Columns.SENT),
			values.getAsString(Columns.NAME),
			values.getAsString(Columns.MOBILENUMBER),
			values.getAsString(Columns.STATUS)
		};
		return array;
	};
	
	@Override
	public int addRecord( Context context ) {
		try {
			if( validate( context ) ){
				final String now = getNowDate();
				created = now;
				modified = now;
				context.getContentResolver().insert(Message.CONTENT_URI, getContentValues());
				return DATA_SAVED;
			} else {
				return DATA_INVALID;
			}
		} catch (Exception e){
			return DATA_ERROR;
		}
	}
	
	@Override
	public int updateRecord( Context context ) {
		try {
			if( validate( context ) ){
				final String now = getNowDate();
				modified = now;
				final Uri bookmarkUri = Uri.withAppendedPath( Message.CONTENT_URI, String.valueOf( _id) );
				context.getContentResolver().update( bookmarkUri, getContentValues(), null,null);
				return DATA_SAVED;
			} else {
				return DATA_INVALID;
			}
		} catch (Exception e){
			return DATA_ERROR;
		}
	}
	
	@Override
	public boolean validate( Context context ) {
		return true;
	}
	
	public int getId(){
		return this._id;
	}
	
	public String getCreated(){
		return this.created;
	}
	
	public String getModified(){
		return this.modified;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public int getSent(){
		return this.sent;
	}
	
	public String getMobileNumber(){
		return this.mobileNumber;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public void setStatus(int _status){
		this.status = _status;
	}
	
	public void setName(String _name){
		this.name = _name;
	}
	
	public void setMobileNumber(String _mobileNumber){
		this.mobileNumber = _mobileNumber;
	}
	
	public void setMessage(String _message){
		this.message = _message;
	}
	
	/**
	*  Parcelable related
	**/ 

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
		public Message createFromParcel(Parcel p) {
			return new Message(p);
		}
	
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(created);
		dest.writeString(modified);
		dest.writeString(message);
		dest.writeInt(sent);
		dest.writeString(name);
		dest.writeString(mobileNumber);
		dest.writeInt(status);
	}

}
