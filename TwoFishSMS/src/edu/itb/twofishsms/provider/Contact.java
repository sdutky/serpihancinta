package edu.itb.twofishsms.provider;

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

public class Contact extends DbTable {
	public static final String TABLE_NAME = "Contact";
	public static final String PACKAGE = "twofishsms.provider";
	public static final String AUTHORITY = "edu.itb." + PACKAGE;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + PACKAGE + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + PACKAGE + "." + TABLE_NAME;

	public int _id;
	public String created;
	public String modified;
	private String name;
	private String mobileNumber;
	private String version;
	
	public Contact(){}
	
	public Contact(String _name, String _mobileNumber){
		this.name = _name;
		this.mobileNumber = _mobileNumber;
	}
	
	public Contact(Cursor c){
		_id = c.getInt(c.getColumnIndex(Columns._ID));
		created = c.getString(c.getColumnIndex(Columns.CREATED));
		modified = c.getString(c.getColumnIndex(Columns.MODIFIED));
		name = c.getString(c.getColumnIndex(Columns.NAME));
		mobileNumber = c.getString(c.getColumnIndex(Columns.MOBILENUMBER));
		version = c.getString(c.getColumnIndex(Columns.VERSION));
	}
	
	public Contact(JSONObject j){
		try {
			created = j.getString("created");
			modified = j.getString("modified");
			name = j.getString("name");
			mobileNumber = j.getString("mobileNumber");
			version = j.getString("version");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Contact(Parcel p){
		_id = p.readInt();
		created = p.readString();
		modified = p.readString();
		name = p.readString();
		mobileNumber = p.readString();
		version = p.readString();
	}
	
	public static class Columns implements BaseColumns {
		private Columns() {}
		public static final String CREATED = "created";
		public static final String MODIFIED = "modified";
		public static final String NAME = "name";
		public static final String MOBILENUMBER = "number";
		public static final String VERSION = "version";
	}
	
	public static final void getCreatedScript( SQLiteDatabase db, Context context ) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Contact.TABLE_NAME + " (" +
			Columns._ID + " INTEGER PRIMARY KEY," +
			Columns.CREATED + " INTEGER," + 
			Columns.MODIFIED + " INTEGER, " +
			Columns.NAME + " TEXT, " +
			Columns.MOBILENUMBER + " TEXT, " +
			Columns.VERSION + " TEXT " +
		");");
	}
	
	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		if( _id > 0 ){
			values.put(Columns._ID, _id);
		}
		values.put(Columns.CREATED, created);
		values.put(Columns.MODIFIED, modified);
		values.put(Columns.NAME, name);
		values.put(Columns.MOBILENUMBER, mobileNumber);
		values.put(Columns.VERSION, version);
		return values;
	}
	
	public static String[] getStringArray( ContentValues values ){
		String[] array =  {
			values.getAsString(String.valueOf(Columns._ID)),
			values.getAsString(Columns.CREATED),
			values.getAsString(Columns.MODIFIED),
			values.getAsString(Columns.NAME),
			values.getAsString(Columns.MOBILENUMBER),
			values.getAsString(Columns.VERSION)
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
				context.getContentResolver().insert(Contact.CONTENT_URI, getContentValues());
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
				final Uri bookmarkUri = Uri.withAppendedPath( Contact.CONTENT_URI, String.valueOf( _id) );
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
	
	public String getName(){
		return this.name;
	}
	
	public String getMobileNumber(){
		return this.mobileNumber;
	}
	
	public void setName(String _name){
		this.name = _name;
	}
	
	public void setVersion(String _version){
		this.version = _version;
	}
	
	/**
	*  Parcelable related
	**/ 

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel p) {
			return new Contact(p);
		}
	
		public Contact[] newArray(int size) {
			return new Contact[size];
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
		dest.writeString(name);
		dest.writeString(mobileNumber);
		dest.writeString(version);
	}

}
