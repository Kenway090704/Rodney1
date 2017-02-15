package com.iwit.rodney.ui.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.downloader.provider.Downloads;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.interfaces.IDownload;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.activity.RecordActivity;

public class RecordListAdapter extends
		RecyclerView.Adapter<RecordListViewHolder> implements OnClickListener {
	public static final String TAG = "RecordListAdapter";
	private List<Story> stories;
	private IDownload iDownload;
	private Context context;
	private Handler handler;

	public RecordListAdapter(Context context, List<Story> storys) {
		this.stories = storys;
		this.context = context;
		iDownload = IManager.getIDownload(context.getApplicationContext());
		handler = new Handler();
	}

	@Override
	public int getItemCount() {
		return stories == null ? 0 : stories.size();
	}

	@Override
	public void onBindViewHolder(RecordListViewHolder vh, int position) {
		Story s = stories.get(position);
		int status = iDownload.getDownloadStatus(s);

		status = processStatus(s, status);

		vh.setViewDownloaded(status == Downloads.Impl.STATUS_SUCCESS);
		vh.setTitle(s.getName());
		vh.rl.setTag(position);

		String requestUrl = CommConst.SIT_ROOT_FILE_URL + s.getPicName();
		WebRequest.loadImage(vh.itemView.getContext(), requestUrl,
				vh.getImagePic(), new String[] { "has corner" });
	}

	private int processStatus(Story s, int status) {
		Log.e(TAG, "processStatus:" + status);
		switch (status) {
		case Downloads.Impl.STATUS_SUCCESS:
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + s.getLocalPath() + s.getLrcName());
			if (!file.exists()) {
				iDownload.removeDownload(s);
				status = 0;
			}
			return status;
		case Downloads.Impl.STATUS_RUNNING:
		case Downloads.Impl.STATUS_PENDING:
			handler.removeCallbacks(invalidate);
			handler.postDelayed(invalidate, 500);
			return status;
		case Downloads.Impl.STATUS_DEVICE_NOT_FOUND_ERROR:
			Toast.makeText(context, "找不到存储设备，请检查您的存储设备并点击重试",
					Toast.LENGTH_SHORT).show();
			return status;
		case Downloads.Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
			Toast.makeText(context, "存储空间不足,请检查后重试", Toast.LENGTH_SHORT)
					.show();
			return status;
		case Downloads.Impl.STATUS_BAD_REQUEST:
			// 这个错误一般是由于地址错误。在保证前面的实现代码正确的情况下，由于我们下载地址是固定的，所以此错误一般无法通过代码修复。
			Toast.makeText(context, "请求地址错误", Toast.LENGTH_SHORT).show();
			return status;
		case Downloads.Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
			Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
			return status;
		case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
			Toast.makeText(context, "网络未连接，将在连接网络之后自动下载", Toast.LENGTH_SHORT)
					.show();
			return status;
		case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
			Toast.makeText(context, "当前要下载的文件较大，将在连接wifi后自动下载",
					Toast.LENGTH_SHORT).show();
			return status;
		case Downloads.Impl.STATUS_WAITING_TO_RETRY:
			Toast.makeText(context, "当前网络状态不佳，请稍后重试", Toast.LENGTH_SHORT)
					.show();
			return status;
		}
		return status;
	}

	@Override
	public RecordListViewHolder onCreateViewHolder(ViewGroup vg, int type) {
		View view = LayoutInflater.from(vg.getContext()).inflate(
				R.layout.record_story, vg, false);
		RecordListViewHolder vh = new RecordListViewHolder(view);
		vh.rl.setOnClickListener(this);
		return vh;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_record_story:
			int position = (Integer) v.getTag();
			Story s = stories.get(position);
			int status = iDownload.getDownloadStatus(s);
			if (status == Downloads.Impl.STATUS_SUCCESS)
				jumpToRecordActivity(s);
			else
				processDownloadStatusWhileClick(s, status);
			break;
		default:
			break;
		}
	}

	private void jumpToRecordActivity(Story s) {
		String mid = s.getMid();
		Intent intent = new Intent(context, RecordActivity.class);
		intent.putExtra("mid", mid);
		context.startActivity(intent);
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

	private Runnable invalidate = new Runnable() {
		@Override
		public void run() {
			notifyDataSetChanged();
		}
	};
}
