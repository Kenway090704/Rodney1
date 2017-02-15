package com.iwit.rodney.interfaces;

import com.iwit.rodney.entity.Downloadable;

public interface IDownload {
	public long download(Downloadable item);

	public void pause(Downloadable item);

	public void resume(Downloadable item);

	/**
	 * 获取该文件的下载状态。若该文件没有下载记录，会返回0。
	 * 
	 * @param item
	 * @return
	 */
	public int getDownloadStatus(Downloadable item);

	/**
	 * 返回正在下载项的进度，0-100
	 * 
	 * @param item
	 * @return
	 */
	public int getDownloadProgress(Downloadable item);


	public void removeDownload(Downloadable item);
	public int getDownloadingTaskCount();
	
	public void pauseAllDownloads();
	
	public boolean deleteDownload(Downloadable item);
}
