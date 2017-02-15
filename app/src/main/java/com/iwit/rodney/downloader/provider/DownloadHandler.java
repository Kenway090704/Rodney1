package com.iwit.rodney.downloader.provider;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DownloadHandler {

    private static final String TAG = "DownloadHandler";
    private final LinkedHashMap<Long, AppDownloadInfo> mDownloadsQueue =
            new LinkedHashMap<Long, AppDownloadInfo>();
    private final HashMap<Long, AppDownloadInfo> mDownloadsInProgress =
            new HashMap<Long, AppDownloadInfo>();
    private static final DownloadHandler mDownloadHandler = new DownloadHandler();
    /**mx threads allowed  ,for muilti downloads , threads pools..*/
    private final int mMaxConcurrentDownloadsAllowed = 5;

    static DownloadHandler getInstance() {
        return mDownloadHandler;
    }

    synchronized void enqueueDownload(AppDownloadInfo info) {
        if (!mDownloadsQueue.containsKey(info.mId)) {
            if (Constants.LOGV) {
                Log.i(TAG, "enqueued download. id: " + info.mId + ", uri: " + info.mUri);
            }
            mDownloadsQueue.put(info.mId, info);
            startDownloadThread();
        }
    }

    private synchronized void startDownloadThread() {
        Iterator<Long> keys = mDownloadsQueue.keySet().iterator();
        ArrayList<Long> ids = new ArrayList<Long>();
        while (mDownloadsInProgress.size() < mMaxConcurrentDownloadsAllowed && keys.hasNext()) {
            Long id = keys.next();
            AppDownloadInfo info = mDownloadsQueue.get(id);
            info.startDownloadThread();
            ids.add(id);
            mDownloadsInProgress.put(id, mDownloadsQueue.get(id));
            if (Constants.LOGV) {
                Log.i(TAG, "started download for : " + id);
            }
        }
        for (Long id : ids) {
            mDownloadsQueue.remove(id);
        }
    }

    synchronized boolean hasDownloadInQueue(long id) {
        return mDownloadsQueue.containsKey(id) || mDownloadsInProgress.containsKey(id);
    }

    synchronized void dequeueDownload(long mId) {
        mDownloadsInProgress.remove(mId);
        startDownloadThread();
        if (mDownloadsInProgress.size() == 0 && mDownloadsQueue.size() == 0) {
            notifyAll();
        }
    }

    // right now this is only used by tests. but there is no reason why it can't be used
    // by any module using DownloadManager (TODO add API to DownloadManager.java)
    public synchronized void WaitUntilDownloadsTerminate() throws InterruptedException {
        if (mDownloadsInProgress.size() == 0 && mDownloadsQueue.size() == 0) {
            if (Constants.LOGVV) {
                Log.i(TAG, "nothing to wait on");
            }
            return;
        }
        if (Constants.LOGVV) {
            for (AppDownloadInfo info : mDownloadsInProgress.values()) {
                Log.i(TAG, "** progress: " + info.mId + ", " + info.mUri);
            }
            for (AppDownloadInfo info : mDownloadsQueue.values()) {
                Log.i(TAG, "** in Q: " + info.mId + ", " + info.mUri);
            }
        }
        if (Constants.LOGVV) {
            Log.i(TAG, "waiting for 5 sec");
        }
        // wait upto 5 sec
        wait(5 * 1000);
    }
}
