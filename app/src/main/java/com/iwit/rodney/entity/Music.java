package com.iwit.rodney.entity;

import java.io.Serializable;

public class Music implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mid;// id 主键
	private String mname;// 名称
	private String mfname;// 文件包名 如 x.mp3
	private String author;// 作者
	private String performer;// 表演者
	private String mtype;// 类型
	private String typeid;// 同上
	private String restype;// 付费与否 字符定义
	private String source;// 来源
	private Integer favourites;// 点赞数量
	private String coverpic;// 封面图片名
	private String lyric;// 歌词文件名
	private Integer recsupport;// 是否支持录音 与录音相关 提供给录音做背景
	private Integer deleted;// 是否删除
	private Integer downloaded = 0; // 标识，书籍是否已经下载到本地， 0 未下载 1 正在下载 2 暂停 3下载完成
	private Integer f; // 当前登陆用户对该音乐是否点赞，0为未点赞，1为已点赞
	private Integer isrecord;// 是否是录音
	private Integer sort;// 所属种类

	private Integer lid;// 语言
	private Integer ordinal;
	/**
	 * 与本地下载数据库和解压用到的数据
	 */
	private long downloadId; // 下载zip包 时 下载id
	private String downloadUrl;// 下载mp3包的url 此属性可以通过bfname 拼接实现
	private int status;// 系在时候的状态码
	private int progress;// 下载的进度

	private boolean isSelect;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	/**
	 * 音乐本身播放的控制变量
	 */
	private int play_status;// 播放的状态

	private int ischoose;

	/**
	 * get set 方法
	 * 
	 * @return
	 */
	public Integer getDownloaded() {
		return downloaded;
	}

	public Integer getF() {
		return f;
	}

	public void setF(Integer f) {
		this.f = f;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public void setDownloaded(Integer downloaded) {
		this.downloaded = downloaded;
	}

	public String getMname() {
		return mname;
	}

	public void setMname(String mname) {
		this.mname = mname;
	}

	public String getMfname() {
		return mfname;
	}

	public void setMfname(String mfname) {
		this.mfname = mfname;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPerformer() {
		return performer;
	}

	public void setPerformer(String performer) {
		this.performer = performer;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getRestype() {
		return restype;
	}

	public void setRestype(String restype) {
		this.restype = restype;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getFavourites() {
		return favourites;
	}

	public void setFavourites(Integer favourites) {
		this.favourites = favourites;
	}

	public String getCoverpic() {
		return coverpic;
	}

	public void setCoverpic(String coverpic) {
		this.coverpic = coverpic;
	}

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	public Integer getRecsupport() {
		return recsupport;
	}

	public void setRecsupport(Integer recsupport) {
		this.recsupport = recsupport;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(long downloadId) {
		this.downloadId = downloadId;
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

	public int getPlay_status() {
		return play_status;
	}

	public void setPlay_status(int play_status) {
		this.play_status = play_status;
	}

	public int getIschoose() {
		return ischoose;
	}

	public void setIschoose(int ischoose) {
		this.ischoose = ischoose;
	}

	public Integer getIsrecord() {
		return isrecord == null ? 0 : isrecord;
	}

	public void setIsrecord(Integer isrecord) {
		this.isrecord = isrecord;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getLid() {
		return lid;
	}

	public void setLid(Integer lid) {
		this.lid = lid;
	}

	public void toInteger() {
		if (f == null) {
			f = 0;
		}
	}

	public Integer getOrdinal() {
		return ordinal == null ? 0 : ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

}
