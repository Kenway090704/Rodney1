package com.iwit.rodney.downloader.downloadmgr;


public class DownloadInfoBean {

	private int download_id;

	private int download_status;

	private int download_total_bytes;

	private int download_current_bytes;

	private String download_title_name;

	private String download_uri;

	private String download_packagename;
	private String download_filepath ; 
	private String download_errormes ; 


	public int getDownload_id() {
		return download_id;
	}

	public void setDownload_id(int download_id) {
		this.download_id = download_id;
	}

	public int getDownload_status() {
		return download_status;
	}

	public void setDownload_status(int download_status) {
		this.download_status = download_status;
	}

	public int getDownload_total_bytes() {
		return download_total_bytes;
	}

	public void setDownload_total_bytes(int download_total_bytes) {
		this.download_total_bytes = download_total_bytes;
	}

	public int getDownload_current_bytes() {
		return download_current_bytes;
	}

	public void setDownload_current_bytes(int download_current_bytes) {
		this.download_current_bytes = download_current_bytes;
	}

	public String getDownload_title_name() {
		return download_title_name;
	}

	public void setDownload_title_name(String download_title_name) {
		this.download_title_name = download_title_name;
	}

	public String getDownload_uri() {
		return download_uri;
	}

	public void setDownload_uri(String download_uri) {
		this.download_uri = download_uri;
	}

	public String getDownload_packagename() {
		return download_packagename;
	}

	public void setDownload_packagename(String download_packagename) {
		this.download_packagename = download_packagename;
	}

	public String getDownload_filepath() {
		return download_filepath;
	}

	public void setDownload_filepath(String download_filepath) {
		this.download_filepath = download_filepath;
	}

	public String getDownload_errormes() {
		return download_errormes;
	}

	public void setDownload_errormes(String download_errormes) {
		this.download_errormes = download_errormes;
	}
}
