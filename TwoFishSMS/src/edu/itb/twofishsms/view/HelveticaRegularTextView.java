package edu.itb.twofishsms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class HelveticaRegularTextView extends TextView {
	
	public HelveticaRegularTextView(Context context) {
		super(context);
		CustomFontHelper.setCustomTextViewFont(this, "fonts/HelveticaNeue-Regular.otf", context);
	}
	
	public HelveticaRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomTextViewFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    public HelveticaRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomTextViewFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }

}
