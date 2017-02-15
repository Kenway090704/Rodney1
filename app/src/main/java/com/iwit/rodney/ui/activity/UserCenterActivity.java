package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwit.rodney.R;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.imp.IAccountImp;

public class UserCenterActivity extends Activity implements OnClickListener {
	private LinearLayout mLlSet, mLlAccount;
	private ImageView mIvBack;
	private TextView mTvName;
	private IAccountImp account;
	private Boolean isLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_center);
		account = new IAccountImp(this);
		initView();
	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mLlSet = (LinearLayout) findViewById(R.id.ll_set);
		mLlSet.setOnClickListener(this);
		mLlAccount = (LinearLayout) findViewById(R.id.ll_account);
		mLlAccount.setOnClickListener(this);
		mTvName = (TextView) findViewById(R.id.tv_myaccount);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Account currentAccount = account.getCurrentAccount();
		if (currentAccount != null) {
			mTvName.setText(currentAccount.getEmail());
			isLogin = true;
		} else {
			mTvName.setText(getResources().getString(R.string.label_account));
			isLogin = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;

		case R.id.ll_set:
			Intent setIntent = new Intent(UserCenterActivity.this,
					SetActivity.class);
			startActivity(setIntent);
			break;
		case R.id.ll_account:
			if (!isLogin) {
				Intent accountIntent = new Intent(UserCenterActivity.this,
						LoginActivity.class);
				startActivity(accountIntent);
			} else {
				Intent accountIntent = new Intent(UserCenterActivity.this,
						LoginSuccessActivity.class);
				startActivity(accountIntent);
			}
			break;
		}

	}
}
