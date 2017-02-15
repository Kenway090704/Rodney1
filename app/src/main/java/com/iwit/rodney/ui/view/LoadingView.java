package com.iwit.rodney.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LoadingView extends View {
	// private View processView;
	private int process;
	private Paint paint;

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// if (!isInEditMode()) {
		// setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.loading_bar));
		// }
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
	}

	// @Override
	// protected void onLayout(boolean changed, int l, int t, int r, int b) {
	// super.onLayout(changed, l, t, r, b);
	// if (changed) {
	// if (processView == null) {
	// processView = new View(getContext());
	// addView(processView);
	// }
	// int width = getWidth() * process / 100;
	// processView.setLayoutParams(new RelativeLayout.LayoutParams(width,
	// getHeight()));
	// processView.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.process));
	// }
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		float radius = height / 2;
		paint.setStrokeWidth(height);
		paint.setColor(0xFFA28978);
		canvas.drawLine(radius, radius, width - radius, radius, paint);
		int processWidth = width * process / 100;
		if (processWidth < 2 * radius) {
			processWidth = (int) (2 * radius + 1);
		}
		paint.setColor(0xFF66CD0B);
		canvas.drawLine(radius, radius, processWidth - radius, radius, paint);
	}

	/**
	 * 设置进度，进度值应该在0-100之间(包括0和100)。
	 * 
	 * @param process
	 *            进度值
	 */
	public void setProgress(int process) {
		checkProcess(process);
		this.process = process;
		invalidate();
	}

	private void checkProcess(int process) {
		if (process < 0 || process > 100) {
			throw new IllegalArgumentException();
		}
	}

}
