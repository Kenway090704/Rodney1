package com.iwit.rodney.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.tools.PlayMedia;

public class ArarmDialog {
	private View contentView;
	private WindowManager wManager;// 窗口管理者
	  private WindowManager.LayoutParams mParams;// 窗口的属性
	ImageView animation;
	AnimationDrawable animationDrawable;
	private Context context;
	public ArarmDialog(Context context){
		this.context = context;
		wManager = (WindowManager) context.getApplicationContext().getSystemService(
		        Context.WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
	    mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
	    mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
	    mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
	    mParams.width = WindowManager.LayoutParams.MATCH_PARENT;//窗口的宽和高
	    mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
	    mParams.x = 0;//窗口位置的偏移量
	    mParams.y = 0;
		contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_ararm, null);
		contentView.setBackgroundDrawable(App.ctx.getResources().getDrawable(
				R.drawable.cover_grey_dialog));
	}
	public void show() {
		wManager.addView(contentView, mParams);
		animation = (ImageView)contentView.findViewById(R.id.animation);
		contentView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				PlayMedia.getInstance(context).releaseMediaPlayer();
			}
		});
		animation.setImageResource(R.drawable.animation_ararm);  
        animationDrawable = (AnimationDrawable) animation.getDrawable();  
        animationDrawable.start(); 
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				dismiss();
			}
		}, 15000);
	}

	public void dismiss() {
		if(animationDrawable != null && animationDrawable.isRunning()){
			animationDrawable.stop();
		}
		if(contentView != null){
			wManager.removeView(contentView);
			contentView = null;
		}
	}
}
