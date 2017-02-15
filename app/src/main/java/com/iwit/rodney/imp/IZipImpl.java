package com.iwit.rodney.imp;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.FileUtils;
import com.iwit.rodney.interfaces.Izip;

public class IZipImpl implements Izip {
	private SharedPreferences sp;
	public static final String SP_NAME = "zip_info";
	// private MessageDigest digest;

	private Map<String, Integer> unzipInfo;
	private UnZipHandler handler;
	private HandlerThread thread;
	public static final int MSG_WHAT_UNZIP = 0;
	public static final int MSG_WHAT_REUNZIP = 1;

	public IZipImpl(Context context) {
		initUnzipStates(context);

		thread = new HandlerThread("unzip-thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		handler = new UnZipHandler(thread.getLooper());
	}

	@SuppressWarnings("unchecked")
	private void initUnzipStates(Context context) {
		sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		unzipInfo = (Map<String, Integer>) sp.getAll();
		if (unzipInfo == null) {
			unzipInfo = new HashMap<String, Integer>();
			return;
		}
		Set<Entry<String, Integer>> entry = unzipInfo.entrySet();
		Iterator<Entry<String, Integer>> it = entry.iterator();
		Entry<String, Integer> en = null;
		while (it.hasNext()) {
			en = it.next();
			if (en.getValue() == STATE_UNZIPING) {
				// 这种情况可能出现在解压线程被意外终止，从而状态没有保存。此时一律视为上次解压操作是失败的。
				en.setValue(STATE_UNZIP_FAILED);
			}
		}
	}

	@Override
	public void unzip(String src, String dest) {
		check(src, dest);
		File srcFile = new File(src);
		String fileName = srcFile.getName();
		String justName = fileName.substring(0, fileName.lastIndexOf("."));
		handler.sendMessage(handler.obtainMessage(MSG_WHAT_UNZIP, new String[] {
				fileName, CommConst.ROOT_BOOK_PATH,
				dest + File.separator + justName }));
	}

	private void check(String src, String dest) {
		checkFilePath(src, dest);

		File srcFile = new File(src);
		if (!srcFile.exists() || !srcFile.isFile()) {
			throw new IllegalArgumentException("src file doesnt exists");
		}

		File destFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + dest);
		if (destFile.exists() && destFile.isFile()) {
			throw new IllegalArgumentException();
		}

		// String fileName = srcFile.getName();
		// destFile = new File(dest + File.separator
		// + fileName.substring(0, fileName.lastIndexOf(".")));
		// destFile.mkdirs();
	}

	@Override
	public int getFileState(String src) {
		checkFilePath(src);
		String key = src.substring(src.lastIndexOf("/") + 1, src.length());
		Integer value = unzipInfo.get(key);
		if (value == null)
			return STATE_UNKNOW_FILE;
		return value;
	}

	private void checkFilePath(String... path) {
		if (path == null) {
			throw new NullPointerException("empty-file-path");
		}
		final int len = path.length;
		for (int i = 0; i < len; i++) {
			if (TextUtils.isEmpty(path[i])) {
				throw new NullPointerException("empty-file-path");
			}
		}
	}

	private class UnZipHandler extends Handler {
		public UnZipHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (MSG_WHAT_UNZIP != msg.what && MSG_WHAT_REUNZIP != msg.what) {
				return;
			}
			boolean redo = msg.what == MSG_WHAT_REUNZIP;
			String[] strs = (String[]) (msg.obj);
			String filename = strs[0];
			String filepath = strs[1];
			String unZipPath = strs[2];

			String src = filepath + filename;
			String key = filename;
			if (!redo) {
				Integer state = unzipInfo.get(key);
				if (state != null && state == STATE_UNZIPED) {
					return;
				}
			}

			saveOrUpdate(key, STATE_UNZIPING);

			boolean result = FileUtils.getInstance().unZipFile(filename,
					filepath, unZipPath + File.separator);
			int resultstate = result ? STATE_UNZIPED : STATE_UNZIP_FAILED;

			boolean saved = saveOrUpdate(key, resultstate);

			if (saved) {// 不管解压成功失败，只要保存了状态就把zip文件删除。才能保证zip文件损坏的话可以重新下载。
				new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + src).delete();
			} else {
				// 不处理存储失败的情况。如果存储失败，下次再打开应用获取解压状态时，会返回STATE_UNZIP_FAILED
			}
		}
	}

//	@Override
//	public void reUnzip(String src, String dest) {
//		check(src, dest);
//		File srcFile = new File(src);
//		String fileName = srcFile.getName();
//		String justName = fileName.substring(0, fileName.lastIndexOf("."));
//		handler.sendMessage(handler.obtainMessage(MSG_WHAT_REUNZIP,
//				new String[] { fileName, CommConst.ROOT_BOOK_PATH,
//						dest + File.separator + justName }));
//	}

	@Override
	public void removeFileState(String src) {
		if (getFileState(src) == STATE_UNKNOW_FILE)
			throw new IllegalArgumentException();

		remove(getKeyByFilePath(src));
	}

	private boolean saveOrUpdate(String key, int state) {
		boolean add = sp.edit().putInt(key, state).commit();
		if (add)
			unzipInfo.put(key, state);
		return add;
	}

	private void remove(String key) {
		boolean remove = sp.edit().remove(key).commit();
		if (remove) {
			unzipInfo.remove(key);
		}
	}

	private String getKeyByFilePath(String src) {
		return new File(src).getName();
	}
}
