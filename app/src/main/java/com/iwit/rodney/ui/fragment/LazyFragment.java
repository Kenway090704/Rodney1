package com.iwit.rodney.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwit.rodney.R;

public abstract class LazyFragment extends Fragment {
	public static final String TAG = "LazyFragment";
	private View mView;
	private boolean mIsFirstVisible = true;
	private boolean mIsVisible;
	private boolean mFirstDelay = false;
	private boolean mIsViewLoaded = false;
	private TextView mTvBase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void updatePannel(int pos){
	}
	public void notifyView(){
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_lazy_base, container,
					false);
			mTvBase = (TextView) mView
					.findViewById(R.id.tv_fragment_base_loading);
			setUserVisibleHint(mIsVisible);
		}
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		mIsVisible = isVisibleToUser;
		if (isVisibleToUser && mView != null) {
			if (mIsFirstVisible) {
				onFragmentFirstVisible();
				mIsFirstVisible = false;
			} else {
				onFragmentVisible();
			}
		} else {
			onFragmentInvisible();
		}
	}

	private void onFragmentFirstVisible() {
		if (mView != null) {
			mView.postDelayed(new Runnable() {
				@Override
				public void run() {
					View v = getViewToLoad();
					if (v != null) {
						((RelativeLayout) mView).addView(v);
						if (mTvBase != null) {
							((RelativeLayout) mView).removeView(mTvBase);
						}
						mIsViewLoaded = true;
						onViewAdded();
					}
				}
			}, mFirstDelay ? 200 : 0);
			mFirstDelay = false;
		}
	}

	public void setFirstDelay() {
		if (!mIsViewLoaded) {
			mFirstDelay = true;
		}
	}

	protected abstract View getViewToLoad();

	protected void onViewAdded() {
	}

	protected void onFragmentVisible() {
	}

	protected void onFragmentInvisible() {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mView != null) {
			((ViewGroup) mView.getParent()).removeView(mView);
		}
	}

	public boolean isFragmentVisible() {
		return mIsVisible;
	}

}
