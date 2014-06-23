package edu.itb.twofishsms.provider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.os.Parcelable;

public abstract class DbTable implements Parcelable {
	public static final int DATA_SAVED = 1;
	public static final int DATA_ERROR = 2;
	public static final int DATA_INVALID = 3;


	abstract public int addRecord( Context context );
	abstract public int updateRecord( Context context );
	abstract public boolean validate( Context context );

	public String getNowDate(){
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
