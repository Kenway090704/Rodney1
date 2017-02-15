package com.iwit.rodney.ui.fragment;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.NetWorkTools;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.downloader.downloadmgr.IwitDownloadManager;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.event.EventMusic;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.ui.activity.BaseActivity;
import com.iwit.rodney.ui.activity.MusicHomeActivity;
import com.iwit.rodney.ui.adapter.MusicAdapter;

import de.greenrobot.event.EventBus;

public class CommonListFragment extends ListViewFragment {
	private String TAG = "SongChineseFragment";
	private List<Music> mMusicList;
	private MusicAdapter mMusicAdapter;
	private BaseActivity parentActivity;
	private String type;
	private int sort;
	// ---
	private boolean isEn = false;
	private ProgressDialog mProgressDialog;
	
	public CommonListFragment() {
		super(ListViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	public CommonListFragment(String type, int sort, String TAG) {
		super(ListViewFragment.TYPE_JUST_RECYCLER_VIEW);
		this.type = type;
		this.sort = sort;
		this.TAG = TAG;
		// ---
		if (TAG.equals("SongEnglishFragment")) {
			this.isEn = true;
		}
	}

	@Override
	protected BaseAdapter getListViewAdapter() {
		return mMusicAdapter;
	}

	@Override
	protected void onViewAdded() {
		super.onViewAdded();
		Log.v(TAG, "onViewAdded");
		parentActivity = (BaseActivity) getActivity();
		EventBus.getDefault().register(this);
		showProgressDialog();
		// 英文儿歌特殊处理
		if (isEn) {
			IManager.getIMusic().getMusicByType(type, sort, "en");
		} else {
			IManager.getIMusic().getMusicByType(type, sort);
		}
	}

	public void onEventMainThread(Event event) {
		switch (event.getType()) {
		case Event.EVENT_GET_MUSIC_BY_TYPE:
			dismissDialog();
			if (isFragmentVisible()) {
				if (event.isResult()) {
					EventMusic mevent = (EventMusic) event;
					mMusicList = mevent.getmMusicList();
					Log.v(TAG, "size:" + mMusicList.size());
					mMusicAdapter = new MusicAdapter(getActivity(), mMusicList);
					setAdapter();
					int pos = MusicListControl.getInstance().getCurPosition();
					updatePannel(pos);
				} else {
					Toast.makeText(getActivity(), event.getMsg(), 1).show();
				}
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected void onFragmentInvisible() {
		super.onFragmentInvisible();
		Log.v(TAG, "onFragmentInvisible");
		if (mMusicAdapter != null) {
			mMusicAdapter.unrigisterContentObserver();
		}
	}

	@Override
	protected void onFragmentVisible() {
		Log.v(TAG, "onFragmentVisible");
		if (mMusicAdapter != null) {
			mMusicAdapter.rigisterContentObserver();
		}
		int pos = MusicListControl.getInstance().getCurPosition();
		updatePannel(pos);
		super.onFragmentVisible();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(mMusicList == null){
			return;
		}
		Music music = mMusicList.get(position);
		int network = NetWorkTools.getAPNType(parentActivity);
		if(network == -1 && music.getDownloaded() < 4){
			Toast.makeText(parentActivity, 
					parentActivity.getString(R.string.notify_web), 1).show();
		}
		updateList(position);
		mMusicAdapter.notifyDataSetChanged();
		MusicListControl.getInstance().setMusics(mMusicList);
		try {
			parentActivity.getmIMusicManager().playMusic(position);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		downloadLrc(music);
	}
	private void downloadLrc(Music music){
		String lrcName = music.getMname() + ".lrc";
		String url_file = CommConst.SIT_ROOT_FILE_URL + music.getLyric();
		
		String lrc = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ CommConst.MUSIC_LRC_PATH
				+ music.getMname() + ".lrc";
		File file = new File(lrc);
		if(!file.exists()){
			if(music.getMid().startsWith("s1")){
				//判断是够正在下载或者是否有该文件
				IwitDownloadManager.getInstance(getActivity()
						.getApplicationContext()).startDownload(url_file, lrcName,
						CommConst.MUSIC_LRC_PATH);
			}
		}
	}
	@Override
	public void updatePannel(int pos) {
		super.updatePannel(pos);
		Log.v(TAG, "pos:" + pos);
		if (parentActivity == null || parentActivity.getMsgService() == null) {
			return;
		}
		List<Music> musics = MusicListControl.getInstance().getMusics();
		if (StringUtils.isListEmpty(mMusicList)
				|| StringUtils.isListEmpty(musics)) {
			updateList(-1);
			return;
		}
		if (mMusicList == musics || mMusicList.size() == musics.size()) {
			if (mMusicList.get(pos).getMfname()
					.equals(musics.get(pos).getMfname())) {
				updateList(pos);
			} else {
				updateList(-1);
			}
		} else {
			updateList(-1);
		}
		if(mMusicAdapter != null){
			mMusicAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void notifyView(){
		if(mMusicAdapter != null){
			mMusicAdapter.notifyDataSetChanged();
			mMusicAdapter.initFirstDataToBean();
		}
	}
	private void updateList(int pos) {
		if (mMusicList == null) {
			return;
		}
		if (pos == -1) {
			for (int i = 0; i < mMusicList.size(); i++) {
				Music music = mMusicList.get(i);
				music.setIschoose(0);
			}
		} else {
			for (int i = 0; i < mMusicList.size(); i++) {
				if (i == pos) {
					Music music = mMusicList.get(i);
					music.setIschoose(1);
				} else {
					Music music = mMusicList.get(i);
					music.setIschoose(0);
				}
			}
		}
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage("加载中...");
			mProgressDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			// 设置进度条不明确
			mProgressDialog.setIndeterminate(false);
			// 设置进度条是否可按返回键取消加载
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
			mProgressDialog.getWindow().setContentView(R.layout.wait_dialog);
		}
	}

	private void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
}
