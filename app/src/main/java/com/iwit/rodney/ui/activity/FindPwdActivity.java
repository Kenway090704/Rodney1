package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;

import de.greenrobot.event.EventBus;

public class FindPwdActivity extends Activity implements OnClickListener {
	private ImageView mIvBack, mIvCheck;
	private EditText mEtEmail;
	private String emailStr;
	private IAccountImp accountImp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwd);
		EventBus.getDefault().register(this);
		accountImp = new IAccountImp(this);
		initView();
	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mIvCheck = (ImageView) findViewById(R.id.iv_check);
		mIvCheck.setOnClickListener(this);
		mEtEmail = (EditText) findViewById(R.id.et_email);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_check:
			emailStr = mEtEmail.getText().toString().trim();
			if (emailStr != null && !emailStr.equals("")
					&& StringUtils.isEmail(emailStr)) {
				accountImp.findPwd(emailStr);
			} else {
				Toast.makeText(
						FindPwdActivity.this,
						getResources().getString(
								R.string.toast_account_correctemail),
						Toast.LENGTH_SHORT).show();

			}
			break;

		}

	}

	public void onEventMainThread(EventAccount eventAccount) {
		int result = eventAccount.getResult();
		String msg = eventAccount.getMsg();
		Toast.makeText(FindPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
		if (result == 1) {
			finish();
		}
	}
}
