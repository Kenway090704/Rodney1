package com.iwit.rodney.interfaces;

import java.util.List;

import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;

public interface IRecord {
	List<Record> getRecords();

	/**
	 * 删除录音文件和记录，只要一项删除失败就返回false
	 * 
	 * @param record
	 * @return
	 */
	boolean delete(Record record);

	/**
	 * 此方法本该由IMusic实现，但是目前只有此处用到，所以先放在这里
	 * 
	 * @param mid
	 * @return
	 */
	Music getCachedMusic(String mid);

	boolean save(String tempFilePath, Record record);

	boolean rename(Record record, String name);

	/**
	 * 保存歌词文件。
	 * 
	 * @param lrcName
	 *            要保存的文件名。
	 * @param lrcContent
	 *            歌词的内容，包括标题和内容。
	 * @return 由于要保存的文件名可能被占用，所以此方法会调整文件名保存，并返回最终保存的文件名。
	 */
	String saveLrc(String lrcName, String lrcContent);
}
