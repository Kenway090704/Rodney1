package com.iwit.rodney.ui.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.activity.RecordActivity;

public class RecordSongAdapter extends Adapter<RecordSongViewHolder> implements
		OnClickListener {
	public static final int VIEW_TYPE_CREATION = 0;
	public static final int VIEW_TYPE_RECORD = 1;
	public static final int VIEW_TYPE_NORMAL = 2;
	public static final int POSITION_VIEW_TYPE_CREATION = 0;
	public static final int POSITION_VIEW_TYPE_RECORD = 1;
	/**
	 * 顶部固定可选项的数量,故事创作、故事录入等
	 */
	public static final int FIXED_OPTIONS_COUNT = 2;

	@Override
	public int getItemCount() {
		return FIXED_OPTIONS_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		switch (position) {
		case 0:
			return VIEW_TYPE_CREATION;
		case 1:
			return VIEW_TYPE_RECORD;
		default:
			return VIEW_TYPE_NORMAL;
		}
	}

	@Override
	public void onBindViewHolder(RecordSongViewHolder vh, int position) {
		vh.view.setTag(position);
	}

	@Override
	public RecordSongViewHolder onCreateViewHolder(ViewGroup vg, int type) {
		View view = LayoutInflater.from(vg.getContext()).inflate(
				R.layout.item_record_image, vg, false);
		switch (type) {
		case VIEW_TYPE_CREATION:
			view = LayoutInflater.from(vg.getContext()).inflate(
					R.layout.item_record_image, vg, false);
			((ImageView) view)
					.setBackgroundDrawable(vg.getContext().getResources().getDrawable(R.drawable.item_record_image_song_creation));
			view.setOnClickListener(this);
			break;
		case VIEW_TYPE_RECORD:
			view = LayoutInflater.from(vg.getContext()).inflate(
					R.layout.item_record_image, vg, false);
			((ImageView) view)
					.setBackgroundDrawable(vg.getContext().getResources().getDrawable(R.drawable.item_record_image_song_record));
			view.setOnClickListener(this);
			break;
		case VIEW_TYPE_NORMAL:

			break;
		}
		return new RecordSongViewHolder(view);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		switch (position) {
		case POSITION_VIEW_TYPE_CREATION:
			RecordActivity.jumpInto(v.getContext(),
					RecordActivity.RECORD_TYPE_CREATE_SONG, null);
			return;
		case POSITION_VIEW_TYPE_RECORD:
			RecordActivity.jumpInto(v.getContext(),
					RecordActivity.RECORD_TYPE_RECORD_SONG, null);
			return;
		default:
		}
	}

}
