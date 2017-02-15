package com.iwit.rodney.ui.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.event.EventMusic;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.ui.activity.BaseActivity;
import com.iwit.rodney.ui.adapter.MusicAdapter;

import de.greenrobot.event.EventBus;

public class StorySearchFragment extends LazyFragment implements OnItemClickListener{

	private EditText mEtSearch;
	private List<Music> mMusicList;
	private MusicAdapter mMusicAdapter;
	private BaseActivity parentActivity;
	private ProgressDialog mProgressDialog;
	private ListView mLvMusic;
	private void showProgressDialog() {
		if(mProgressDialog == null){
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
	private void dismissDialog(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	
	@Override
	protected View getViewToLoad() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_songs_search, null, false);
		mEtSearch = (EditText) view.findViewById(R.id.et_etdit_search);
		mLvMusic = (ListView) view.findViewById(R.id.lv_songs);
		mEtSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					Log.v(TAG, "keyCode ::" + keyCode);
					requestMusicByName();
					if(mMusicAdapter != null && mMusicList != null){
						mMusicList.clear();
						mMusicAdapter.notifyDataSetChanged();
					}
				}

				return false;
			}
		});
		return view;
	}
	
	
	@Override
	protected void onViewAdded() {
		super.onViewAdded();
		parentActivity = (BaseActivity ) getActivity();
		EventBus.getDefault().register(this);
		
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
		if(mMusicAdapter != null){
			mMusicAdapter.unrigisterContentObserver();
		}
	}

	@Override
	protected void onFragmentVisible() {
		Log.v(TAG, "onFragmentVisible");
		if(mMusicAdapter != null){
			mMusicAdapter.rigisterContentObserver();
		}
		super.onFragmentVisible();
	}

	
	
	private void requestMusicByName(){
		String name = mEtSearch.getText().toString();
		if(StringUtils.isEmpty(name)){
			Toast.makeText(getActivity(), "请输入搜索名称", 1).show();
		}else{
			IManager.getIMusic().getSearchMusicList(name,2);
			//缓冲列表
			showProgressDialog();
		}
	}
	public void onEventMainThread(Event event) {
		switch(event.getType()){
		case Event.EVENT_GET_MUSIC_BY_SEARCH:
			dismissDialog();
			if(event.isResult()){
				EventMusic mevent = (EventMusic) event;
				mMusicList = mevent.getmMusicList();
				Log.v(TAG, "size:"+mMusicList.size());
				mMusicAdapter = new MusicAdapter(getActivity(),mMusicList);
				mLvMusic.setAdapter(mMusicAdapter);
				mLvMusic.setOnItemClickListener(this);
			}else{
				Toast.makeText(getActivity(), event.getMsg(), 1).show();
			}
			break;
		}
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
	private void updateList(int pos){
		for(int i = 0;i<mMusicList.size();i++){
			if(i == pos){
				Music music = mMusicList.get(i);
				music.setIschoose(1);
			}else{
				Music music = mMusicList.get(i);
				music.setIschoose(0);
			}
		}
	}
}
