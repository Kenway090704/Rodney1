package com.iwit.rodney.interfaces;

import android.content.Context;
import android.os.Looper;

import com.iwit.rodney.imp.DownloadProxy;
import com.iwit.rodney.imp.IAccountImp;
import com.iwit.rodney.imp.IBookImpl;
import com.iwit.rodney.imp.IMusicImp;
import com.iwit.rodney.imp.IRecordImpl;
import com.iwit.rodney.imp.IStoryImpl;
import com.iwit.rodney.imp.IZipImpl;

public class IManager {
	private static IMusic mIMusic;
	private static IAccount mIAccount;

	public static IMusic getIMusic() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("thread should be mian thread.");
		}
		if (null == mIMusic) {
			mIMusic = new IMusicImp();
		}
		return mIMusic;
	}

	public static IAccount getIAccount() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("thread should be mian thread.");
		}
		if (null == mIAccount) {
			mIAccount = new IAccountImp();
		}
		return mIAccount;
	}

	private static IStory sIStory;

	public static IStory getIStory() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("thread should be mian thread.");
		}
		if (sIStory == null) {
			sIStory = new IStoryImpl();
		}
		return sIStory;
	}

	private static IRecord sIMyStory;

	public static IRecord getIRecord() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("thread should be mian thread.");
		}
		if (sIMyStory == null) {
			sIMyStory = new IRecordImpl();
		}
		return sIMyStory;
	}

	private static IBook sIBookBuss;

	public static IBook getIBookBuss() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			throw new RuntimeException("thread should be mian thread.");
		}
		if (sIBookBuss == null) {
			sIBookBuss = new IBookImpl();
		}
		return sIBookBuss;
	}

	private static IDownload sIDownload;

	public static IDownload getIDownload(Context context) {
		if (sIDownload == null) {
			synchronized (IManager.class) {
				if (sIDownload == null) {
					sIDownload = new DownloadProxy(context);
				}
			}
		}
		return sIDownload;
	}

	private static Izip sIzip;

	public synchronized static Izip getIzip(Context context) {
		if (sIzip == null) {
			sIzip = new IZipImpl(context);
		}
		return sIzip;
	}
}
