package com.iwit.rodney.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iwit.rodney.R;

public class TimeProgressTextView extends TextView {
	/**
	 * total time in second
	 */
	private int totalTime;
	/**
	 * current time in second
	 */
	private int currentTime;

	private int progressBarHeight = 10;
	private Paint paint;

	public static final SimpleDateFormat format = new SimpleDateFormat("mm:ss",
			Locale.CHINA);

	public TimeProgressTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressBarHeight = getResources().getDimensionPixelSize(
				R.dimen.width_height_record_progress);
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.record_progress));
		paint.setStrokeWidth(progressBarHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (totalTime <= 0) {
			return;
		}
		float proWidth = getWidth() * currentTime / totalTime;
		float lineHeight = getHeight() - (progressBarHeight * 0.5f);
		canvas.drawLine(0, lineHeight, proWidth, lineHeight, paint);
	}

	/**
	 * 设置时间，以秒为单位。如果total小于等于0，则不显示进度条。
	 */
	public void setTimes(int total, int current) {
		totalTime = total;
		currentTime = current;
		String result = format.format(new Date(current * 1000));
		setText(result);
	}
}
