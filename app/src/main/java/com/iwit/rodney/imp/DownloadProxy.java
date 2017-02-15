package com.iwit.rodney.imp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iwit.rodney.App;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.downloader.downloadmgr.IwitDownloadManager;
import com.iwit.rodney.entity.Downloadable;
import com.iwit.rodney.interfaces.IDownload;

public class DownloadProxy implements IDownload {
	public static final String TAG = "DownloadProxy";
	private IwitDownloadManager dm;

	public DownloadProxy(Context context) {
		dm = IwitDownloadManager.getInstance(context.getApplicationContext());
		if (dm == null)
			throw new NullPointerException();
	}

	@Override
	public long download(Downloadable item) {
		String url = item.getUrl();
		String localPath = item.getLocalPath();
		if (url == null || localPath == null) {
			throw new IllegalArgumentException();
		}
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

		File path = new File(localPath.substring(0, localPath.length() - 1));
		if (path.exists()) {
			if (path.isFile()) {
				throw new IllegalArgumentException();
			}
		} else {
			path.mkdirs();
		}
		String title = null;
		try {
			title = url.substring(url.lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

		// 验证要下载的文件是否已存在，如果已存在，则更新下载状态为success
		File dest = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + localPath + title);
		Log.e(TAG, "即将下载文件:" + dest.getAbsolutePath());
		try {
			dest.delete();// 如果文件存在则直接删除文件再下载。不是很好的做法，目前先这样实现。
		} catch (Exception ignore) {
		}
		return dm.startDownload(url, title, localPath);
	}

	@Override
	public void pause(Downloadable item) {
		String url = item.getUrl();
		if (url == null) {
			throw new IllegalArgumentException();
		}
		dm.pauseDownload(url);
	}

	@Override
	public void resume(Downloadable item) {
		String url = item.getUrl();
		if (url == null) {
			throw new IllegalArgumentException();
		}
		dm.resumeDownload(url);
	}

	@Override
	public int getDownloadStatus(Downloadable item) {
		String url = item.getUrl();
		if (url == null) {
			throw new IllegalArgumentException();
		}
		List<Integer> is = dm.queryDownloadStateByUrl(url);
		if (is != null && is.size() > 1) {
		//	throw new RuntimeException("该url对应的任务超过1个，请保证同一个url的下载任务不超过1个");
		}
		return is == null || is.size() == 0 ? 0 : is.get(0);
	}

	@Override
	public int getDownloadProgress(Downloadable item) {
		String url = item.getUrl();
		if (url == null) {
			throw new IllegalArgumentException();
		}
		List<Integer[]> its = dm.queryDownloadProgress(url);
		if (its != null && its.size() > 1) {
		//	throw new RuntimeException("该url对应的任务超过1个，请保证同一个url的下载任务不超过1个");
		}
		if (its == null || its.size() < 1) {
			return 0;
		}
		int i0 = its.get(0)[0];
		int i1 = its.get(0)[1];
		if (i0 < 0 || i1 < 0)
			return 0;
		int result = (int) (i0 * 1.0 / i1 * 100);
		Log.e(TAG, "getDownloadProgress " + i0 + "/" + i1 + "=" + result);
		return result;
	}

	@Override
	public void removeDownload(Downloadable item) {
		dm.deleteDatabaseAndLocalFile(item.getUrl(), CommConst.PACKAGE_NAME);
	}

	@Override
	public int getDownloadingTaskCount() {
		return dm.getDownloadingCount(CommConst.PACKAGE_NAME);
	}

	@Override
	public void pauseAllDownloads() {
		dm.pauseAllDownloadlkt();
	}

	@Override
	public boolean deleteDownload(Downloadable item) {
		String url = item.getUrl();
		return dm.deleteDatabaseAndLocalFile(url, App.ctx.getPackageName());
	}

}
