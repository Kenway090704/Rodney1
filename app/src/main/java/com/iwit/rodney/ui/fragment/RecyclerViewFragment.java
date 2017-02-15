package com.iwit.rodney.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.adapter.RecordLocalAdapter;

public abstract class RecyclerViewFragment extends LazyFragment implements
		TextWatcher, OnClickListener, OnTouchListener {
	public static final int TYPE_JUST_RECYCLER_VIEW = 0;
	public static final int TYPE_RECYCLER_VIEW_WITH_SEARCH = 1;
	private int mType;
	private Adapter<?> mAdapter;
	private RecyclerView mRv;
	public void deleteCancel(){
		
	};
	public RecyclerViewFragment(int type) {
		if (type < TYPE_JUST_RECYCLER_VIEW
				|| type > TYPE_RECYCLER_VIEW_WITH_SEARCH) {
			throw new IllegalArgumentException();
		}
		mType = type;
	}

	@Override
	protected View getViewToLoad() {
		switch (mType) {
		case TYPE_JUST_RECYCLER_VIEW:
			return loadRecyclerView();
		case TYPE_RECYCLER_VIEW_WITH_SEARCH:
			return loadRecyclerViewWithSearch();
		}
		return null;
	}

	private View loadRecyclerView() {
		mRv = new RecyclerView(getActivity());
		mRv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mRv.setLayoutManager(getLayoutManager());
		mAdapter = getRecyclerViewAdapter();
		mRv.setAdapter(mAdapter);
		mRv.setOnClickListener(this);
		return mRv;
	}

	protected LayoutManager getLayoutManager() {
		return new GridLayoutManager(getActivity(), 3);
	}

	protected abstract RecyclerView.Adapter<?> getRecyclerViewAdapter();

	private View loadRecyclerViewWithSearch() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.edit_recyclerview, null, false);

		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mRv = (RecyclerView) view.findViewById(R.id.rv_edit_recyclerview);
		mRv.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
					onRecyclerViewStateChangeToDragging();
				}
			}
		});
		mRv.setOnTouchListener(this);
		LayoutManager lm = new GridLayoutManager(getActivity(), 3);
		mRv.setLayoutManager(lm);
		mAdapter = getRecyclerViewAdapter();
		mRv.setAdapter(mAdapter);

		etSearch = (EditText) view.findViewById(R.id.et_etdit_recyclerview);
		etSearch.addTextChangedListener(this);

		rlCancel = (RelativeLayout) view
				.findViewById(R.id.rl_edit_recyclerview_cancel);
		rlCancel.setOnClickListener(this);
		return view;
	}

	private RelativeLayout rlCancel;
	private EditText etSearch;

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String text = s.toString();

		rlCancel.setVisibility(text.length() == 0 ? View.GONE : View.VISIBLE);

		RecordLocalAdapter adapter = (RecordLocalAdapter) mAdapter;
		adapter.getSearchInterface().search(text, false);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onClick(View v) {
		if (v == rlCancel) {
			etSearch.getText().clear();
			closeInputKeyboard(getActivity());
			mRv.requestFocus();
		}else if(v == mRv){
			Log.e("RecyclerViewFragment", "onTouch");
		}
	}

	public static void closeInputKeyboard(Activity act) {
		InputMethodManager imm = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(act.getWindow().getDecorView()
				.getWindowToken(), 0);
	}

	@Override
	protected void onFragmentVisible() {
		if (mRv != null) {
			mRv.requestFocus();
		}
	}

	protected void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.e("RecyclerViewFragment", "onTouch");
			clearscreen();
		}
		return false;
	}

	protected void onRecyclerViewStateChangeToDragging() {
		clearscreen();
	}

	protected void clearscreen() {
		Log.e(TAG, "clearscreen");
		// recyclerView clearFocus的时候，EditText会抢到focus，所以再次requestFocus
		mRv.clearFocus();
		mRv.requestFocus();
		closeInputKeyboard(getActivity());
	}
}
