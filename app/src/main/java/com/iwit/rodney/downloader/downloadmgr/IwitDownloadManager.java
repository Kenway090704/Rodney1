package com.iwit.rodney.downloader.downloadmgr;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.downloader.provider.AppDownloadManager;
import com.iwit.rodney.downloader.provider.AppDownloadManager.Request;

public class IwitDownloadManager {

	private static IwitDownloadManager mInstance;
	private static AppDownloadManager downloadManager;

	private static final String TAG = "IwitDownloadManager";

	private static Context mContext;

	private IwitDownloadManager() {

	}

	public synchronized static IwitDownloadManager getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new IwitDownloadManager();
		}
		downloadManager = AppDownloadManager.getDownloadManagerService(context);
		mContext = context;
		return mInstance;
	}

	/**
	 * @Desciption: 根据指定的id中断下载任务
	 * @param id
	 *            ：指定下载任务的id
	 */
	public int pauseDownload(long id) {
		return downloadManager.pauseDownload(id);
	}

	public int pauseDownload(String url) {
		return downloadManager.pauseDownload(url);
	}

	/**
	 * lkt add,pause all download no matter what packageName
	 * 
	 * @param packageName
	 */
	public void pauseAllDownloadlkt() {
		downloadManager.pauseAllDownload();
	}

	/**
	 * @Desciption: 暂停所有正在下载的任务
	 */
	public void pauseAllDownload(String packageName) {
		/**
		 * 找到正在下载的线程id
		 * */
		Cursor cursor = downloadManager
				.queryDownloadInfoWherePackage(packageName);
		if (cursor != null) {
			try {
				while (cursor.moveToNext()) {
					int status = cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_STATUS));

					if (DownLoadUtils.translateStatus(status) == AppDownloadManager.STATUS_RUNNING) {
						long id = (long) cursor.getInt(cursor.getInt(0));
						pauseDownload(id);
					}
				}
			} finally {
				cursor.close();
			}
		}
	}

	/**
	 * @Desciption: 根据指定的id续传下载任务
	 * @param id
	 *            指定下载任务的id
	 */
	public int resumeDownload(long id) {
		return downloadManager.resumeDownload(id);
	}

	public int resumeDownload(String url) {
		return downloadManager.resumeDownload(url);
	}

	public void restartDownload(long id) {
		downloadManager.restartDownload(id);
	}

	/**
	 * @Desciption: 根据指定的id查询下载了多少字节
	 * @param id
	 *            指定下载任务的id
	 */
	/*
	 * public int queryDownloadByteSoFar(long id) { AppDownloadManager.Query
	 * query = new AppDownloadManager.Query(); query.setFilterById(id); Cursor c
	 * = downloadManager.query(query); int bytesDownloadSoFar = -1; if (null !=
	 * c && c.moveToFirst()) { bytesDownloadSoFar = c .getInt(c
	 * .getColumnIndexOrThrow
	 * (AppDownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)); } if (c != null) {
	 * c.close(); } return bytesDownloadSoFar; }
	 */
	/**
	 * @Desciption: 根据指定的id移除APK文件
	 * @param id
	 */
	public boolean deleteDatabaseAndLocalFile(String url, String packageName) {
		String filePath = getApkFilePath(url, packageName);
		int flag = downloadManager.RowDeleted(url, packageName);
		if (!TextUtils.isEmpty(filePath)) {
			File deleteFile = new File(filePath);
			deleteFile(deleteFile);
		}
		Log.e("qh", "delete flag:" + flag);
		return flag > 0;
	}

	private String getApkFilePath(String url, String packageName) {
		Cursor cursor = downloadManager.queryDwonloadIWherePackageAndUri(
				packageName, url);
		String filePath = null;
		Log.e(TAG, "getApkFilePath +++url : " + url);
		Log.e(TAG, "getApkFilePath +++packageName : " + packageName);
		Log.e(TAG, "getApkFilePath cursor : " + cursor);
		while (cursor != null && cursor.moveToNext()) {
			filePath = cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_LOCAL_FILENAME));
		}
		if (cursor != null) {
			cursor.close();
		}
		Log.e(TAG, "getApkFilePath file path : " + filePath);
		return filePath;

	}

	private boolean deleteFile(File file) {
		boolean flag = false;
		if (file != null && file.exists()) {
			flag = file.delete();
		}
		return flag;
	}

	/**
	 * @Desciption: 根据指定的url下载文件
	 * @param url
	 *            用户指定的web 的URI
	 * @param title
	 *            用户指定的下载的文件名，包括后缀
	 */
	public long startDownload(String url, String title, String Path) {
		Uri resource = Uri.parse(encodeGB(url));
		AppDownloadManager.Request request = new AppDownloadManager.Request(
				resource);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
				.getFileExtensionFromUrl(url));
		request.setMimeType(mimeString);
		//request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(false);
		request.setDestinationInExternalPublicDir(Path, title);
		request.setTitle(title);
		long id = downloadManager.enqueue(request);
		return id;
	}

	/**
	 * 如果服务器不支持中文路径的情况下需要转换url的编码。
	 * 
	 * @param string
	 * @return
	 */
	private String encodeGB(String string) {
		// 转换中文编码
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
		return split[0];
	}

	public int getDownloadingCount(String packageName) {
		Cursor cursor = downloadManager
				.queryDownloadInfoWherePackage(packageName);
		int downloadingCount = 0;
		if (cursor != null) {
			try {
				while (cursor.moveToNext()) {
					int status = cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_STATUS));
					if (DownLoadUtils.translateStatus(status) == AppDownloadManager.STATUS_RUNNING) {
						downloadingCount++;
					}
				}
			} finally {
				cursor.close();
			}
		}
		return downloadingCount;
	}

	/**
	 * @Desription :查询下载文件的大小 *
	 * @param id
	 *            下载文件的ID
	 * @return 文件的大小
	 */
	/**
	 * @description 根据包名和URL确定下载数量
	 * @param packageName
	 *            应用程序的包名
	 * @param url
	 *            下载的应用程序的url
	 * 
	 */
	public DownloadInfoBean queryDownloadInfoPackageNameAndUrl(
			String packageName, String url) {
		Cursor cursor = downloadManager.queryDwonloadIWherePackageAndUri(
				packageName, url);
		DownloadInfoBean downloadBean = new DownloadInfoBean();
		while (cursor != null && cursor.moveToNext()) {
			downloadBean.setDownload_id(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ID)));
			downloadBean
					.setDownload_packagename(cursor.getString(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_NOTIFICATION_PACKAGE_NAME)));
			downloadBean.setDownload_status(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_STATUS)));
			downloadBean.setDownload_title_name(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_TITLE)));
			downloadBean
					.setDownload_current_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
			downloadBean
					.setDownload_total_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
			downloadBean.setDownload_uri(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_URI)));
			downloadBean
					.setDownload_filepath(AppDownloadManager.COLUMN_LOCAL_FILENAME);
			downloadBean.setDownload_errormes(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ERROR_MSG)));

		}
		if (cursor != null) {
			cursor.close();
		}

		return downloadBean;
	}

	/**
	 * @description 根据包名和URL确定下载数量
	 * @param packageName
	 *            应用程序的包名
	 * @param url
	 *            下载的应用程序的url
	 * 
	 */
	public List<DownloadInfoBean> queryDownloadInfoByPackageNameAndUrl(
			String packageName, String url) {
		Cursor cursor = downloadManager.queryDwonloadIWherePackageAndUri(
				packageName, url);
		List<DownloadInfoBean> listDownloadInfoBean = new ArrayList<DownloadInfoBean>();
		while (cursor != null && cursor.moveToNext()) {
			DownloadInfoBean downloadBean = new DownloadInfoBean();
			downloadBean.setDownload_id(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ID)));
			downloadBean
					.setDownload_packagename(cursor.getString(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_NOTIFICATION_PACKAGE_NAME)));
			downloadBean.setDownload_status(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_STATUS)));
			downloadBean.setDownload_title_name(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_TITLE)));
			downloadBean
					.setDownload_current_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
			downloadBean
					.setDownload_total_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
			downloadBean.setDownload_uri(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_URI)));
			downloadBean
					.setDownload_filepath(AppDownloadManager.COLUMN_LOCAL_FILENAME);
			downloadBean.setDownload_errormes(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ERROR_MSG)));

			listDownloadInfoBean.add(downloadBean);
		}
		if (cursor != null) {
			cursor.close();
		}

		return listDownloadInfoBean;
	}

	/**
	 * @description:
	 * 
	 * 
	 */
	public synchronized List<DownloadInfoBean> queryDownloadInfoByPackageName(
			String packageName) {
		Cursor cursor = downloadManager
				.queryDownloadInfoWherePackage(packageName);
		List<DownloadInfoBean> listDownloadInfoBean = new ArrayList<DownloadInfoBean>();
		while (cursor != null && cursor.moveToNext()) {
			DownloadInfoBean downloadBean = new DownloadInfoBean();
			downloadBean.setDownload_id(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ID)));
			downloadBean
					.setDownload_packagename(cursor.getString(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_NOTIFICATION_PACKAGE_NAME)));
			downloadBean.setDownload_status(cursor.getInt(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_STATUS)));
			downloadBean.setDownload_title_name(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_TITLE)));
			downloadBean
					.setDownload_current_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
			downloadBean
					.setDownload_total_bytes(cursor.getInt(cursor
							.getColumnIndex(AppDownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
			downloadBean.setDownload_uri(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_URI)));
			downloadBean.setDownload_filepath(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_LOCAL_FILENAME)));
			downloadBean.setDownload_errormes(cursor.getString(cursor
					.getColumnIndex(AppDownloadManager.COLUMN_ERROR_MSG)));

			listDownloadInfoBean.add(downloadBean);
		}
		if (cursor != null) {
			cursor.close();
		}
		return listDownloadInfoBean;
	}

	public synchronized List<Integer> queryDownloadStateByUrl(String url) {
		return downloadManager.queryDownloadStates(CommConst.PACKAGE_NAME, url);
	}

	public synchronized List<Integer[]> queryDownloadProgress(String url) {
		return downloadManager.queryDownloadProgress(CommConst.PACKAGE_NAME,
				url);
	}

	public synchronized List<Long> queryDownloadID(String url) {
		return downloadManager.queryDownloadID(CommConst.PACKAGE_NAME, url);
	}

	public synchronized void removeTask(long id) {
		downloadManager.remove(id);
	}

}
