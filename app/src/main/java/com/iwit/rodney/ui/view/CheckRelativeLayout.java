package com.iwit.rodney.ui.view;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class CheckRelativeLayout implements OnClickListener {
	public static final String TAG = "RelativeLayoutCheckBoxTextView";
	private View rl;
	private CheckBox cb;
	private OnCheckedChangeListener listener;
	private boolean checked;

	public static interface OnCheckedChangeListener {
		void onCheckedChanged(View v, boolean checked);
	}

	public CheckRelativeLayout(View rl, View cb) {
		this.rl = rl;
		this.cb = (CheckBox) cb;

		rl.setOnClickListener(this);
	}

	public void setChecked(boolean checked) {
		if (this.checked == checked) {
			return;
		}
		this.checked = checked;
		cb.setChecked(checked);

		if (listener != null) {
			listener.onCheckedChanged(rl, checked);
		}
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener lis) {
		listener = lis;
	}

	@Override
	public void onClick(View v) {
		Log.e(TAG, "onclick");
		setChecked(!checked);
	}

	public void setEnabled(boolean enabled) {
		rl.setEnabled(enabled);
		cb.setEnabled(enabled);
	}

}
