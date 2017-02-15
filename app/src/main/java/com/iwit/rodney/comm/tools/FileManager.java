package com.iwit.rodney.comm.tools;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

public class FileManager {
	private String TAG = "FileManager";
	private Map<String, MyUnZipTask> taskMap = new HashMap<String, MyUnZipTask>();//管理解压
	private static FileManager mFileManager;
	private FileUtils mFileUtils;
	/**
	 * 构造函数
	 * 这里应该使用私有的比较好---Kenway 2,14
	 */
	public FileManager() {
		mFileUtils = FileUtils.getInstance();
	}

	/**
	 * 单例的实例对象
	 * 
	 * @return
	 */
	public static FileManager getInstance() {
		if (null == mFileManager) {
			mFileManager = new FileManager();
		}
		return mFileManager;
	}
	/**
	 * 解压缩 方法 
	 * @param filename zip包的名称
	 * @param filepath zip
	 * @param unZipPath 解压缩到的路径
	 * @param mOnUnZipCompleteListener
	 */
	public void unZipFile(String filename, String filepath, String unZipPath,
			OnUnZipCompleteListener mOnUnZipCompleteListener) {
		
		if (filepath != null && filename != null && unZipPath != null) {
			String path = filepath + filename;
			Log.v("upZipFile", "解压开始"+path);
			if (needCreateMyUnZipTask(path)) {
				Log.v("upZipFile", "创建解压线程");
				MyUnZipTask task = new MyUnZipTask(filename, filepath,
						unZipPath, mOnUnZipCompleteListener);
				task.execute();
				taskMap.put(path, task);
			}else{
				Log.e("upZipFile", "正在解压当前的文件");
				//得到当前的解压任务 重新将接口赋值给当前线程
				taskMap.get(path).mOnUnZipCompleteListener = mOnUnZipCompleteListener;
			}
		}

	}
	/**
	 * 是否需要创建解压线程
	 * @param path
	 * @return
	 */
	private boolean needCreateMyUnZipTask(String path) {

		if (path != null) {
			if (taskMap != null && taskMap.get(path) != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 移除解压线程
	 * @param path
	 */
	private void removeTaskFormMap(String path){
        if(path != null && taskMap != null && taskMap.get(path) != null){
        	taskMap.remove(path);
        }
    }
	/**
	 * 解压线程
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyUnZipTask extends AsyncTask<String, Void, Boolean> {
		private String filename;
		private String filepath;
		private String unZipPath;
		private OnUnZipCompleteListener mOnUnZipCompleteListener;

		public MyUnZipTask(String filename, String filepath, String unZipPath,
				OnUnZipCompleteListener mOnUnZipCompleteListener) {
			this.filename = filename;
			this.filepath = filepath;
			this.unZipPath = unZipPath;
			this.mOnUnZipCompleteListener = mOnUnZipCompleteListener;
		}

		@Override
		protected Boolean doInBackground(String... filePath) {
			Log.v("upZipFile", "调用解压文件工具类");
			boolean isUnZipSuccess = mFileUtils.unZipFile(filename, filepath , unZipPath);
			return isUnZipSuccess;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			String path = filepath + filename;
			mOnUnZipCompleteListener.onSuccess(result, path);
			removeTaskFormMap(path);
		}

	}
}
