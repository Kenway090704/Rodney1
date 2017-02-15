package com.iwit.rodney.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwit.rodney.R;

public abstract class ListViewFragment extends LazyFragment implements OnItemClickListener{
	public static final int TYPE_JUST_RECYCLER_VIEW = 0;
	private int mType;
	private BaseAdapter mAdapter;
	private static final String TAG = "ListViewFragment";
	public ListViewFragment(int type) {
		if (type != 0) {
			throw new IllegalArgumentException();
		}
		mType = type;
	}
	
	protected abstract BaseAdapter getListViewAdapter();
	private ListView lv;
	private TextView tv;
	private RelativeLayout frame;
	private View loadListView() {
		lv = new ListView(getActivity());
		lv.setDivider(null);
		lv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		return lv;
	}
	
	protected void setAdapter(){
		mAdapter = (BaseAdapter)getListViewAdapter();
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
	}
	@Override
	protected View getViewToLoad() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_list, null, false);
		lv = (ListView) view.findViewById(R.id.lv_songs);
		tv = (TextView)view.findViewById(R.id.loading);
		frame = (RelativeLayout)view.findViewById(R.id.frame_loading);
		return view;
	}
	public void setTvVisible(int isVisible){
		if(frame != null){
			frame.setVisibility(isVisible);
		}
		
	}
}
