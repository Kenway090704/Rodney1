package com.iwit.rodney.ui.view;

import java.util.Arrays;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.iwit.rodney.R;

public class BgMusicDialog implements OnCheckedChangeListener {
	public interface OnMusicSelectListener {
		void onMusicSelected(int rawId);
		void dismiss();
		void back();
	}

	private PopupWindow pw;
	private View contentView;
	private int rbIds[] = new int[] { R.id.rb_dialog_record_bg_music_none,
			R.id.rb_dialog_record_bg_music_1, R.id.rb_dialog_record_bg_music_2,
			R.id.rb_dialog_record_bg_music_3, R.id.rb_dialog_record_bg_music_4 };
	private int rawIds[] = new int[] { 0, R.raw.bgsong01, R.raw.bgsong02,
			R.raw.bgsong03, R.raw.bgsong04 };
	private RadioButton[] rbs = new RadioButton[rbIds.length];
	private OnMusicSelectListener lis;

	public BgMusicDialog(Context context, OnMusicSelectListener lis) {
		this.lis = lis;
		contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_record_bg_music, null, false);

		pw = new PopupWindow(context);
		pw.setContentView(contentView);
		pw.setHeight((int) context.getResources().getDimension(
				R.dimen.width_height_dialog_record_bg_music));
		pw.setWidth((int) context.getResources().getDimension(
				R.dimen.width_width_dialog_record_bg_music));
		pw.setFocusable(true);
		pw.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.cover_grey_dialog));
		pw.setAnimationStyle(android.R.style.Animation_Dialog);

		for (int i = 0; i < rbs.length; i++) {
			rbs[i] = (RadioButton) contentView.findViewById(rbIds[i]);
			if (i > 0) {
				rbs[i].setText("背景音乐0" + i);
			}
			rbs[i].setOnCheckedChangeListener(this);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			int id = buttonView.getId();
			int index = Arrays.binarySearch(rbIds, id);
			if (index < 0) {
				throw new RuntimeException();
			}
			lis.onMusicSelected(rawIds[index]);
			contentView.postDelayed(new Runnable() {
				@Override
				public void run() {
					lis.back();
					//dismiss();
				}
			}, 100);
		}
	}

	public void show() {
		pw.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}

	public void dismiss() {
		pw.dismiss();
	}

	public boolean isShowing() {
		return pw.isShowing();
	}
}
