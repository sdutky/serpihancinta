package edu.itb.twofishsms.view;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomFontHelper {
	
	private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();
	
	/**
     * Sets a font on a textview
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomTextViewFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = getTypeFace(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }
    
    public static void setCustomEditTextFont(EditText editText, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = getTypeFace(font, context);
        if(tf != null) {
            editText.setTypeface(tf);
        }
    }
    
    public static void setCustomButtonFont(Button button, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = getTypeFace(font, context);
        if(tf != null) {
            button.setTypeface(tf);
        }
    }
    
    public static Typeface getTypeFace(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

}
