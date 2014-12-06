package com.example.urmlauncher;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

	private final GestureDetector gestureDetector;

	public OnSwipeTouchListener(Context ctxt) {
		gestureDetector = new GestureDetector(ctxt, new GestureListener());
	}

	private final class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d("e2X", String.valueOf(e2.getX()));
			Log.d("e1X", String.valueOf(e1.getX()));
			Log.d("velocityY", String.valueOf(velocityY));
			float diffY = e2.getY() - e1.getY();
			float diffX = e2.getX() - e1.getX();
			if (Math.abs(diffY) > Math.abs(diffX)) {
				if (Math.abs(diffY) > SWIPE_THRESHOLD) {
					if (diffY > 0) {
						onSwipeDown();
					} else {
						onSwipeUp();
					}
				}
			} else {
				if (Math.abs(diffX) > SWIPE_THRESHOLD) {
					if (diffX < 0) {
						onSwipeRight();
					} else {
						onSwipeLeft();
					}
				}
			}
			return true;
		}
	}

	public void onSwipeUp() {

	}

	public void onSwipeDown() {

	}

	public void onSwipeRight() {

	}

	public void onSwipeLeft() {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("", "onTouch");
		Log.d("", String.valueOf(gestureDetector.onTouchEvent(event)));
		return gestureDetector.onTouchEvent(event);
	}

}
