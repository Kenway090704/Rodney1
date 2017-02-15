package com.iwit.rodney.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class LrcRows {
	private List<Long> times = new ArrayList<Long>();
	private List<String> lrcs = new ArrayList<String>();

	public void add(String row) {
		if (row.length() < 10) {
			return;
		}
		if (row.charAt(9) != ']') {
			return;
		}
		String timeStr = row.substring(1, 9);
		long time = 0;
		try {
			int min = Integer.valueOf(timeStr.substring(0, 2));
			int sec = Integer.valueOf(timeStr.substring(3, 5));
			int mil = Integer.valueOf(timeStr.substring(6));
			time = (min * 60 + sec) * 1000 + mil * 10;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}
		times.add(time);
		String rowLrc = row.length() > 10 ? row.substring(10) : "";
		lrcs.add(rowLrc);
	}

	public String getByIndex(int index) {
		if (index >= 0 && index < times.size()) {
			return lrcs.get(index);
		}
		return null;
	}

	public int getIndexByTimestamp(long timestamp) {
		int index = Collections.binarySearch(times, timestamp);
		return Math.abs(index) - 1;
	}

	public long gettimestampByIndex(int index) {
		return times.get(index);
	}

	public int getTotalIndex() {
		return times == null ? 0 : times.size();
	}
}
