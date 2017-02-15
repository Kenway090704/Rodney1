package com.iwit.rodney.ui.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.downloader.provider.Downloads;
import com.iwit.rodney.entity.Book;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.interfaces.IDownload;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.Izip;
import com.iwit.rodney.ui.view.DownloadControlView;


import de.greenrobot.event.EventBus;

/**
 * 书籍下载界面RecyclerView的适配器。由于每一项视图包含多种状态--多种下载状态和解压状态，所以逻辑较复杂。
 * 此处试图将书籍的下载状态拆分成展示和处理两块代码，彼此互不干扰。 书籍的下载状态从IDownload接口获取。
 * <p>
 * 1.下载状态的展示和提示
 * <p>
 * {@link #onBindViewHolder(BookViewHolder, int)}
 * 方法在获取书籍的下载状态之后，负责更新视图和对用户做出提示。不对书籍做任何业务处理。
 * <p>
 * 2.下载状态处理
 * <p>
 * 此类仅在用户点击了书籍的情况下对书籍的下载状态做出处理，然后在必要情况下通知视图更新。
 */
public class BookAdapter extends Adapter<BookViewHolder> implements
		OnClickListener ,OnLongClickListener{
	public static final String TAG = "BookStoreAdapter";

	private List<Book> books;
	private Context context;
	private IDownload iDownload;
	private Handler handler;
	private Izip izip;
	/**
	 * 记录书籍状态的数组，用于前后对比是否书籍的状态发生改变
	 */
	private int[] preStatus;
	boolean isStore;
	public BookAdapter(Context context, List<Book> books,boolean isStore) {
		this.context = context;
		this.books = books;
		this.isStore = isStore;
		handler = new Handler();
		iDownload = IManager.getIDownload(context.getApplicationContext());
		izip = IManager.getIzip(context);
	}

	@Override
	public int getItemCount() {
		return books == null ? 0 : books.size();
	}

	@Override
	public void onBindViewHolder(BookViewHolder vh, int position) {
		initBooksPreState();
		vh.getBookView().setTag(position);
		vh.getDelView().setTag(position);
		Book b = books.get(position);

		final int status = iDownload.getDownloadStatus(b);
		Log.e(TAG, b.getBname() + " " + status);

		updateBookView(vh, b, status);
		setDeleteStatus(vh , b);
		if (isBookStateChanged(status, position)) {
			// 当状态有改变时才会执行信息显示，避免每次视图更新都提示一下。
			showMsg(status);
			checkDownloadCount();
		}

		if (status == Downloads.Impl.STATUS_RUNNING
				|| status == Downloads.Impl.STATUS_PENDING) {// 书籍正在下载，由于要更新界面的进度条，所以要实现连续的更新
			invalidateDelay();
		}

		preStatus[position] = status;
	}

	private boolean isBookStateChanged(int status, int position) {
		return status != preStatus[position];
	}

	/**
	 * 初始化记录书籍状态的数组，用于前后对比是否书籍的状态发生改变
	 */
	private void initBooksPreState() {
		if (preStatus == null || preStatus.length != getItemCount()) {
			preStatus = new int[getItemCount()];
		}
	}

	private void showMsg(int status) {
		if (Downloads.Impl.isStatusError(status)) {
			Toast.makeText(context,
					String.format("下载失败,错误码:%s。请检查手机状态后重试", status),
					Toast.LENGTH_SHORT).show();
		} else {
			switch (status) {
			case Downloads.Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
				Toast.makeText(context, "存储空间不足，请检查后重试", Toast.LENGTH_SHORT)
						.show();
				return;
			case Downloads.Impl.STATUS_DEVICE_NOT_FOUND_ERROR:
				// 可能是SD卡没有挂载
				Toast.makeText(context, "找不到SD卡，请检查后重试", Toast.LENGTH_SHORT)
						.show();
				return;
			case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
				Toast.makeText(context, "网络未连接，请检查网络状态", Toast.LENGTH_SHORT)
						.show();
				return;
			case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
				Toast.makeText(context, "当前要下载的文件较大，请连接wifi后重试",
						Toast.LENGTH_SHORT).show();
				return;
			case Downloads.Impl.STATUS_WAITING_TO_RETRY:
				Toast.makeText(context, "当前网络状态不佳，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		}
	}

	private void updateBookView(BookViewHolder vh, Book b, int status) {
		vh.setName(b.getBname());

		int state = translateToViewState(status);
		vh.setDownloadingState(state);

		if (!Downloads.Impl.isStatusCompleted(status)) {
			int progress = iDownload.getDownloadProgress(b);
			vh.setProgress(progress);
		}

		WebRequest.loadImage(vh.itemView.getContext(),
				CommConst.SIT_ROOT_FILE_URL + b.getCoverpic(), vh.getPicView());
	}

	private void invalidateDelay() {
		handler.removeCallbacks(invalidate);
		handler.postDelayed(invalidate, 500);
	}

	private int translateToViewState(int status) {
		if (status == 0) {
			return DownloadControlView.STATE_READY_TO_DOWNLOAD;
		} else if (Downloads.Impl.isStatusInformational(status)) {
			if (Downloads.Impl.STATUS_RUNNING == status
					|| Downloads.Impl.STATUS_PENDING == status) {
				return DownloadControlView.STATE_READY_TO_PAUSE;
			} else {
				return DownloadControlView.STATE_READY_TO_RESUME;
			}
		} else {
			return DownloadControlView.STATE_READY_TO_PLAY;
		}
	}

	@Override
	public BookViewHolder onCreateViewHolder(ViewGroup vg, int type) {
		View v = LayoutInflater.from(vg.getContext()).inflate(
				R.layout.item_book, vg, false);
		BookViewHolder vh = new BookViewHolder(v);

		vh.getBookView().setOnClickListener(this);
		vh.getBookView().setOnLongClickListener(this);
		vh.getDelView().setOnClickListener(this);
		
		return vh;
	}

	private Runnable invalidate = new Runnable() {
		@Override
		public void run() {
			notifyDataSetChanged();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_book_view:// 即vh.getBookView()返回的view
			int position = (Integer) v.getTag();
			onBookClick(position);
			break;
		case R.id.iv_book_delete:// 即vh.getBookView()返回的view
			int pos = (Integer) v.getTag();
			deleteBook(pos);
			break;
		default:
			break;
		}
	}

	private void onBookClick(int position) {
		Book b = books.get(position);
		int status = iDownload.getDownloadStatus(b);
		Log.e(TAG, b.getBname() + " " + status);
		if (status == 0) {
			// 此状态下，直接下载文件。
			// 如果文件已存在，只是app数据被用户清除了。那样的情况交由IDownload接口去实现。这里只管当前的状态对应到下载。
			iDownload.download(b);
		} else if (Downloads.Impl.isStatusSuccess(status)) {
			// 此状态下，查看书籍解压状态，启动书籍解压或者解压完毕的情况下打开书籍
			String zipFilePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ CommConst.ROOT_BOOK_PATH
					+ b.getBfname();
			int state = izip.getFileState(zipFilePath);
			switch (state) {
			case Izip.STATE_UNKNOW_FILE:
				izip.unzip(zipFilePath, CommConst.ROOT_BOOK_PATH.substring(0,
						CommConst.ROOT_BOOK_PATH.length() - 1));
			case Izip.STATE_UNZIPING:
				Toast.makeText(context, "正在装载书籍内容，请稍后再打开", Toast.LENGTH_SHORT)
						.show();
				break;
			case Izip.STATE_UNZIPED:
				// TODO 怎么知道是否成功打开了？如果打开失败该怎么处理？
				//此处报错
//				MoueeReader.show(context,
//						zipFilePath.substring(0, zipFilePath.lastIndexOf("."))
//								+ File.separator + "book");
				// 进入书本前，通知停止音乐
				EventBus.getDefault().post(
						new Event(Event.EVENT_MUSIC_STOP, null, false));
				break;
			case Izip.STATE_UNZIP_FAILED:
				// 如果解压失败，则移除下载好的任务，清除解压状态，准备重新下载。
				Toast.makeText(context, "装载书籍失败，可能文件受损，点击重新下载",
						Toast.LENGTH_SHORT).show();
				izip.removeFileState(zipFilePath);
				iDownload.removeDownload(b);
				break;
			default:
				throw new UnknownError();
			}
		} else if (Downloads.Impl.isStatusInformational(status)) {
			if (Downloads.Impl.STATUS_RUNNING == status
					|| Downloads.Impl.STATUS_PENDING == status) {
				iDownload.pause(b);
			} else {
				// 在这个状态下，书本目前被用户操作或者其他原因导致暂停下载。
				// TODO 并不是所有情况都测试过是否可以resume
				iDownload.resume(b);
			}
		} else if (Downloads.Impl.isStatusError(status)) {
			iDownload.removeDownload(b);
		}
		notifyDataSetChanged();
	}

	private void checkDownloadCount() {
		int downloadCount = iDownload.getDownloadingTaskCount();
		if (downloadCount >= 3) {
			Toast.makeText(context, "同时打开多个任务可能会导致下载缓慢", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	private void deleteBook(int position){
		Book b = books.get(position);
		//删除下载数据库
		boolean isDel = iDownload.deleteDownload(b);
		if(isDel){
			books.remove(b);
			Toast.makeText(context, "删除成功!", 1).show();
			notifyDataSetChanged();
		}else{
			Toast.makeText(context, "删除失败!", 1).show();
		}
		
	}
	
	private void setDeleteStatus(BookViewHolder vh, Book b){
		if(b.getDeleted() == 0){
			vh.getDelView().setVisibility(View.GONE);
			vh.getMaskView().setVisibility(View.GONE);
		}else{
			vh.getDelView().setVisibility(View.VISIBLE);
			vh.getMaskView().setVisibility(View.VISIBLE);
		}
	}
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.rl_book_view:
			int position = (Integer) v.getTag();
			Book b = books.get(position);
			if(isStore){
				//删除图标
				b.setDeleted(1);
				notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
		return true;
	}
}
