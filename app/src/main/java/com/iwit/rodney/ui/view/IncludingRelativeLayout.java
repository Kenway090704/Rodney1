package com.iwit.rodney.ui.view;

import android.view.View;
import android.view.View.OnClickListener;

public class IncludingRelativeLayout {
	public static final String TAG = "IncludingRelativeLayout";
	private View rl;
	private View include;

	public IncludingRelativeLayout(View rl, int includeId) {
		this.rl = rl;
		this.include = rl.findViewById(includeId);
	}

	public void setEnabled(boolean enabled) {
		rl.setEnabled(enabled);
		include.setEnabled(enabled);
	}

	public void setOnClickListener(OnClickListener lis) {
		rl.setOnClickListener(lis);
	}

}
