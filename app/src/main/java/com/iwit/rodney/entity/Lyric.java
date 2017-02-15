package com.iwit.rodney.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class Lyric {
	private LrcRows rows;

	public Lyric() {
		rows = new LrcRows();
	}

	public static Lyric getByFile(File file) {
		if (file == null || !file.isFile() || !file.exists()) {
			return null;
		}
		Lyric lrc = new Lyric();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis,"GB2312"));//这里为什么要使用GB2312?
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				lrc.rows.add(URLDecoder.decode(line, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lrc;
	}
	
	public String getLrcByIndex(int index){
		return rows.getByIndex(index);
	}
	public long getTimestampByIndex(int index){
		return rows.gettimestampByIndex(index);
	}
	public int getIndexByTimestamp(long timestamp){
		return rows.getIndexByTimestamp(timestamp);
	}
	public int getTotalIndex(){
		return rows.getTotalIndex();
	}
}
