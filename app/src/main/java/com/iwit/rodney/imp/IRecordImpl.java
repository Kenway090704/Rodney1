package com.iwit.rodney.imp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Environment;

import com.iwit.rodney.bussess.MusicDas;
import com.iwit.rodney.bussess.RecordDas;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.FileUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.event.EventRecord;
import com.iwit.rodney.interfaces.IRecord;

import de.greenrobot.event.EventBus;

public class IRecordImpl implements IRecord {
	private Map<String, Music> mid2Music = new HashMap<String, Music>();

	@Override
	public List<Record> getRecords() {
		return RecordDas.getRecords();
	}

	@Override
	public Music getCachedMusic(String mid) {
		synchronized (this) {
			Music m = mid2Music.get(mid);
			if (m != null)
				return m;
			m = getFromDB(mid);
			if (m != null)
				mid2Music.put(mid, m);
			return m;
		}
	}

	private Music getFromDB(String mid) {
		return MusicDas.getMusic(mid);
	}

	@Override
	public boolean delete(Record record) {
		// delete file and db data
		if (RecordDas.delete(record) <= 0) {
			return false;
		}

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ CommConst.MUSIC_FILE_PATH
				+ record.getName());
		if (file.exists()) {
			file.delete();
		}

		notifyRecrodDataChanged();
		return true;
	}

	@Override
	public boolean save(String tempFilePath, Record record) {
		File file = new File(tempFilePath);
		boolean result = RecordDas.save(record);

		if (!result) {
			return false;
		}

		result = file.renameTo(new File(Environment
				.getExternalStorageDirectory().getAbsoluteFile()
				+ CommConst.MUSIC_FILE_PATH + record.getName() + ".amr"));

		if (!result) {
			RecordDas.delete(record);
		}

		if (result) {
			notifyRecrodDataChanged();
		}
		return result;
	}

	@Override
	public boolean rename(Record record, String name) {
		// 重命名本地文件，修改数据库
		boolean result = false;
		try {
			result = RecordDas.update(record.getName(), name) > 0;
		} catch (Exception ignore) {
		}
		if (!result) {
			return false;
		}

		String basePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + CommConst.MUSIC_FILE_PATH;
		File dest = new File(basePath + name + ".amr");
		if (dest.exists()) {
			dest.delete();
		}
		File src = new File(basePath + record.getName() + ".amr");
		if (!src.exists()) {
			throw new RuntimeException();
		}
		result = src.renameTo(dest);
		if (!result) {
			RecordDas.update(name, record.getName());
			return false;
		}
		notifyRecrodDataChanged();
		return true;
	}

	private void notifyRecrodDataChanged() {
		EventBus.getDefault().post(new EventRecord());
	}

	@Override
	public String saveLrc(String lrcName, String lrcContent) {
		String basePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + CommConst.MUSIC_FILE_PATH;
		File lrc = new File(basePath + lrcName + ".lrc");
		int count = 0;
		while (lrc.exists()) {
			if (count++ > 10) {
				return null;
			}
			lrcName += "_";
			lrc = new File(basePath + lrcName + ".lrc");
		}

		if (FileUtils.saveFile(lrc, lrcContent))
			return lrcName;
		else
			return null;
	}

}
