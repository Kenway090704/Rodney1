package com.iwit.rodney.entity;

import com.iwit.rodney.comm.CommConst;

/**
 * 故事类。其实在代码实现上，故事即是音乐。不过为了与UI界面逻辑匹配，所以创建此类。音乐与故事之间的数据转化在业务层实现。
 */
public class Story implements Downloadable {
	/**
	 * 故事的命名
	 */
	private String name;
	private String picName;
	private String author;
	/**
	 * 是否已点赞
	 */
	private String like;
	private String lrcName;
	private String performer;
	/**
	 * 参照类的说明。故事即为音乐，所以此mid即为音乐的mid
	 */
	private String mid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String name) {
		this.picName = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getLrcName() {
		return lrcName;
	}

	public void setLrcName(String lrcName) {
		this.lrcName = lrcName;
	}

	public String getPerformer() {
		return performer;
	}

	public void setPerformer(String performer) {
		this.performer = performer;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	@Override
	public String getUrl() {
		return CommConst.SIT_ROOT_FILE_URL + lrcName;
	}

	@Override
	public String getLocalPath() {
		return CommConst.MUSIC_FILE_PATH;
	}
}
