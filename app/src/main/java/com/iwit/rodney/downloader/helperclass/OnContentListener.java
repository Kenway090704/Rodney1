package com.iwit.rodney.downloader.helperclass;

import java.util.List;

import com.iwit.rodney.downloader.downloadmgr.DownloadInfoBean;

public interface OnContentListener {
	abstract void onChange(List<DownloadInfoBean> listDownloadInfoBean);
}
