package edu.itb.twofishsms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;


public class HelveticaRegularButton extends Button {

	public HelveticaRegularButton(Context context) {
		super(context);
		CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Regular.otf", context);
	}
	
	public HelveticaRegularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    public HelveticaRegularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomButtonFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }

}
