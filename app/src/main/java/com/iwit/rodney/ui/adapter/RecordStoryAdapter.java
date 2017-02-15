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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.downloader.provider.Downloads;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.interfaces.IDownload;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.activity.RecordActivity;

public class RecordStoryAdapter extends Adapter<RecordStoryViewHolder>
		implements OnClickListener {
	public static final String TAG = "RecordStoryAdapter";
	public static final int VIEW_TYPE_CREATION = 0;
	public static final int VIEW_TYPE_RECORD = 1;
	public static final int POSITION_VIEW_TYPE_CREATION = 0;
	public static final int POSITION_VIEW_TYPE_RECORD = 1;
	public static final int VIEW_TYPE_NORMAL = 2;
	/**
	 * 顶部固定可选项的数量,故事创作、故事录入等
	 */
	public static final int FIXED_OPTIONS_COUNT = 2;

	private IDownload iDownload;

	private List<Story> ss;
	private Context context;
	private Handler handler;

	public RecordStoryAdapter(Context context, List<Story> ss) {
		this.ss = ss;
		this.context = context;
		handler = new Handler();
		iDownload = IManager.getIDownload(context.getApplicationContext());
	}

	@Override
	public int getItemCount() {
		return FIXED_OPTIONS_COUNT + (ss == null ? 0 : ss.size());
	}

	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case 0:
			return VIEW_TYPE_CREATION;
		case 1:
			return VIEW_TYPE_RECORD;
		default:
			return VIEW_TYPE_NORMAL;
		}
	}

	@Override
	public void onBindViewHolder(RecordStoryViewHolder vh, int position) {
		vh.view.setTag(position);
		if (position < FIXED_OPTIONS_COUNT) {
			return;
		}
		int index = position - FIXED_OPTIONS_COUNT;
		Story s = ss.get(index);

		int status = iDownload.getDownloadStatus(s);
		handleStatus(s, status);
		updateView(vh, status, index);

		String requestUrl = CommConst.SIT_ROOT_FILE_URL + s.getPicName();
		WebRequest.loadImage(vh.itemView.getContext(), requestUrl, vh.ivTitle,
				new String[] { "has corner" });
	}

	private void updateView(RecordStoryViewHolder vh, int status, int index) {
		Story s = ss.get(index);
		boolean result = Downloads.Impl.isStatusSuccess(status);
		vh.ivMic.setVisibility(result ? View.VISIBLE : View.GONE);
		vh.tvTitle.setText(s.getName());
	}

	private void handleStatus(Story s, int status) {
		switch (status) {
		case Downloads.Impl.STATUS_SUCCESS:
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + s.getLocalPath() + s.getLrcName());
			if (!file.exists()) {
				iDownload.removeDownload(s);
			}
			break;
		case Downloads.Impl.STATUS_RUNNING:
		case Downloads.Impl.STATUS_PENDING:
			handler.removeCallbacks(invalidate);
			handler.postDelayed(invalidate, 500);
			break;
		case Downloads.Impl.STATUS_DEVICE_NOT_FOUND_ERROR:
			Toast.makeText(context, "找不到存储设备，请检查您的存储设备并点击重试",
					Toast.LENGTH_SHORT).show();
			break;
		case Downloads.Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
			Toast.makeText(context, "存储空间不足,请检查后重试", Toast.LENGTH_SHORT).show();
			break;
		case Downloads.Impl.STATUS_BAD_REQUEST:
			// 这个错误一般是由于地址错误。在保证前面的实现代码正确的情况下，由于我们下载地址是固定的，所以此错误一般无法通过代码修复。
			Toast.makeText(context, "请求地址错误", Toast.LENGTH_SHORT).show();
			break;
		case Downloads.Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
			Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
			break;
		case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
			Toast.makeText(context, "网络未连接，将在连接网络之后自动下载", Toast.LENGTH_SHORT)
					.show();
			break;
		case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
			Toast.makeText(context, "当前要下载的文件较大，将在连接wifi后自动下载",
					Toast.LENGTH_SHORT).show();
			break;
		case Downloads.Impl.STATUS_WAITING_TO_RETRY:
			Toast.makeText(context, "当前网络状态不佳，请稍后重试", Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public RecordStoryViewHolder onCreateViewHolder(ViewGroup vg, int type) {
		View view = LayoutInflater.from(vg.getContext()).inflate(
				R.layout.item_record_image, vg, false);
		switch (type) {
		case VIEW_TYPE_CREATION:
			view = LayoutInflater.from(vg.getContext()).inflate(
					R.layout.item_record_image, vg, false);
			((ImageView) view).setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_story_creation));
			break;
		case VIEW_TYPE_RECORD:
			view = LayoutInflater.from(vg.getContext()).inflate(
					R.layout.item_record_image, vg, false);
			((ImageView) view).setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_story_record));
			break;
		case VIEW_TYPE_NORMAL:
			view = LayoutInflater.from(vg.getContext()).inflate(
					R.layout.item_story, vg, false);
			break;
		}
		RecordStoryViewHolder vh = new RecordStoryViewHolder(view);
		vh.view.setOnClickListener(this);
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
		int position = (Integer) v.getTag();
		switch (position) {
		case POSITION_VIEW_TYPE_CREATION:
			RecordActivity.jumpInto(context,
					RecordActivity.RECORD_TYPE_CREATE_STORY, null);
			return;
		case POSITION_VIEW_TYPE_RECORD:
			RecordActivity.jumpInto(context,
					RecordActivity.RECORD_TYPE_RECORD_STORY, null);
			return;
		default:
			int index = position - FIXED_OPTIONS_COUNT;
			Story s = ss.get(index);

			int status = iDownload.getDownloadStatus(s);
			if (Downloads.Impl.isStatusSuccess(status))
				RecordActivity.jumpInto(context,
						RecordActivity.RECORD_TYPE_SHOW_STORY, s.getMid());
			else
				processDownloadStatusWhileClick(s, status);
			return;
		}
	}

	private void processDownloadStatusWhileClick(Story s, int status) {
		Log.e(TAG, "processDownloadStatusWhileClick:" + status);
		if (status == 0) {
			iDownload.download(s);
			handler.postDelayed(invalidate, 500);
		} else if (Downloads.Impl.STATUS_PENDING == status
				|| Downloads.Impl.STATUS_RUNNING == status) {
			Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show();
		} else if (Downloads.Impl.isStatusCompleted(status)) {
			// 此处是下载完成了，但是状态不是下载成功，所以删除任务重新下载。
			iDownload.removeDownload(s);
			iDownload.download(s);
			handler.postDelayed(invalidate, 500);
		} else if (Downloads.Impl.STATUS_WAITING_TO_RETRY == status) {// 已测试，此状态可以被resume
			iDownload.resume(s);
			handler.postDelayed(invalidate, 500);
		}
	}
}
