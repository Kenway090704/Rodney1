package com.iwit.rodney.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class CrashHandler implements UncaughtExceptionHandler {

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	// CrashHandler 实例
	private static CrashHandler INSTANCE = new CrashHandler();
	public SharedPreferences _sp;
	private String dir = Environment.getExternalStorageDirectory().getPath();
	private String fileName = "erro_rodney_log.txt";
	/** 保证只有一个 CrashHandler 实例 */
	private CrashHandler() {

	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 */
	public void init(Context context) {
		mContext = context;
		// 记录下默认的UncaughtExceptionHandler
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(thread, ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Thread thread, Throwable ex) {
		if (ex == null) {
			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(thread + ", Cause By:" + ex).append("\r\n\r\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			sb.append(elements[i].toString() + "\r\n");
		}
		// 将日志信息存放到txt文档
		FileOutputStream out;
		try {
			File file = new File(dir + "//" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file, true);
			out.write(sb.toString().getBytes("utf-8"));
			if (out != null) {
				out.close();
				out = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}