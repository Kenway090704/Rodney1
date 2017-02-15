package com.iwit.rodney.downloader.provider;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

class RealSystemFacade implements SystemFacade {
	private Context mContext;
	private NotificationManager mNotificationManager;

	public RealSystemFacade(Context context) {
		mContext = context;
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public Long getMaxBytesOverMobile() {
		return AppDownloadManager.getMaxBytesOverMobile(mContext);
	}

	@Override
	public Long getRecommendedMaxBytesOverMobile() {
		return AppDownloadManager.getRecommendedMaxBytesOverMobile(mContext);
	}

	@Override
	public void sendBroadcast(Intent intent) {
		mContext.sendBroadcast(intent);
	}

	@Override
	public boolean userOwnsPackage(int uid, String packageName)
			throws NameNotFoundException {
		return mContext.getPackageManager().getApplicationInfo(packageName, 0).uid == uid;
	}

	@Override
	public void postNotification(long id, Notification notification) {
		/**
		 * TODO: The system notification manager takes ints, not longs, as IDs,
		 * but the download manager uses IDs take straight from the database,
		 * which are longs. This will have to be dealt with at some point.
		 */
		mNotificationManager.notify((int) id, notification);
	}

	@Override
	public void cancelNotification(long id) {
		mNotificationManager.cancel((int) id);
	}

	@Override
	public void cancelAllNotifications() {
		mNotificationManager.cancelAll();
	}

	@Override
	public void startThread(Thread thread) {
		thread.start();
	}
	@Override
	public NetworkInfo getActiveNetworkInfo() {
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.w(Constants.TAG, "couldn't get connectivity manager");
			return null;
		}

		final NetworkInfo activeInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (activeInfo == null && Constants.LOGVV) {
			Log.v(Constants.TAG, "network is not available");
		}
		return activeInfo;

	}
}
