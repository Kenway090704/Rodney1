package com.iwit.rodney.comm.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap sourceBitmap) {
		
		try {
			Bitmap targetBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
					sourceBitmap.getHeight(), Config.ARGB_8888);
			// 得到画布
			Canvas canvas = new Canvas(targetBitmap);
			// 创建画笔
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			// 值越大角度越明显
			float roundPx = 20;
			float roundPy = 20;
			Rect rect = new Rect(0, 0, sourceBitmap.getWidth(),
					sourceBitmap.getHeight());
			RectF rectF = new RectF(rect);
			// 绘制
			canvas.drawARGB(0, 0, 0, 0);
			canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(sourceBitmap, rect, rect, paint);
			return targetBitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
