package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;

import de.greenrobot.event.EventBus;

public class LoginActivity extends Activity implements OnClickListener {
	private ImageView mIvBack, mIvLogin;
	private TextView mTvForgetPwd, mTvRegister;
	private EditText mEtPhone, mEtPwd;
	private IAccountImp accountImp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		accountImp = new IAccountImp(this);
		EventBus.getDefault().register(this);
		initView();
	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mIvLogin = (ImageView) findViewById(R.id.iv_login);
		mIvLogin.setOnClickListener(this);
		mTvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
		mTvForgetPwd.setOnClickListener(this);
		Paint paint = mTvForgetPwd.getPaint();
		paint.setColor(getResources().getColor(R.color.link_blue));
		paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mEtPhone = (EditText) findViewById(R.id.et_phone);
		mEtPwd = (EditText) findViewById(R.id.et_pwd);
		mEtPhone.getPaint().setFakeBoldText(true);
		mEtPwd.getPaint().setFakeBoldText(true);
		mTvRegister = (TextView) findViewById(R.id.tv_register);
		mTvRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_register:
			Intent registIntent = new Intent(LoginActivity.this,
					RegistActivity.class);
			startActivity(registIntent);
			LoginActivity.this.finish();
			break;
		case R.id.tv_forget_pwd:
			Intent forgetPwdIntent = new Intent(LoginActivity.this,
					FindPwdActivity.class);
			startActivity(forgetPwdIntent);
			break;
		case R.id.iv_login:
			login();
			break;
		}

	}

	private void login() {
		String phonenum = mEtPhone.getText().toString().trim();
		String password = mEtPwd.getText().toString().trim();
		if (phonenum == null || "".equals(phonenum)) {
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.toast_no_emailinfo),
					Toast.LENGTH_SHORT).show();
		} else if (password == null || "".equals(password)) {
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.toast_no_password),
					Toast.LENGTH_SHORT).show();
		} else {
			Account account = new Account();
			account.setEmail(phonenum);
			account.setPassword(password);
			accountImp.login(account);
		}

	}

	public void onEventMainThread(EventAccount eventAccount) {
		String msg = eventAccount.getMsg();
		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();

	}

}
