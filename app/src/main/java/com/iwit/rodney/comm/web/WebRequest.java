package com.iwit.rodney.comm.web;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.iwit.rodney.App;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.AsyncImageDownloader;
import com.iwit.rodney.comm.tools.BitmapUtils;
import com.iwit.rodney.comm.tools.OnImageDownload;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WebRequest {

	/**
	 * 网络请求处理
	 * 
	 * @param recall
	 * @param method
	 * @param params
	 */
	public static void requestFromWeb(AsyncHttpResponseHandler recall,
			String method, String[]... params) {
		final String url = CommConst.SIT_ROOT_URL + method;
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);// 设置为10秒超时
		RequestParams req = new RequestParams();
		req.add("lang", EnumLang.getSubUrlByCountryCode(App.getCountryCode()));

		if (params != null && params.length > 0) {
			for (String[] param : params) {
				if (checkParam(param)) {
					req.add(param[0], param[1]);
				}
			}
		}
		client.post(url, req, recall);
	}
	public static void requestFromWeb2(AsyncHttpResponseHandler recall,
			String method, String[]... params) {
		final String url = CommConst.SIT_ROOT_URL + method;
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);// 设置为10秒超时
		RequestParams req = new RequestParams();

		if (params != null && params.length > 0) {
			for (String[] param : params) {
				if (checkParam(param)) {
					req.add(param[0], param[1]);
				}
			}
		}
		client.post(url, req, recall);
	}
	/**
	 * 网络请求歌词 140731eeb782ac.lrc
	 */
	public static void requestFromWeb(AsyncHttpResponseHandler recall,
			String name) {
		final String url = CommConst.SIT_ROOT_FILE_URL + name;
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);// 设置为10秒超时
		client.post(url, null, recall);
	}

	/**
	 * 检查参数
	 * 
	 * @param param
	 */
	private static boolean checkParam(String[] param) {
		if (param == null) {
			return false;
		}
		if (param.length != 2) {
			return false;
		}

		if (StringUtils.isEmpty(param[0]) || StringUtils.isEmpty(param[1])) {
			return false;
		}
		return true;
	}

	public static void loadImage(Context ctx, String requestUrl,
			final ImageView imageView,final String... args) {
		imageView.setTag(requestUrl);
		AsyncImageDownloader.getInstance(ctx).imageDownload(requestUrl,
				imageView, CommConst.ROOT_PIC_PATH, new OnImageDownload() {

					@Override
					public void onDownloadSucc(Bitmap bitmap, String c_url,
							ImageView imageView) {
						if (bitmap != null && !bitmap.isRecycled()) {
							if(c_url .equals(imageView.getTag()) ){
								if (imageView != null) {
									if(args != null && args.length != 0){
										imageView.setImageBitmap(bitmap);
									}else{
										imageView.setImageBitmap(bitmap);
									}
								}
							}
						}
					}
				},args);
	}

}
