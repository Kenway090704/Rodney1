package com.iwit.rodney.entity;

public interface Downloadable {
	/**
	 * @return 文件的下载地址，例如http://ds.weeocean.com/file/ds/14081640fdd920.lrc
	 */
	String getUrl();

	/**
	 * @return 文件的本地存储路径 ,例如/digitalstory/music/,默认存放在外部存储
	 */
	String getLocalPath();

}
