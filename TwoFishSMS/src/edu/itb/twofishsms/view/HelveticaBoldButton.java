package edu.itb.twofishsms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class HelveticaBoldButton extends Button {
	
	public HelveticaBoldButton(Context context) {
		super(context);
		CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Bold.otf", context);
	}
	
	public HelveticaBoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Bold.otf", context);
    }

    public HelveticaBoldButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Bold.otf", context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }

}
