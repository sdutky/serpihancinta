package edu.itb.twofishsms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EditText;

public class HelveticaRegularEditText extends EditText {

	public HelveticaRegularEditText(Context context) {
		super(context);
		CustomFontHelper.setCustomEditTextFont(this, "fonts/HelveticaNeue-Regular.otf", context);	
	}
	
	public HelveticaRegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomEditTextFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    public HelveticaRegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomEditTextFont(this, "fonts/HelveticaNeue-Regular.otf", context);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

    }

}
