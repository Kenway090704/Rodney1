package com.iwit.rodney.bussess;

import java.util.List;

import android.content.ContentValues;
import android.text.TextUtils;

import com.iwit.rodney.App;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.ui.view.RecordPanel;

public class RecordDas {
	public static final String TABLE_NAME = "record";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LYRIC = "lyric";
	public static final String COLUMN_PIC_NAME = "pic_name";

	// public static final String COLUMN_MID = "mid";

	public static final String SELECTION = "SELECT * FROM " + TABLE_NAME;

	public static boolean save(Record r) {
		// return CommDas.insertOrUpdate(TABLE_NAME, new Object[][] {
		// { COLUMN_ID, null }, { COLUMN_NAME, r.getName() },
		// { COLUMN_MID, r.getMid() } }, new Object[] { COLUMN_ID });
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, r.getName());
		values.put(COLUMN_LYRIC, r.getLyric());
		values.put(COLUMN_PIC_NAME, r.getPic_name());
		return App.getDbUtil().db.insert(TABLE_NAME, null, values) > 0;
	}

	public static List<Record> getRecords() {
		try {
			List<Record> rs = App.getDbUtil().queryMulti(Record.class,
					SELECTION, new Object[] {});
			return rs;
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int delete(Record r) {
		if (r.getName() == null || r.getName().length() < 1) {
			throw new IllegalArgumentException();
		}
		return App.getDbUtil().db.delete(TABLE_NAME, COLUMN_NAME + " = ?",
				new String[] { String.valueOf(r.getName()) });
	}

	public static int update(String nameSrc, String nameDst) {
		if (TextUtils.isEmpty(nameSrc) || TextUtils.isEmpty(nameDst)) {
			throw new IllegalArgumentException();
		}
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, nameDst);
		return App.getDbUtil().db.update(TABLE_NAME, values, COLUMN_NAME
				+ " = ?", new String[] { nameSrc });
	}
}
