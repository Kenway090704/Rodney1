package com.iwit.rodney.entity;

import java.io.Serializable;

import com.iwit.rodney.comm.CommConst;

public class Book implements Serializable, Downloadable {
	public static final int STATUS_NO_DOWNLOAD = 0;
	public static final int STATUS_DOWNLOADING = 192;
	public static final int STATUS_PAUSE = 193;
	public static final int STATUS_READY = 200;
	public static final int STATUS_MOUNT_BEGIN = 194;
	public static final int STATUS_MOUNT_ING = 195;
	public static final int STATUS_MOUNT_END = 196;
	public static final int STATUS_UNZIPING = 197;
	public static final int STATUS_READ_BOOK = 2 << 2;

	private static final long serialVersionUID = 1L;
	/**
	 * 与服务端交换的数据
	 */
	private Integer bid;
	private String bname;
	private String bfname;
	private Integer size;
	private String bdesc;
	private String coverpic;
	private String rackpic;
	private String p1;
	private String p2;
	private String p3;
	private String p4;
	private String restype;
	private Integer sid;
	private Integer deleted;

	/**
	 * 与本地下载数据库和解压用到的数据
	 */
	private Integer downloaded;// 下载标识
	private long downloadId; // 下载zip包 时 下载id
	private String downloadUrl;// 下载zip包的url 此属性可以通过bfname 拼接实现
	private int status;// 系在时候的状态码
	private int progress;// 下载的进度

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

	public String getBname() {
		return bname;
	}

	public void setBname(String bname) {
		this.bname = bname;
	}

	public String getBfname() {
		return bfname;
	}

	public void setBfname(String bfname) {
		this.bfname = bfname;
	}

	public float getSize() {
		return size;
	}

	public void setBdesc(String bdesc) {
		this.bdesc = bdesc;
	}

	public String getCoverpic() {
		return coverpic;
	}

	public void setCoverpic(String coverpic) {
		this.coverpic = coverpic;
	}

	public String getRackpic() {
		return rackpic;
	}

	public void setRackpic(String rackpic) {
		this.rackpic = rackpic;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	public String getP3() {
		return p3;
	}

	public void setP3(String p3) {
		this.p3 = p3;
	}

	public String getP4() {
		return p4;
	}

	public void setP4(String p4) {
		this.p4 = p4;
	}

	public String getRestype() {
		return restype;
	}

	public void setRestype(String restype) {
		this.restype = restype;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getDeleted() {
		return deleted == null ? 0 : deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
	}

	public Integer getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Integer downloaded) {
		this.downloaded = downloaded;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getBdesc() {
		return bdesc;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "bname:" + (bname == null ? "null" : bname) + "|bid:"
				+ (bid == null ? "null" : bid);
	}

	@Override
	public String getUrl() {
		return CommConst.SIT_ROOT_FILE_URL + bfname;
	}

	@Override
	public String getLocalPath() {
		return CommConst.ROOT_BOOK_PATH;
	}
}
