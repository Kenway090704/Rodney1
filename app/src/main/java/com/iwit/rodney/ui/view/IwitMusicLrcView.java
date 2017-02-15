package com.iwit.rodney.ui.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.iwit.rodney.entity.Lyric;
import com.iwit.rodney.interfaces.OnCallBack;

public class IwitMusicLrcView extends View {
	private final String TAG = this.getClass().getSimpleName();
	public static final float HEIGHT_MAGGINTOP = 0.25f;
	private boolean mIsSeekingLrc;
	private float mLrcStartMovingY;
	private Lyric mLrc;
	private Paint mPaint;
	private float LrcY;
	private float centerX;
	private float textSize;
	private float textHeight;
	private int indexCur;
	public IwitMusicLrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Align.CENTER);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	/**
	 * 每行的间距 再加上一个渐变的 alph 值
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		final int width = getWidth();
		final int height = getHeight();
		float startHeight = height * HEIGHT_MAGGINTOP;
		centerX = width * 0.5f;
		LrcY = height - startHeight;
		// draw lyric
		if (mLrc == null) {
			clear(canvas);
			return;
		}
		if(!mIsSeekingLrc){
			indexCur = mLrc.getIndexByTimestamp(timestamp) - 1;
		}
		textSize = startHeight * 0.2f;
		textHeight = textSize + 8;
		
		mPaint.setShadowLayer(4, 0, 0, Color.argb(255, 122, 122, 122));
		String cs = null;
		int index = 0;
		float centerY = LrcY * 0.6f;
		while (true) {
			cs = mLrc.getLrcByIndex(index);
			if (cs == null) {
				break;
			}
			if (indexCur == index) {
				mPaint.setTextSize(textSize + 10);
				mPaint.setColor(Color.WHITE);
				mPaint.setAlpha(255);
				canvas.drawText(cs, centerX, centerY + textHeight
						* (index - indexCur) + 12, mPaint);
			} else if (index < indexCur) {
				mPaint.setTextSize(textSize);
				mPaint.setColor(Color.WHITE);
				mPaint.setAlpha(120);
				canvas.drawText(cs, centerX, centerY + textHeight
						* (index - indexCur), mPaint);
			} else {
				mPaint.setTextSize(textSize);
				mPaint.setColor(Color.WHITE);
				mPaint.setAlpha(120);
				canvas.drawText(cs, centerX, centerY + textHeight
						* (index - indexCur) + 14, mPaint);
			}
			index++;
		}
	}

	public void clear(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAlpha(0);
		canvas.drawPaint(paint);
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onLrc(event);
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(event);
			break;
		case MotionEvent.ACTION_UP:
			onUp(event);
			break;
		}
		invalidate();
		return true;
	}

	private void onUp(MotionEvent event) {
		if (!mIsSeekingLrc || mLrc == null) {
			return;
		}
		if(indexCur < 0){
			indexCur = 0;
		}
		long time = mLrc.getTimestampByIndex(indexCur);
		Log.v(TAG, "time::" + time);
		onCallBack.back(time);
		mIsSeekingLrc = false;
	}

	private void onMove(MotionEvent event) {
		if (!mIsSeekingLrc) {
			return;
		}
		float yMoved = event.getY() - mLrcStartMovingY;
		scrollLrc(yMoved);
	}
	//这个滑动时一格格的
	private void scrollLrc(float yMoved) {
		// 歌词滑动ymoved 个行距 ymoved
		if(mLrc == null){
			return;
		}
		// 找到index 边界处理
		int textDis = (int) (yMoved / textSize);
		if (textDis != 0) {
			Log.v(TAG, "textDis::" + textDis);
			int curIndex = indexCur - textDis;
			Log.v(TAG, "curIndex::" + curIndex);
			if (curIndex > mLrc.getTotalIndex() -1) {
				curIndex = mLrc.getTotalIndex() - 1;
			} else if (curIndex < 0) {
				curIndex = 0;
			}
			if(curIndex < 0){
				return;
			}
			indexCur = curIndex;
			// 找到对应的index
			mLrcStartMovingY = mLrcStartMovingY + yMoved;
			invalidate();
		}
	}

	private void onLrc(MotionEvent event) {
		mIsSeekingLrc = true;
		mLrcStartMovingY = event.getY();
	}

	/**
	 * 选择要播放的文件。(因为此方法由第三方调用，所以加入检查判断的代码。但是按照面向对象的思维，此功能应该属于音乐播放器的控制面板。这样的话，
	 * 检查判断的代码就可以省去。)
	 * 
	 * @param file
	 */
	private OnCallBack onCallBack;

	public void setLrc(String path, ImageView iv, OnCallBack onCallBack) {
		this.onCallBack = onCallBack;
		File file = new File(path);
		if (file.exists()) {
			mLrc = Lyric.getByFile(file);
			iv.setVisibility(View.VISIBLE);
		} else {
			mLrc = null;
			iv.setVisibility(View.GONE);
		}
		invalidate();
	}

	private long timestamp;

	public void onProcessChanged(long timestamp) {
		this.timestamp = timestamp;
		if (mLrc != null) {
			invalidate();
		}
	}

}
