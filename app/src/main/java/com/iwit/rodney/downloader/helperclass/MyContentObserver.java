package com.iwit.rodney.downloader.helperclass;

import java.util.List;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.iwit.rodney.downloader.downloadmgr.DownloadInfoBean;
import com.iwit.rodney.downloader.downloadmgr.IwitDownloadManager;

public class MyContentObserver extends ContentObserver {
	private IwitDownloadManager downloadManager;
	private Context mContext;
	private OnContentListener mOnContentListener;

	public MyContentObserver(Context mContext,
			IwitDownloadManager downloadManager,OnContentListener mOnContentListener) {
		super(new Handler());
		this.downloadManager = downloadManager;
		this.mOnContentListener=mOnContentListener;
		this.mContext = mContext;
	}

	public MyContentObserver(Handler handler) {
		super(handler);
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		queryInfomationFromDatabase();
	}

	public void queryInfomationFromDatabase() {
		List<DownloadInfoBean> listDownloadInfoBean = downloadManager.queryDownloadInfoByPackageName(mContext.getPackageName());
		mOnContentListener.onChange(listDownloadInfoBean);
	}
}
