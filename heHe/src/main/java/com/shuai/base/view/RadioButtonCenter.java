package com.shuai.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import com.shuai.hehe.R;


public class RadioButtonCenter extends android.support.v7.widget.AppCompatRadioButton {

	public RadioButtonCenter(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CompoundButton, 0, 0);
		//buttonDrawable = a.getDrawable(1);
		setButtonDrawable(android.R.color.transparent);
		setButtonDrawable(null);
	}

	Drawable buttonDrawable;
	
	

	@Override
	public void setChecked(boolean checked) {
		//在LG G3 android5.0的机器上强制刷新一下，不然ui不更新
		if(isChecked()!=checked){
			invalidate();
		}
		super.setChecked(checked);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (buttonDrawable != null) {
			buttonDrawable.setState(getDrawableState());
			final int verticalGravity = getGravity()
					& Gravity.VERTICAL_GRAVITY_MASK;
			final int height = buttonDrawable.getIntrinsicHeight();

			int y = 0;

			switch (verticalGravity) {
			case Gravity.BOTTOM:
				y = getHeight() - height;
				break;
			case Gravity.CENTER_VERTICAL:
				y = (getHeight() - height) / 2;
				break;
			}

			int buttonWidth = buttonDrawable.getIntrinsicWidth();
			int buttonLeft = (getWidth() - buttonWidth) / 2;
			buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y
					+ height);
			buttonDrawable.draw(canvas);
		}
	}

}
