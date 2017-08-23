package com.shuai.base.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * see http://stackoverflow.com/questions/5375838/scrollview-disable-focus-move
 */
public class NonFocusingScrollView extends ScrollView {
	
	public NonFocusingScrollView(Context context) {
		super(context);
	}

	public NonFocusingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonFocusingScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction,
			Rect previouslyFocusedRect) {
		return true;
	}
}
