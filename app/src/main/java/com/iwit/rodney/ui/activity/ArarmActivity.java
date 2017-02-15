package com.iwit.rodney.ui.activity;

import com.iwit.rodney.ui.view.ArarmDialog;

import android.app.Activity;
import android.os.Handler;

public class ArarmActivity extends Activity{

	@Override
	protected void onResume() {
		super.onResume();
		final ArarmDialog dialog = new ArarmDialog(this);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				dialog.show();
				finish();
			}
		}, 1000);
	}

}
