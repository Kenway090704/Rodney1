/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iwit.rodney.downloader.provider;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class handles the updating of the Notification Manager for the cases
 * where there is an ongoing download. Once the download is complete (be it
 * successful or unsuccessful) it is no longer the responsibility of this
 * component to show the download in the notification manager.
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class DownloadNotification {

	Context mContext;
	HashMap<String, NotificationItem> mNotifications;
	private SystemFacade mSystemFacade;

	static final String LOGTAG = "DownloadNotification";
	static final String WHERE_RUNNING = "(" + Downloads.Impl.COLUMN_STATUS
			+ " >= '100') AND (" + Downloads.Impl.COLUMN_STATUS
			+ " <= '199') AND (" + Downloads.Impl.COLUMN_VISIBILITY
			+ " IS NULL OR " + Downloads.Impl.COLUMN_VISIBILITY + " == '"
			+ Downloads.Impl.VISIBILITY_VISIBLE + "' OR "
			+ Downloads.Impl.COLUMN_VISIBILITY + " == '"
			+ Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "')";
	static final String WHERE_COMPLETED = Downloads.Impl.COLUMN_STATUS
			+ " >= '200' AND " + Downloads.Impl.COLUMN_VISIBILITY + " == '"
			+ Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "'";

	/**
	 * This inner class is used to collate downloads that are owned by the same
	 * application. This is so that only one notification line item is used for
	 * all downloads of a given application.
	 * 
	 */
	static class NotificationItem {
		int mId; // This first db _id for the download for the app
		long mTotalCurrent = 0;
		long mTotalTotal = 0;
		int mTitleCount = 0;
		String mPackageName; // App package name
		String mDescription;
		String[] mTitles = new String[2]; // download titles.
		String mPausedText = null;

		/*
		 * Add a second download to this notification item.
		 */
		void addItem(String title, long currentBytes, long totalBytes) {
			mTotalCurrent += currentBytes;
			if (totalBytes <= 0 || mTotalTotal == -1) {
				mTotalTotal = -1;
			} else {
				mTotalTotal += totalBytes;
			}
			if (mTitleCount < 2) {
				mTitles[mTitleCount] = title;
			}
			mTitleCount++;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param ctx
	 *            The context to use to obtain access to the Notification
	 *            Service
	 */
	DownloadNotification(Context ctx, SystemFacade systemFacade) {
		mContext = ctx;
		mSystemFacade = systemFacade;
		mNotifications = new HashMap<String, NotificationItem>();
		mBuilder = new HashMap<Long, Notification.Builder>();
	}

	/*
	 * Update the notification ui.
	 */
	public void updateNotification(Collection<AppDownloadInfo> downloads) {
		updateActiveNotification(downloads);
		updateCompletedNotification(downloads);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void updateActiveNotification(Collection<AppDownloadInfo> downloads) {
		// Collate the notifications

		mNotifications.clear();
		for (AppDownloadInfo download : downloads) {
			if (!isActiveAndVisible(download)) {
				continue;
			}
			String packageName = download.mPackage;
			long max = download.mTotalBytes;
			long progress = download.mCurrentBytes;
			long id = download.mId;
			String title = download.mTitle;
			if (title == null || title.length() == 0) {
				title = "test_tilte";
			}

			NotificationItem item;
			String downUrl = download.mUri;

			if (mNotifications.containsKey(downUrl)) {
				item = mNotifications.get(downUrl);
				item.addItem(title, progress, max);
			} else {//
				item = new NotificationItem();
				item.mId = (int) id;
				item.mPackageName = packageName;
				item.mDescription = download.mDescription;
				item.addItem(title, progress, max);
				mNotifications.put(downUrl, item);
			}
			if (download.mStatus == Downloads.Impl.STATUS_QUEUED_FOR_WIFI
					&& item.mPausedText == null) {
				item.mPausedText = "notification_need_wifi_for_size";
			}

		}
		
//		if (mBuilder.size()>5) {//defualt  only 5 
//			mBuilder.clear();
//		}
		
		// Add the notifications
		for (NotificationItem item : mNotifications.values()) {
			// Build the notification object
			final Notification.Builder builder;
		
				builder = new Notification.Builder(mContext);
			
		

			boolean hasPausedText = (item.mPausedText != null);
			int iconResource = android.R.drawable.stat_sys_download_done;
			if (hasPausedText) {
				iconResource = android.R.drawable.stat_sys_warning;
			}
			builder.setSmallIcon(iconResource);
			builder.setOngoing(true);

			boolean hasContentText = false;
			StringBuilder title = new StringBuilder(item.mTitles[0]);
			if (item.mTitleCount > 1) {
				title.append("/");
				title.append(item.mTitles[1]);
				if (item.mTitleCount > 2) {
					// title.append(mContext.getString(R.string.notification_filename_extras,
					// new Object[] { Integer.valueOf(item.mTitleCount - 2) }));
				}
			} else if (!TextUtils.isEmpty(item.mDescription)) {
				builder.setContentText(item.mDescription);
				hasContentText = true;
			}
			builder.setContentTitle(title);

			if (hasPausedText) {
				builder.setContentText(item.mPausedText);
			} else {
				builder.setProgress((int) item.mTotalTotal,
						(int) item.mTotalCurrent, item.mTotalTotal == -1);
				if (hasContentText) {
					builder.setContentInfo(buildPercentageLabel(mContext,
							item.mTotalTotal, item.mTotalCurrent));
				}
			}

			Intent intent = new Intent(Constants.ACTION_LIST);
			intent.setClassName(mContext.getPackageName(),
					DownloadReceiver.class.getName());
			intent.setData(ContentUris.withAppendedId(
					Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, item.mId));
			intent.putExtra("multiple", item.mTitleCount > 1);

			builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0,
					intent, 0));

			mSystemFacade.postNotification(item.mId, builder.getNotification());

		}
	}

	HashMap<Long, Notification.Builder> mBuilder;

	private void updateCompletedNotification(
			Collection<AppDownloadInfo> downloads) {
		for (AppDownloadInfo download : downloads) {
			if (!isCompleteAndVisible(download)) {
				continue;
			}
			notificationForCompletedDownload(download.mId, download.mTitle,
					download.mStatus, download.mDestination, download.mLastMod);
		}
	}

	void notificationForCompletedDownload(long id, String title, int status,
			int destination, long lastMod) {
		Notification.Builder builder;
		// Add the notifications
		builder = new Notification.Builder(mContext);

		builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
		if (title == null || title.length() == 0) {
			title = "download_unknown_title";
		}
		Uri contentUri = ContentUris.withAppendedId(
				Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, id);
		String caption;
		Intent intent;
		if (Downloads.Impl.isStatusError(status)) {
			caption = "notification_download_failed";
			intent = new Intent(Constants.ACTION_LIST);
		} else {
			caption = "notification_download_complete";
			if (destination != Downloads.Impl.DESTINATION_SYSTEMCACHE_PARTITION) {
				intent = new Intent(Constants.ACTION_OPEN);
			} else {
				intent = new Intent(Constants.ACTION_LIST);
			}
		}
		intent.setClassName(mContext.getPackageName(),
				DownloadReceiver.class.getName());
		intent.setData(contentUri);

		builder.setWhen(lastMod);
		builder.setContentTitle(title);
		builder.setContentText(caption);
		builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0,
				intent, 0));

		intent = new Intent(Constants.ACTION_HIDE);
		intent.setClassName(mContext.getPackageName(),
				DownloadReceiver.class.getName());
		intent.setData(contentUri);
		builder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, intent,
				0));

		mSystemFacade.postNotification(id, builder.getNotification());
	}

	private boolean isActiveAndVisible(AppDownloadInfo download) {
		return 100 <= download.mStatus && download.mStatus < 200
				&& download.mVisibility != Downloads.Impl.VISIBILITY_HIDDEN;
	}

	private boolean isCompleteAndVisible(AppDownloadInfo download) {
		return download.mStatus >= 200
				&& download.mVisibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
	}

	private static String buildPercentageLabel(Context context,
			long totalBytes, long currentBytes) {
		if (totalBytes <= 0) {
			return null;
		} else {
			final int percent = (int) (100 * currentBytes / totalBytes);
			return "download%:" + percent;
		}
	}
}
