package com.iwit.rodney.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iwit.rodney.comm.utils.MusicPlayer;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.event.EventRecord;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.ui.activity.BaseActivity;
import com.iwit.rodney.ui.adapter.RecordLocalAdapter;
import com.iwit.rodney.ui.adapter.RecordLocalAdapter.OnRecordClickListener;

import de.greenrobot.event.EventBus;

public class RecordLocalFragment extends RecyclerViewFragment implements
		OnRecordClickListener {

	private RecordLocalAdapter adapter;
	private List<Record> records = new ArrayList<Record>();

	public RecordLocalFragment() {
		super(RecyclerViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	@Override
	protected Adapter<?> getRecyclerViewAdapter() {
		initDataView();
		adapter = new RecordLocalAdapter(getActivity(), records, this);
		return adapter;
	}

	@Override
	protected LayoutManager getLayoutManager() {
		return new LinearLayoutManager(getActivity());
	}

	private void updateData() {
		records.clear();
		List<Record> rs = IManager.getIRecord().getRecords();
		if (rs != null && rs.size() > 0) {
			records.addAll(rs);
		}

		addRecordsToPlayerList();
	}

	private void addRecordsToPlayerList() {
		BaseActivity b = getParentBaseActivity();
		b.getMsgService().pause1Music();
		MusicListControl.getInstance().setRecords(records);
	}
	
	//init records 
	private void initDataView(){
		records.clear();
		List<Record> rs = IManager.getIRecord().getRecords();
		if (rs != null && rs.size() > 0) {
			records.addAll(rs);
		}
		initRecordsToPlayerList();
	}
	private void initRecordsToPlayerList() {
		List<Music> musics = MusicListControl.getInstance().getMusics();
		if(!StringUtils.isListEmpty(musics)){
			Music music = musics.get(0);
			if(music.getIsrecord() == 1){
				BaseActivity b = getParentBaseActivity();
				b.getMsgService().pause1Music();
				MusicListControl.getInstance().setRecords(records);
			}
		}
	}
	
	private BaseActivity getParentBaseActivity() {
		Activity act = getActivity();
		if (!(act instanceof BaseActivity))
			throw new RuntimeException(
					"parent activity should be a BaseActivity");

		BaseActivity b = (BaseActivity) act;
		return b;
	}

	@Override
	protected void onFragmentVisible() {
		super.onFragmentVisible();
		initDataView();
		adapter.notifyRecordDataChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		MusicPlayer.get(getActivity()).release();
		super.onPause();
	}

	public void onEventMainThread(EventRecord event) {
		if (adapter != null) {
			initDataView();
			adapter.notifyRecordDataChanged();
		}
	}

	@Override
	public void onRecordClick(int position) {
		addRecordsToPlayerList();
		//设置当前的
		BaseActivity b = getParentBaseActivity();
		try {
			b.getmIMusicManager().playMusic(position);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
