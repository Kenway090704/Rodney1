
package com.iwit.rodney.downloader.provider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Performs the background downloads requested by applications that use the Downloads provider.
 */
public class AppDownloadService extends Service {
    /** amount of time to wait to connect to MediaScannerService before timing out */
    private static final long WAIT_TIMEOUT = 10 * 1000;

    /** Observer to get notified when the content observer's data changes */
    private DownloadManagerContentObserver mObserver;

    /** Class to handle Notification Manager updates */
    private DownloadNotification mNotifier;

    /**
     * The Service's view of the list of downloads, mapping download IDs to the corresponding info
     * object. This is kept independently from the content provider, and the Service only initiates
     * downloads based on this data, so that it can deal with situation where the data in the
     * content provider changes or disappears.
     */
    private Map<Long, AppDownloadInfo> mDownloads =new HashMap<Long, AppDownloadInfo>();

    /**
     * The thread that updates the internal download list from the content
     * provider.
     */
    UpdateThread mUpdateThread;

    /**
     * Whether the internal download list should be updated from the content
     * provider.
     */
    private boolean mPendingUpdate;


    SystemFacade mSystemFacade;

    private StorageManager mStorageManager;

    /**
     * Receives notifications when the data in the content provider changes
     */
    private class DownloadManagerContentObserver extends ContentObserver {

        public DownloadManagerContentObserver() {
            super(new Handler());
        }

        /**
         * Receives notification when the data in the observed content
         * provider changes.
         */
        @Override
        public void onChange(final boolean selfChange) {
            if (Constants.LOGVV) {
                Log.v(Constants.TAG, "Service ContentObserver received notification");
            }
            updateFromProvider();
        }

    }


    /**
     * Returns an IBinder instance when someone wants to connect to this
     * service. Binding to this service is not allowed.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public IBinder onBind(Intent i) {
        throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
    }

    /**
     * Initializes the service when it is first created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onCreate");
        }
Log.v("www", "onCreateonCreateonCreateonCreateonCreateonCreateonCreateonCreateonCreate");
        if (mSystemFacade == null) {
            mSystemFacade = new RealSystemFacade(this);
        }

        mObserver = new DownloadManagerContentObserver();
        getContentResolver().registerContentObserver(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                true, mObserver);

  
        mNotifier = new DownloadNotification(this, mSystemFacade);
        mSystemFacade.cancelAllNotifications();
        mStorageManager = StorageManager.getInstance(getApplicationContext());
        updateFromProvider();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int returnValue = super.onStartCommand(intent, flags, startId);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onStart");
        }
        updateFromProvider();
        return returnValue;
    }

    /**
     * Cleans up when the service is destroyed
     */
    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mObserver);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onDestroy");
        }
        super.onDestroy();
    }

    /**
     * Parses data from the content provider into private array
     */
    private void updateFromProvider() {
        synchronized (this) {
            mPendingUpdate = true;
            if (mUpdateThread == null) {
                mUpdateThread = new UpdateThread();
                mSystemFacade.startThread(mUpdateThread);
            }
        }
    }

    private class UpdateThread extends Thread {
        public UpdateThread() {
            super("Download Service");
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            boolean keepService = false;
            // for each update from the database, remember which download is
            // supposed to get restarted soonest in the future
            long wakeUp = Long.MAX_VALUE;
            for (;;) {//this thread is looper for checking quiue is adding ,,,if add, we need check 5 threads is busy now? if not buys,Ok down .Or just wait..
            	
                synchronized (AppDownloadService.this) {
                    if (mUpdateThread != this) {
                        throw new IllegalStateException(
                                "multiple UpdateThreads in AppDownloadService");
                    }
                    if (!mPendingUpdate) {
                        mUpdateThread = null;
                        if (!keepService) {// the service stop  no task in  threads pool??
                        	if(mDownloads.size() == 0){
                        		stopSelf();	
                        	}
                        }
                        if (wakeUp != Long.MAX_VALUE) {
                            scheduleAlarm(wakeUp);
                        }
                        return;
                    }
                    mPendingUpdate = false;
                }

                long now = mSystemFacade.currentTimeMillis();
                boolean mustScan = false;
                keepService = false;
                wakeUp = Long.MAX_VALUE;
                Set<Long> idsNoLongerInDatabase = new HashSet<Long>(mDownloads.keySet());

                Cursor cursor = getContentResolver().query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                        null, null, null, null);
                if (cursor == null) {
                    continue;
                }
                try {
                    AppDownloadInfo.Reader reader =
                            new AppDownloadInfo.Reader(getContentResolver(), cursor);
                    int idColumn = cursor.getColumnIndexOrThrow(Downloads.Impl._ID);
                    if (Constants.LOGVV) {
                        Log.i(Constants.TAG, "number of rows from downloads-db: " +
                                cursor.getCount());
                    }
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        idsNoLongerInDatabase.remove(id);
                        AppDownloadInfo info = mDownloads.get(id);
                        if (info != null) {
                            updateDownload(reader, info, now);
                        } else {
                            info = insertDownload(reader, now);
                        }

                        if (info.hasCompletionNotification()) {
                            keepService = true;
                        }
                        long next = info.nextAction(now);
                        if (next == 0) {
                            keepService = true;
                        } else if (next > 0 && next < wakeUp) {
                            wakeUp = next;
                        }
                    }
                } finally {
                    cursor.close();
                }

                for (Long id : idsNoLongerInDatabase) {
                    deleteDownload(id);
                }

                // is there a need to start the AppDownloadService? yes, if there are rows to be
                // deleted.
                if (!mustScan) {
                    for (AppDownloadInfo info : mDownloads.values()) {
                        if (info.mDeleted && TextUtils.isEmpty(info.mMediaProviderUri)) {
                            mustScan = true;
                            keepService = true;
                            break;
                        }
                    }
                }
                //mNotifier.updateNotification(mDownloads.values());// update list view or notification progress changed.....
                if (mustScan) {
                  //  bindMediaScanner();
                } else {
                  //  mMediaScannerConnection.disconnectMediaScanner();
                }

                // look for all rows with deleted flag set and delete the rows from the database
                // permanently
                for (AppDownloadInfo info : mDownloads.values()) {
                    if (info.mDeleted) {
                        // this row is to be deleted from the database. but does it have
                        // mediaProviderUri?
                        if (TextUtils.isEmpty(info.mMediaProviderUri)) {
                            if (info.shouldScanFile()) {
                                // initiate rescan of the file to - which will populate
                                // mediaProviderUri column in this row
//                                if (!scanFile(info, false, true)) {
//                                    throw new IllegalStateException("scanFile failed!");
//                                }
                                continue;
                            }
                        } else {
                            // yes it has mediaProviderUri column already filled in.
                            // delete it from MediaProvider database.
                            getContentResolver().delete(Uri.parse(info.mMediaProviderUri), null,
                                    null);
                        }
                        // delete the file
                        deleteFileIfExists(info.mFileName);
                        // delete from the downloads db
                        getContentResolver().delete(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                                Downloads.Impl._ID + " = ? ",
                                new String[]{String.valueOf(info.mId)});
                    }
                }
            }
        }

        private void scheduleAlarm(long wakeUp) {
            AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarms == null) {
                Log.e(Constants.TAG, "couldn't get alarm manager");
                return;
            }

            if (Constants.LOGV) {
                Log.v(Constants.TAG, "scheduling retry in " + wakeUp + "ms");
            }

            Intent intent = new Intent(Constants.ACTION_RETRY);
            intent.setClassName("com.android.providers.downloads",
                    DownloadReceiver.class.getName());
            alarms.set(
                    AlarmManager.RTC_WAKEUP,
                    mSystemFacade.currentTimeMillis() + wakeUp,
                    PendingIntent.getBroadcast(AppDownloadService.this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT));
        }
    }

    /**
     * Keeps a local copy of the info about a download, and initiates the
     * download if appropriate.
     */
    private AppDownloadInfo insertDownload(AppDownloadInfo.Reader reader, long now) {
        AppDownloadInfo info = reader.newDownloadInfo(this, mSystemFacade);
        mDownloads.put(info.mId, info);

        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "processing inserted download " + info.mId);
        }

        info.startIfReady(now, mStorageManager);
        return info;
    }

    /**
     * Updates the local copy of the info about a download.
     */
    private void updateDownload(AppDownloadInfo.Reader reader, AppDownloadInfo info, long now) {
        int oldVisibility = info.mVisibility;
        int oldStatus = info.mStatus;

        reader.updateFromDatabase(info);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "processing updated download " + info.mId +
                    ", status: " + info.mStatus);
        }

        boolean lostVisibility =
                oldVisibility == Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                && info.mVisibility != Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                && Downloads.Impl.isStatusCompleted(info.mStatus);
        boolean justCompleted =
                !Downloads.Impl.isStatusCompleted(oldStatus)
                && Downloads.Impl.isStatusCompleted(info.mStatus);
        if (lostVisibility || justCompleted) {
            mSystemFacade.cancelNotification(info.mId);
        }

        info.startIfReady(now, mStorageManager);
    }

    /**
     * Removes the local copy of the info about a download.
     */
    private void deleteDownload(long id) {
        AppDownloadInfo info = mDownloads.get(id);
        if (info.shouldScanFile()) {
            //scanFile(info, false, false);
        }
        if (info.mStatus == Downloads.Impl.STATUS_RUNNING) {
            info.mStatus = Downloads.Impl.STATUS_CANCELED;
        }
        if (info.mDestination != Downloads.Impl.DESTINATION_EXTERNAL && info.mFileName != null) {
            new File(info.mFileName).delete();
        }
        mSystemFacade.cancelNotification(info.mId);
        mDownloads.remove(info.mId);
    }

 

    private void deleteFileIfExists(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                Log.i(Constants.TAG, "deleting " + path);
                File file = new File(path);
                file.delete();
            }
        } catch (Exception e) {
            Log.w(Constants.TAG, "file: '" + path + "' couldn't be deleted", e);
        }
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        for (AppDownloadInfo info : mDownloads.values()) {
            info.dump(writer);
        }
    }
}
