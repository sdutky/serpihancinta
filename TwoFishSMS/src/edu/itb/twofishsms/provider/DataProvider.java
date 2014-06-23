package edu.itb.twofishsms.provider;

import edu.itb.twofishsms.TwoFishSMSApp;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DataProvider extends ContentProvider {

	public static SQLiteOpenHelper mOpenHelper;
	public static final String DB_NAME = "TwoFishSMSDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final int ContactTable = 0;
	private static final int ContactTable_ID = 1;
	private static final int MessageTable = 2;
	private static final int MessageTable_ID = 3;
	private static final int RecipientTable = 4;
	private static final int RecipientTable_ID = 5;

	private static UriMatcher sUriMatcher = null;
	
	public static class DatabaseHelper extends SQLiteOpenHelper {
		private Context mContext;
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
			mContext = context;
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TwoFishSMSApp.TAG, "Database onCreate");
			Contact.getCreatedScript(db, mContext);
			Message.getCreatedScript(db, mContext);
			Recipient.getCreatedScript(db, mContext);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case ContactTable:
				count = db.delete(Contact.TABLE_NAME, whereClause, whereArgs);
			break;
			case ContactTable_ID:
				String ContactIdID = uri.getPathSegments().get(1);
				count = db.delete(Contact.TABLE_NAME, Contact.Columns._ID + "=?", new String[]{ ContactIdID });
			break;
			case MessageTable:
				count = db.delete(Message.TABLE_NAME, whereClause, whereArgs);
			break;
			case MessageTable_ID:
				String MessageIdID = uri.getPathSegments().get(1);
				count = db.delete(Message.TABLE_NAME, Message.Columns._ID + "=?", new String[]{ MessageIdID });
			break;
			case RecipientTable:
				count = db.delete(Recipient.TABLE_NAME, whereClause, whereArgs);
			break;
			case RecipientTable_ID:
				String RecipientIdID = uri.getPathSegments().get(1);
				count = db.delete(Recipient.TABLE_NAME, Recipient.Columns._ID + "=?", new String[]{ RecipientIdID });
			break;
			default:
				throw new IllegalArgumentException("Unknown URI - delete" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case ContactTable:
				return Contact.CONTENT_TYPE;
			case ContactTable_ID:
				return Contact.CONTENT_ITEM_TYPE;
			case MessageTable:
				return Message.CONTENT_TYPE;
			case MessageTable_ID:
				return Message.CONTENT_ITEM_TYPE;
			case RecipientTable:
				return Recipient.CONTENT_TYPE;
			case RecipientTable_ID:
				return Recipient.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI - getType" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = -1;
		switch (sUriMatcher.match(uri)) {
			case ContactTable:
				rowId = db.insert(Contact.TABLE_NAME, null, values);
				if (rowId > 0) {
					Uri starUri = ContentUris.withAppendedId(Contact.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(starUri, null);
					return starUri;
				}
			break;
			case MessageTable:
				rowId = db.insert(Message.TABLE_NAME, null, values);
				if (rowId > 0) {
					Uri starUri = ContentUris.withAppendedId(Message.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(starUri, null);
					return starUri;
				}
			break;
			case RecipientTable:
				rowId = db.insert(Recipient.TABLE_NAME, null, values);
				if (rowId > 0) {
					Uri starUri = ContentUris.withAppendedId(Recipient.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(starUri, null);
					return starUri;
				}
			break;
			default:
				throw new IllegalArgumentException("Unknown URI  - insert " + uri);
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
			case ContactTable:
				qb.setTables(Contact.TABLE_NAME);
			break;
			case ContactTable_ID:
				qb.setTables(Contact.TABLE_NAME);
				qb.appendWhere(Contact.Columns._ID + "=" + uri.getPathSegments().get(1));
			break;
			case MessageTable:
				qb.setTables(Message.TABLE_NAME);
			break;
			case MessageTable_ID:
				qb.setTables(Message.TABLE_NAME);
				qb.appendWhere(Message.Columns._ID + "=" + uri.getPathSegments().get(1));
			break;
			case RecipientTable:
				qb.setTables(Recipient.TABLE_NAME);
			break;
			case RecipientTable_ID:
				qb.setTables(Recipient.TABLE_NAME);
				qb.appendWhere(Recipient.Columns._ID + "=" + uri.getPathSegments().get(1));
			break;
		}
		
		// Get the database and run the query
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		// Tell the cursor what uri to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
			case ContactTable_ID:
				final String ContactId  = uri.getPathSegments().get(1);
				return db.update(Contact.TABLE_NAME, values, Contact.Columns._ID + "=" + ContactId, null);
			case MessageTable_ID:
				final String MessageId  = uri.getPathSegments().get(1);
				return db.update(Message.TABLE_NAME, values, Message.Columns._ID + "=" + MessageId, null);
			case RecipientTable_ID:
				final String RecipientId  = uri.getPathSegments().get(1);
				return db.update(Recipient.TABLE_NAME, values, Recipient.Columns._ID + "=" + RecipientId, null);
			default:
				throw new IllegalArgumentException("Unknown URI  - update " + uri);
		}
	}
	
	public static SQLiteOpenHelper getDatabaseOpenHelper(){
		return mOpenHelper;
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Contact.AUTHORITY, Contact.TABLE_NAME, ContactTable);
		sUriMatcher.addURI(Contact.AUTHORITY, Contact.TABLE_NAME + "/#", ContactTable_ID);
		sUriMatcher.addURI(Message.AUTHORITY, Message.TABLE_NAME, MessageTable);
		sUriMatcher.addURI(Message.AUTHORITY, Message.TABLE_NAME + "/#", MessageTable_ID);
		sUriMatcher.addURI(Recipient.AUTHORITY, Recipient.TABLE_NAME, RecipientTable);
		sUriMatcher.addURI(Recipient.AUTHORITY, Recipient.TABLE_NAME + "/#", RecipientTable_ID);
	}
}