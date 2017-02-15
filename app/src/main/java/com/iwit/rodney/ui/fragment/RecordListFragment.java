package com.iwit.rodney.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.event.EventStory;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.adapter.RecordStoryAdapter;

import de.greenrobot.event.EventBus;

public class RecordListFragment extends RecyclerViewFragment {
	private List<Story> stories = new ArrayList<Story>();
	private RecordStoryAdapter adapter;

	public RecordListFragment() {
		super(RecyclerViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected Adapter<?> getRecyclerViewAdapter() {
		getData();
		adapter = new RecordStoryAdapter(getActivity(), stories);
		return adapter;
	}

	@Override
	protected LayoutManager getLayoutManager() {
		return new LinearLayoutManager(getActivity());
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	public void onEventMainThread(EventStory event) {
		if (!event.isResult()) {
			if(event.getMsg() != null){
				Toast.makeText(getActivity(), event.getMsg(), Toast.LENGTH_SHORT)
				.show();
			}
		} else {
			getData();
			adapter.notifyDataSetChanged();
		}
		dismissDialog();
	}

	private void getData() {
		showProgressDialog();
		List<Story> ss = IManager.getIStory().getStories();
		stories.clear();
		if (ss != null) {
			stories.addAll(ss);
		}
	}
	
	
	private ProgressDialog mProgressDialog;
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
