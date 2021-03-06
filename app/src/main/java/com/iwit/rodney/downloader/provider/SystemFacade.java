package com.iwit.rodney.downloader.provider;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.NetworkInfo;


interface SystemFacade {
    /**
     * @see System#currentTimeMillis()
     */
    public long currentTimeMillis();

    /**
     * @return Currently active network, or null if there's no active
     *         connection.
     */
    public NetworkInfo getActiveNetworkInfo();

    /**
     * @return maximum size, in bytes, of downloads that may go over a mobile connection; or null if
     * there's no limit
     */
    public Long getMaxBytesOverMobile();

    /**
     * @return recommended maximum size, in bytes, of downloads that may go over a mobile
     * connection; or null if there's no recommended limit.  The user will have the option to bypass
     * this limit.
     */
    public Long getRecommendedMaxBytesOverMobile();

    /**
     * Send a broadcast intent.
     */
    public void sendBroadcast(Intent intent);

    /**
     * Returns true if the specified UID owns the specified package name.
     */
    public boolean userOwnsPackage(int uid, String pckg) throws NameNotFoundException;

    /**
     * Post a system notification to the NotificationManager.
     */
    public void postNotification(long id, Notification notification);

    /**
     * Cancel a system notification.
     */
    public void cancelNotification(long id);

    /**
     * Cancel all system notifications.
     */
    public void cancelAllNotifications();

    /**
     * Start a thread.
     */
    public void startThread(Thread thread);
}
