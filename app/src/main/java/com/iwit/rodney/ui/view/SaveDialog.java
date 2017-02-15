package com.iwit.rodney.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.iwit.rodney.R;

public class SaveDialog implements OnClickListener {
	private SharedPreferences sp;
	public static final String SP_NAME = "record";
	public static final String SP_KEY_RECORD_INDEX = "record_index";

	public interface OnSavedListener {
		void onSave(String name);
	}

	private PopupWindow pw;
	private View contentView;
	private Button btnSave;
	private Button btnCancel;
	private EditText etName;
	private OnSavedListener lis;

	public SaveDialog(Context context, OnSavedListener lis) {
		sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

		pw = new PopupWindow(context);

		contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_save, null, false);

		pw.setContentView(contentView);
		pw.setHeight(LayoutParams.MATCH_PARENT);
		pw.setWidth(LayoutParams.MATCH_PARENT);
		pw.setAnimationStyle(android.R.style.Animation_Dialog);
		pw.setFocusable(true);

		btnSave = (Button) contentView.findViewById(R.id.btn_dialog_save_save);
		btnCancel = (Button) contentView
				.findViewById(R.id.btn_dialog_save_cancel);
		etName = (EditText) contentView.findViewById(R.id.et_dialog_save_name);

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		this.lis = lis;
	}

	public void show() {
		etName.setText("录音" + sp.getInt(SP_KEY_RECORD_INDEX, 1));
		pw.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}

	public void dismiss() {
		pw.dismiss();
	}

	public void isShowing() {
		pw.isShowing();
	}

	@Override
	public void onClick(View v) {
		int next = sp.getInt(SP_KEY_RECORD_INDEX, 1) + 1;
		sp.edit().putInt(SP_KEY_RECORD_INDEX, next).commit();
		if (v == btnSave && lis != null) {
			lis.onSave(etName.getText().toString());
		}
		dismiss();
		etName.getText().clear();
	}
}
