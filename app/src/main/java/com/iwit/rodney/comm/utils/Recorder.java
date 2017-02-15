package com.iwit.rodney.comm.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.interfaces.IRecorder;

public class Recorder implements IRecorder {
	public static final String TAG = "Recorder";
	public static final String RECORDING_FILE_NAME = "recorder_recording.amr";
	public static final String TEMP_FILE_NAME = "recorder_temp.amr";
	public static final String MERGE_FILE_NAME = "recorder_merge.amr";

	private MediaRecorder recorder;
	private boolean stopped = true;
	private static Recorder sInstance;

	public static Recorder getInstance() {
		if (sInstance == null) {
			synchronized (Recorder.class) {
				if (sInstance == null) {
					sInstance = new Recorder();
				}
			}
		}
		return sInstance;
	}

	@Override
	public void start() {
		if (!stopped) {
			throw new IllegalStateException("shouldnt start again before stop");
		}
		deleteTempFile();
		stopped = false;
		resume();
	}

	private void deleteTempFile() {
		File temp = new File(getTempFilePath());
		if (temp.exists()) {
			boolean result = temp.delete();
			if (!result) {
				throw new RuntimeException("couldnt delete file "
						+ temp.getAbsolutePath());
			}
		}
	}

	/**
	 * 调用stop之后会返回保存录音的文件对象
	 */
	@Override
	public String stop() {
		String tempPath = null;
		if (recorder != null) {
			tempPath = pause();
		} else {
			tempPath = getTempFilePath();
		}
		stopped = true;
		return tempPath;
	}

	private boolean mergeFile(File head, File foot, File dest) {
		FileOutputStream fos = null;
		FileInputStream fisHead = null;
		FileInputStream fisFoot = null;
		boolean result = false;
		try {
			int HEAD_LEN = 6;// 文件头的长度，要拼接录音文件需要去掉第二个文件的文件头。
			int blockLen = 32 * 12;// 过渡段的长度，12帧每帧32个字节。拼接之后的音频有一小段声音突变，需要从第二个文件的头部去掉这个长度的数据以去掉突变。已测试过，12帧是至少的。
			fos = new FileOutputStream(dest);
			fisHead = new FileInputStream(head);
			fisFoot = new FileInputStream(foot);
			byte[] buf = new byte[512];
			int len = -1;
			while ((len = fisHead.read(buf)) != -1) {
				fos.write(buf, 0, len);
				fos.flush();
			}
			boolean first = true;
			while ((len = fisFoot.read(buf)) != -1) {
				if (first) {
					first = false;
					if (len > blockLen + HEAD_LEN)
						fos.write(buf, blockLen + HEAD_LEN, len - blockLen
								- HEAD_LEN);
					else
						fos.write(buf, HEAD_LEN, len - HEAD_LEN);
				} else {
					fos.write(buf, 0, len);
				}
				fos.flush();
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStreams(fos, fisHead, fisFoot);
		}
		return result;
	}

	private void closeStreams(Closeable... items) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				try {
					items[i].close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getRecordingFilePath() {
		return getBaseDirPath() + RECORDING_FILE_NAME;
	}

	private String getTempFilePath() {
		return getBaseDirPath() + TEMP_FILE_NAME;
	}

	private String getMergeFilePath() {
		return getBaseDirPath() + MERGE_FILE_NAME;
	}

	private String getBaseDirPath() {
		String ext = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String tempDirPath = ext + CommConst.MUSIC_FILE_PATH;
		File tempDir = new File(tempDirPath);
		if (tempDir.exists() && tempDir.isFile()) {
			throw new RuntimeException("目录文件已存在");
		}
		if (!tempDir.exists()) {
			if (!tempDir.mkdirs()) {
				throw new RuntimeException("couldnt make directory");
			}
		}
		return tempDirPath;
	}

	private void renameFile(File src, File dest) {
		if (!src.renameTo(dest)) {
			throw new RuntimeException("couldnt rename file "
					+ src.getAbsolutePath() + " to " + dest.getAbsolutePath());
		}
	}

	@Override
	public String pause() {
		if (stopped) {
			throw new IllegalStateException();
		}
		recorder.stop();
		recorder.release();
		recorder = null;

		// 获取保存的录音文件。如果有因为暂停产生的文件，则合并并重命名为temp文件，没有的话就直接重命名为temp文件
		String tempPath = getTempFilePath();
		String recordingPath = getRecordingFilePath();
		File tempFile = new File(tempPath);
		File recordingFile = new File(recordingPath);

		if (tempFile.exists()) {
			// 合并文件到getMergeFilePath()中
			File mergeFile = new File(getMergeFilePath());
			if (!mergeFile(tempFile, recordingFile, mergeFile)) {
				throw new RuntimeException("合并录音文件失败");
			}
			renameFile(mergeFile, tempFile);
			mergeFile.delete();
			recordingFile.delete();
		} else {
			renameFile(recordingFile, tempFile);
			recordingFile.delete();
		}

		return tempPath;
	}

	@Override
	public void resume() {
		if (stopped) {
			throw new IllegalStateException();
		}
		recorder = new MediaRecorder();
		recorder.setOutputFile(getRecordingFilePath());
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recorder.start();
	}

}
