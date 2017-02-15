package com.iwit.rodney.interfaces;

public interface Izip {
	/**
	 * 未知文件。表示该文件并未调用过 {@link #unzip(String, String)}方法
	 */
	public static final int STATE_UNKNOW_FILE = 1;
	public static final int STATE_UNZIPING = 100;
	public static final int STATE_UNZIPED = 101;
	public static final int STATE_UNZIP_FAILED = 102;

	/**
	 * 解压并删除文件(不管解压失败还是成功都会删除)，已存在的文件会被覆盖
	 * 
	 * @param src
	 *            要解压的文件路径，如: /abc/abc.zip
	 * @param dest
	 *            解压的目的路径,如: /abc, 文件将会解压到(sd卡主目录)/abc下的abc目录中
	 */
	public void unzip(String src, String dest);

	// public void reUnzip(String src, String dest);

	public int getFileState(String src);

	public void removeFileState(String src);
}
