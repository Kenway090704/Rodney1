package com.iwit.rodney;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.PlayMedia;
import com.iwit.rodney.comm.utils.SpUtil;
import com.iwit.rodney.db.DBUtil;
import com.iwit.rodney.exception.CrashHandler;
import com.iwit.rodney.service.IMusicManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.service.MusicManagerService;

public class App extends Application {
	public static Context ctx;

	private static DBUtil dbUtil;
	private static SpUtil spUtil;
	private static Typeface mFace;
	// 退出Activity用到H
	private List<Activity> activityList = new LinkedList<Activity>();
	// 当前实例的aplication
	private static App instance;

	public static App getInstance() {
		if (null == instance) {
			instance = new App();
		}
		return instance;
	}

	/**
	 * add the activity
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void removeActivity(Activity activity){
		activityList.remove(activity);
	}
	public void exitApplication() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}
	public void closeCurrentActivity(){
		
	}
	/**
	 * 统一获取Sp操作权
	 * @return
	 */
	public static SpUtil getSp() {
		if (spUtil == null) {
			spUtil = SpUtil.getInstance(ctx, CommConst.SP_FILE);
		}
		return spUtil;
	}

	/**
	 * 统一的获取数据库操作权
	 * @return
	 */
	public static DBUtil getDbUtil() {
		if (dbUtil == null) {
			dbUtil = DBUtil.getInstance(ctx, CommConst.DB_NAME,
					CommConst.DB_VER);
		}
		return dbUtil;
	}

	/**
	 * 获得地区代码，比如CN/US/HK等
	 * @return 系统语言
	 * @see
	 */
	public static String getCountryCode() {
		String able = ctx.getResources().getConfiguration().locale.getCountry();
		return able;
	}

	/**
	 * 获得字体
	 * 
	 * @return
	 */
	public static Typeface getTypeFace() {
		if (mFace == null) {
			mFace = Typeface.createFromAsset(ctx.getAssets(),
					"fonts/huakang.ttf");
		}
		return mFace;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this.getApplicationContext();
		dbUtil = getDbUtil();
//		Toast.makeText(this, "开始了", Toast.LENGTH_SHORT).show();
		CrashHandler.getInstance().init(this);
		PlayMedia.getInstance(this).releaseMediaPlayer();
	}
	public static String getStringR(int str){
		return ctx.getResources().getString(str);
	}
}
