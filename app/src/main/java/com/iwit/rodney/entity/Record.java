package com.iwit.rodney.entity;

import com.iwit.rodney.comm.CommConst;

public class Record {
	private Long id;
	/**
	 * 录音的命名，也是录音的文件名，不包括后缀。后缀默认为amr。
	 * <p>
	 * 默认存储路径是 "外部存储"/rodney/music/
	 */
	private String name;
	/**
	 * 录音对应的歌词文件名，包括后缀lrc。一般情况下与name属性相同，但也有特殊情况。如果没有歌词文件，则为null。
	 * <p>
	 * 默认存储路径是 "外部存储"/rodney/music/
	 */
	private String lyric;

	/**
	 * 配图的文件名。一般使用方法是服务器下载根地址加文件名作为URL，然后由WebRequest类去下载、管理和显示图片。
	 * <p>
	 * 如果为null，则表示没有配图。此时应该显示一个默认图片。
	 */
	private String pic_name;

	public Record() {

	}

	public Record(String name, String lyric, String picName) {
		if (name == null || name.length() < 1) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.lyric = lyric;
		this.pic_name = picName;
	}

	public String getName() {
		return name;
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	public String getPic_name() {
		return pic_name;
	}

	public void setPic_name(String picName) {
		this.pic_name = picName;
	}

	public Music toMusic() {
		Music music = new Music();
		music.setMname(name);
		music.setIsrecord(1);
		music.setMtype(CommConst.RECORD_TYPE);
		music.setLyric(lyric);
		return music;
	}
}
