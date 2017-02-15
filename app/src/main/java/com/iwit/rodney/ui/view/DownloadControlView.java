package com.iwit.rodney.ui.view;

import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.iwit.rodney.R;

public class DownloadControlView extends View {
	public static final int STATE_READY_TO_DOWNLOAD = 0;
	public static final int STATE_READY_TO_PAUSE = 1;
	public static final int STATE_READY_TO_RESUME = 2;
	public static final int STATE_READY_TO_PLAY = 3;
	private static WeakHashMap<Integer, Drawable> dws = new WeakHashMap<Integer, Drawable>(
			4);

	public DownloadControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setState(STATE_READY_TO_DOWNLOAD);
	}

	public void setState(int state) {
		checkState(state);
		Drawable d = dws.get(state);
		if (d == null) {
			d = getStateDrawable(state);
		}
		setBackgroundDrawable(d);
		dws.put(state, d);
	}

	private Drawable getStateDrawable(int state) {
		int resId = 0;
		switch (state) {
		case STATE_READY_TO_DOWNLOAD:
			resId = R.drawable.download_book;
			break;
		case STATE_READY_TO_PAUSE:
			resId = R.drawable.pause_book;
			break;
		case STATE_READY_TO_RESUME:
			resId = R.drawable.resume_book;
			break;
		case STATE_READY_TO_PLAY:
			resId = R.drawable.play_book;
			break;
		}
		return getResources().getDrawable(resId);
	}

	private void checkState(int state) {
		if (state < STATE_READY_TO_DOWNLOAD || state > STATE_READY_TO_PLAY) {
			throw new IllegalArgumentException();
		}
	}

}
