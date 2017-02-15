package com.iwit.rodney.imp;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Context;

import com.iwit.rodney.App;
import com.iwit.rodney.bussess.BookDas;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.web.EnumLang;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.downloader.provider.Downloads;
import com.iwit.rodney.entity.Book;
import com.iwit.rodney.event.EventBook;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IBook;
import com.iwit.rodney.interfaces.IDownload;
import com.iwit.rodney.interfaces.IManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class IBookImpl implements IBook {

	@Override
	public List<Book> getLocalBooks(Context context) {
		List<Book> bs = BookDas.getBooks(IManager.getIAccount()
				.getCurrentAccountRestypes(), 0);
		IDownload iDownload = IManager.getIDownload(context.getApplicationContext());
		if (bs != null && bs.size() > 0) {
			int size = bs.size();
			Book b = null;
			for (int i = 0; i < size; i++) {
				b = bs.get(i);
				if (iDownload.getDownloadStatus(b) != Downloads.Impl.STATUS_SUCCESS) {
					bs.remove(i);
					i--;
					size--;
				}
			}
		}
		return bs;
	}

	@Override
	public List<Book> getStoreBooks() {
		try {
			List<Book> bs = BookDas.getBooks(IManager.getIAccount()
					.getCurrentAccountRestypes(), 0);
			return bs;
		} finally {
			lookForNewStoreBooks();
		}
	}

	private void lookForNewStoreBooks() {
		WebRequest.requestFromWeb(
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] arg1,
							byte[] responseBody) {
						if (statusCode != 200) {
							onWebRequestResult(false, "当前网络信号较差，请稍后重试");
							return;
						}
						String result = new String(responseBody);
						onWebRequestResult(true, result);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable erro) {
						onWebRequestResult(false, "当前网络信号较差，请稍后重试");
					}
				},
				CommConst.REQUEST_BOOKLIST_METHOD,
				new String[][] {
						{
								"lang",
								EnumLang.getSubUrlByCountryCode(App
										.getCountryCode()) },
						{ "platform", "2" },
						{ "synctime", BookDas.getLastSyncTime() } });
	}

	private void onWebRequestResult(final boolean result, final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (result) {
					Map<String, Object> kvs = null;
					try {
						kvs = JsonUtil.jsonString2Map(data);
					} catch (IwitApiException e) {
						e.printStackTrace();
					}
					saveSyncTime(kvs);
					int bookCount = 0;
					try {
						bookCount = Integer.parseInt((String.valueOf(kvs
								.get(CommConst.KEY_RESLUT))));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (bookCount > 0) {
						BookDas.saveBooks(kvs);
						EventBus.getDefault().post(new EventBook(true, null));
					}
				} else {
					EventBus.getDefault().post(new EventBook(false, data));
				}
			}

			private void saveSyncTime(Map<String, Object> kvs) {
				Object time = kvs.get("synctime");
				if (time != null) {
					BookDas.updateSynctime(time.toString());
				}
			}
		}).start();
	}
}
