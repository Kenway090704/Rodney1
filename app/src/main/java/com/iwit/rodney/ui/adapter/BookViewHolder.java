package com.iwit.rodney.ui.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.view.DownloadControlView;
import com.iwit.rodney.ui.view.LoadingView;

public class BookViewHolder extends ViewHolder {
	private TextView tvName;
	private View cover;
	private DownloadControlView dcv;
	private LoadingView lv;
	private ImageView picView,delView,maskView;
	private RelativeLayout rlBookView;

	public BookViewHolder(View view) {
		super(view);
		rlBookView = (RelativeLayout) view.findViewById(R.id.rl_book_view);
		delView = (ImageView) view.findViewById(R.id.iv_book_delete);
		maskView= (ImageView) view.findViewById(R.id.iv_book_mask);
		
		tvName = (TextView) view.findViewById(R.id.tv_book_name);
		cover = view.findViewById(R.id.view_book_cover);
		dcv = (DownloadControlView) view.findViewById(R.id.dcv_book);
		lv = (LoadingView) view.findViewById(R.id.loadview_book);
		picView = (ImageView) view.findViewById(R.id.iv_book_picture);
		
	}

	public void setProgress(int progress) {
		lv.setProgress(progress);
	}

	public void setDownloadingState(int state) {
		dcv.setState(state);

		int visibility = state == DownloadControlView.STATE_READY_TO_DOWNLOAD
				|| state == DownloadControlView.STATE_READY_TO_PLAY ? View.GONE
				: View.VISIBLE;

		lv.setVisibility(visibility);
		cover.setVisibility(visibility);
	}

	public void setName(String name) {
		tvName.setText(name);
	}

	public ImageView getPicView() {
		return picView;
	}

	public View getBookView() {
		return rlBookView;
	}
	public ImageView getDelView(){
		return delView;
	}
	public ImageView getMaskView(){
		return maskView;
	}
}
