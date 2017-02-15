package com.iwit.rodney.comm.tools;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * 从缓存、磁盘、网络读取图片
 * @author wangsongtao?
 * @author tao 整理
 *
 */
public class AsyncImageDownloader {

	private static final String TAG = "ImageDownloader";
	private Context mContext;
	private ImageMemoryCache memoryCache;
	private ImageFileCache fileCache;
	private static AsyncImageDownloader mAsyncImageDownloader;
	private HashMap<String, MyAsyncTask> map = new HashMap<String, MyAsyncTask>();
	private static ExecutorService LIMITED_TASK_EXECUTOR;  
	//用5个线程去加载图片
	static {  
        LIMITED_TASK_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(3);  
    };  
	/**
	 * 
	 * @param context
	 */
	public AsyncImageDownloader(Context context) {
		this.mContext = context;

	}
	/**
	 * 单例的下载类
	 * @param mContext
	 * @return
	 */
	public static AsyncImageDownloader getInstance(Context mContext) {

		if (null == mAsyncImageDownloader) {
			mAsyncImageDownloader = new AsyncImageDownloader(mContext);
		}

		return mAsyncImageDownloader;

	}
	/**
	 * 下载图片的方法 通过接口进行OnImageDownload异步回传
	 * @param url 资源访问全路径
	 * @param imageView 加载到界面组件
	 * @param targetDir 保存到文件夹
	 * @param recall 下载完成后回调
	 */
	public void imageDownload(String url, ImageView imageView, String targetDir, OnImageDownload recall,String... args) {
		if (null == memoryCache) {
			memoryCache = new ImageMemoryCache(mContext);
		}
		
		if (imageView != null && url.equals(imageView.getTag()) ) {//equals为确保imageView对象的Tag被赋值了？
			Bitmap bitmap = memoryCache.getBitmapFromCache(url);//首先从缓存中取
			
			if (bitmap != null) {
				Log.e("qh", "从缓存取得图片。"+url);
				if(args != null && args.length != 0){
					imageView.setImageBitmap(bitmap);
				}else{
					imageView.setImageBitmap(bitmap);
				}
			} else {
				if (null == fileCache) {
					fileCache = new ImageFileCache(targetDir);
				}
				bitmap = fileCache.getImage(url);//其次从文件中取
				
				if (bitmap != null) {
					Log.e("qh", "从sd卡中取得图片"+url);
					if(args != null && args.length != 0){
						imageView.setImageBitmap(bitmap);
					}else{
						imageView.setImageBitmap(bitmap);
					}
					memoryCache.addBitmapToCache(url, bitmap);
				} else if (url != null && needCreateNewTask(imageView)) {
					//最后，异步下载的内容加载到imageView中
					MyAsyncTask task = new MyAsyncTask(url, imageView, recall);
					task.executeOnExecutor(LIMITED_TASK_EXECUTOR);
					map.put(url, task);
				}
			}
		}
	}
	/**
	 * 如果需要进行网络请求的时候
	 * @param mImageView
	 * @return
	 */
	private boolean needCreateNewTask(ImageView mImageView) {
		boolean b = true;
		if (mImageView != null) {
			String curr_task_url = (String) mImageView.getTag();
			if (isTasksContains(curr_task_url)) {
				b = false;
			}
		}
		return b;
	}

	private boolean isTasksContains(String url) {
		boolean b = false;
		if (map != null && map.get(url) != null) {
			b = true;
		}
		return b;
	}

	private void removeTaskFormMap(String url) {
		if (url != null && map != null && map.get(url) != null) {
			map.remove(url);
		}
	}

	private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;
		private String url;
		private OnImageDownload download;

		public MyAsyncTask(String url, ImageView mImageView,
				OnImageDownload download) {
			this.mImageView = mImageView;
			this.url = url;
			this.download = download;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap data = null;
			if (url != null) {
				try {
					data = ImageGetFromHttp.downloadBitmap(url);
					//保存到缓存中
					if (!fileCache.saveBitmap(data, url)) {
						fileCache.removeBitmapFromFile(url);
					}
					memoryCache.addBitmapToCache(url, data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return data;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			
			if (download != null) {
				download.onDownloadSucc(result, url, mImageView);
				Log.e("qh", "从网络取得图片。");
			}
			removeTaskFormMap(url);
			super.onPostExecute(result);
		}

	}

}
