package com.iwit.rodney.ui.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.IRecord;
import com.iwit.rodney.ui.fragment.RecordLocalFragment;
import com.iwit.rodney.ui.view.RecordPanel;

public class RecordLocalAdapter extends Adapter<RecordLocalViewHolder>
		implements OnLongClickListener, OnClickListener, OnFocusChangeListener {
	public static final String TAG = "RecordLocalAdapter";

	public interface OnRecordClickListener {
		public void onRecordClick(int position);
	}

	public interface Searchable {
		void search(String value, boolean useLastValue);
	}

	private List<Record> records;
	private List<Record> searchResult = new ArrayList<Record>();
	private boolean mIsSearching;
	private Context context;
	private IRecord iRecrod;
	private OnRecordClickListener mListener;
	private Set<RecordLocalViewHolder> vhs = new HashSet<RecordLocalViewHolder>();

	/**
	 * 当用户需要搜索某些东西时，她只需要从Adapter中获取搜索接口，并调用接口的方法输入她要搜索的数据。
	 * 
	 * @return 搜索接口
	 */
	public Searchable getSearchInterface() {
		return mSearcher;
	}

	public void notifyRecordDataChanged() {
		if (mIsSearching)
			mSearcher.search(null, true);
		else {
			closeInputKeyboard();
			notifyDataSetChangedWrapper();
		}
	}

	public RecordLocalAdapter(Context context, List<Record> stories,
			OnRecordClickListener lis) {
		this.context = context;
		this.records = stories;
		mListener = lis;
		iRecrod = IManager.getIRecord();
		vhs.clear();
	}

	@Override
	public int getItemCount() {
		if (mIsSearching) {
			return searchResult.size();
		} else {
			return records == null ? 0 : records.size();
		}
	}

	@Override
	public void onBindViewHolder(final RecordLocalViewHolder vh, int position) {
		// vh.rlLayout.setTag(position);
		// vh.et.setTag(position);
		// vh.rlDelete.setTag(position);
		vh.view.setTag(position);
		// 在数据发生改变的时候应该清除vhs这个容器。但是由于不能重写notifyDataSetChanged,所以重写一个方法包含notifyDataSetChanged
		vhs.add(vh);
		List<Record> rs = getCurrentRecrods();
		// 正常情况下，这里ss不应该为null，且ss的size不可能小于position。所以不做判断。
		Record r = rs.get(position);
		vh.tvTitle.setText(r.getName());
		// vh.setStoryName(r.getName());
		// vh.setViewDeleting(false);// 更新数据之后，初始化视图为非deleting状态

		// Music m = iRecrod.getCachedMusic(r.getMid());
		String picName = r.getPic_name();
		if (picName != null) {
			String requestUrl = CommConst.SIT_ROOT_FILE_URL + picName;
			WebRequest.loadImage(vh.itemView.getContext(), requestUrl,
					vh.ivTitle, new String[] { "has corner" });
		}
	}

	private List<Record> getCurrentRecrods() {
		List<Record> rs = null;
		if (mIsSearching) {
			rs = searchResult;
		} else {
			rs = records;
		}
		return rs;
	}

	@Override
	public RecordLocalViewHolder onCreateViewHolder(ViewGroup vg, int type) {
		Log.e(TAG, "onCreateViewHolder");
		View v = LayoutInflater.from(vg.getContext()).inflate(
				R.layout.item_story, vg, false);
		RecordLocalViewHolder vh = new RecordLocalViewHolder(v);
		v.setOnClickListener(this);
		// vh.rlLayout.setOnLongClickListener(this);
		// vh.rlLayout.setOnClickListener(this);
		// vh.et.setOnFocusChangeListener(this);
		// vh.rlDelete.setOnClickListener(this);
		return vh;
	}

	@Override
	public boolean onLongClick(View v) {
		int position = (Integer) v.getTag();// 获取onBindViewHolder方法中设置的tag
		setViewDeletingState(position);
		return true;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		onRecordClick(position);
//		switch (v.getId()) {
//		case R.id.rl_my_story:
//			return;
//		case R.id.rl_my_story_delete:
//			Record r = getCurrentRecrods().get(position);
//			boolean result = iRecrod.delete(r);
//			if (!result) {
//				throw new RuntimeException("要删除的记录不存在");
//			}
//			clearViewDeletingStates();
//			return;
//		}

	}

	private void onRecordClick(int position) {
		// 播放前应该将所有视图恢复正常状态
		// if (clearViewStates()) {
		// return;
		// }
		if (mListener != null) {
			mListener.onRecordClick(position);
		}

		// 停止正在播放的音乐
		// EventBus.getDefault().post(
		// new Event(Event.EVENT_MUSIC_STOP, null, false));
		//
		// File file = new File(Environment.getExternalStorageDirectory()
		// .getAbsolutePath()
		// + CommConst.MUSIC_FILE_PATH
		// + r.getName()
		// + ".amr");
		// MusicPlayer.get(context).start(file);
	}

	@Override
	public void onFocusChange(final View v, boolean hasFocus) {
		int position = (Integer) v.getTag();// 获取onBindViewHolder方法中设置的tag
		final Record r = getCurrentRecrods().get(position);
		if (hasFocus) {
			clearViewDeletingStates();
		} else {
			final String name = ((EditText) v).getText().toString();
			v.post(new Runnable() {// 稍后执行重命名录音文件的操作
				@Override
				public void run() {
					if (r.getName().equals(name)) {
						return;
					}
					if (!RecordPanel.checkRecordName(context, name)) {
						closeInputKeyboard();
						notifyDataSetChangedWrapper();
						return;
					}

					boolean result = iRecrod.rename(r, name);
					String str = "重命名成功";
					if (!result) {
						str = "重命名失败，命名重复";
						((EditText) v).setText(r.getName());
					}
					Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public void clearViewDeletingStates() {
		// Iterator<RecordLocalViewHolder> it = vhs.iterator();
		// while (it.hasNext()) {
		// it.next().setViewDeleting(false);
		// }
	}

	// private boolean clearViewStates() {
	// 清除编辑状态和删除状态
	// Iterator<RecordLocalViewHolder> it = vhs.iterator();
	// RecordLocalViewHolder vh = null;
	// boolean result = false;
	// while (it.hasNext()) {
	// vh = it.next();
	// vh.setViewDeleting(false);
	// if (vh.et.isFocused()) {
	// vh.et.clearFocus();
	// closeInputKeyboard();
	// result = true;
	// }
	// }
	// return result;
	// }

	private void setViewDeletingState(int position) {
		// 当有一个视图进入删除状态时，其他视图必须恢复正常状态
		// clearViewStates();
		//
		// Iterator<RecordLocalViewHolder> it = vhs.iterator();
		// RecordLocalViewHolder vh = null;
		// while (it.hasNext()) {
		// vh = it.next();
		// if (vh.position == position) {
		// vh.setViewDeleting(true);
		// }
		// }
	}

	/**
	 * 因为无法重写notifyDataSetChanged方法，又需要监听数据变化的通知，所以添加一个方法包含notifyDataSetChanged
	 */
	private void notifyDataSetChangedWrapper() {
		vhs.clear();
		notifyDataSetChanged();
	}

	private void closeInputKeyboard() {
		RecordLocalFragment.closeInputKeyboard((Activity) context);
	}

	/**
	 * 当用户需要搜索某些东西时，她只需要从Adapter中获取这个接口，并调用接口的方法输入她要搜索的数据。
	 */
	private Searchable mSearcher = new Searchable() {
		private String kw;

		@Override
		public void search(String value, boolean useLastValue) {
			if (!useLastValue)
				kw = value;
			check();

			if (kw.length() == 0) {
				mIsSearching = false;
				closeInputKeyboard();
				notifyDataSetChangedWrapper();
				return;
			}

			mIsSearching = true;
			run();
			notifyDataSetChangedWrapper();
		}

		private void check() {
			if (Looper.getMainLooper() != Looper.myLooper()) {
				throw new RuntimeException("应该在主线程调用这个方法");
			}
			if (kw == null) {
				throw new NullPointerException("请保证要搜索的值不为null");
			}
		}

		private void run() {
			searchResult.clear();
			if (records != null && records.size() > 0) {
				int size = records.size();
				String name = null;
				for (int i = 0; i < size; i++) {
					name = records.get(i).getName();
					if (name == null) {
						throw new NullPointerException("请保证书名不能为null");
					}
					if (name.contains(kw)) {
						searchResult.add(records.get(i));
					}
				}
			}
		}
	};
}
