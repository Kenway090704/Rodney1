package com.iwit.rodney.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.iwit.rodney.ui.adapter.RecordSongAdapter;

public class RecordSongFragment extends RecyclerViewFragment {

	public RecordSongFragment() {
		super(RecyclerViewFragment.TYPE_JUST_RECYCLER_VIEW);
	}

	@Override
	protected LayoutManager getLayoutManager() {
		return new LinearLayoutManager(getActivity());
	}

	@Override
	protected Adapter<?> getRecyclerViewAdapter() {
		return new RecordSongAdapter();
	}

}
