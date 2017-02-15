package com.iwit.rodney.downloader.downloadmgr;

import com.iwit.rodney.downloader.provider.Downloads;


public class DownLoadUtils {
	
	public final static int STATUS_PENDING = 1 << 0;//待定

	/**
	 * Value of {@link #COLUMN_STATUS} when the download is currently running.
	 */
	public final static int STATUS_RUNNING = 1 << 1;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download is waiting to retry or
	 * resume.
	 */
	public final static int STATUS_PAUSED = 1 << 2;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download has successfully
	 * completed.
	 */
	public final static int STATUS_SUCCESSFUL = 1 << 3;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download has failed (and will
	 * not be retried).
	 */
	public final static int STATUS_FAILED = 1 << 4;
	
	public static int translateStatus(int status) {
		switch (status) {
		case Downloads.Impl.STATUS_PENDING:
			return STATUS_RUNNING;
			// liujw
		case Downloads.Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
			// end liujw
		case Downloads.Impl.STATUS_RUNNING:
			return STATUS_RUNNING;

		case Downloads.Impl.STATUS_PAUSED_BY_APP:
		case Downloads.Impl.STATUS_WAITING_TO_RETRY:
		case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
		case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
			return STATUS_PAUSED;

		case Downloads.Impl.STATUS_SUCCESS:
			return STATUS_SUCCESSFUL;

		default:
			assert Downloads.Impl.isStatusError(status);
			return STATUS_FAILED;
		}
	}
	
	
	

}
