package com.iwit.rodney.ui.fragment;

import java.util.List;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.iwit.rodney.bussess.MusicDas;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.ui.activity.BaseActivity;
import com.iwit.rodney.ui.adapter.MusicAdapter;

public class FavoriteFragment extends ListViewFragment {
	private String TAG = "SongChineseFragment";
	private List<Music> mMusicList;
	private MusicAdapter mMusicAdapter;
	private BaseActivity parentActivity;
	private String type;
	private int sort;
	private String[] args;

	public FavoriteFragment() {
		super(ListViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	public FavoriteFragment(String type, int sort, String TAG, String... args) {
		super(ListViewFragment.TYPE_JUST_RECYCLER_VIEW);
		this.type = type;
		this.sort = sort;
		this.TAG = TAG;
		this.args = args;
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
		// mMusicList = IManager.getIMusic().getMusicListFromDb(type, sort,
		// 4,args);
		mMusicList = MusicDas.getMusicListFav(sort, type);
		mMusicAdapter = new MusicAdapter(getActivity(), mMusicList);
		int pos = MusicListControl.getInstance().getCurPosition();
		updatePannel(pos);
		setAdapter();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
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
		updateList(position);
		mMusicAdapter.notifyDataSetChanged();
		MusicListControl.getInstance().setMusics(mMusicList);
		try {
			parentActivity.getmIMusicManager().playMusic(position);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updatePannel(int pos) {
		super.updatePannel(pos);
		Log.v(TAG, "pos:" + pos);
		List<Music> musics = MusicListControl.getInstance().getMusics();
		if (StringUtils.isListEmpty(mMusicList)
				|| StringUtils.isListEmpty(musics)) {
			return;
		}
		if (mMusicList == musics || mMusicList.size() == musics.size()) {
			if (mMusicList.get(pos).getMfname()
					.equals(musics.get(pos).getMfname())) {
				updateList(pos);
				mMusicAdapter.notifyDataSetChanged();
			} else {
				updateList(-1);
			}

		} else {
			updateList(-1);
			mMusicAdapter.notifyDataSetChanged();
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
}
