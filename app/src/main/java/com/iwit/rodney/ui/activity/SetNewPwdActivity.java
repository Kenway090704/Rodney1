package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.iwit.rodney.R;

public class SetNewPwdActivity extends Activity implements OnClickListener {
	private ImageView mIvBack;
	private EditText mEtNewPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_newpwd);
		initView();
	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mEtNewPwd = (EditText) findViewById(R.id.et_new_pwd);
		mEtNewPwd.getPaint().setFakeBoldText(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		}

	}
}
