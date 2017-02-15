package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;

import de.greenrobot.event.EventBus;

public class RegistActivity extends Activity implements OnClickListener {
	private ImageView mIvBack, mIvRegist;
	private EditText mEtEmail, mEtPwd1, mEtPwd2, mEtName, mEtAge;
	private TextView mTvUserPro;
	private IAccountImp accountImp;
	private Button mBtnBoy, mBtnGirl;
	private int sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		accountImp = new IAccountImp(this);
		EventBus.getDefault().register(this);
		initView();

	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mEtEmail = (EditText) findViewById(R.id.et_email);
		mEtPwd1 = (EditText) findViewById(R.id.et_pwd1);
		mEtPwd2 = (EditText) findViewById(R.id.et_pwd2);
		mEtName = (EditText) findViewById(R.id.et_name);
		mEtAge = (EditText) findViewById(R.id.et_age);
		mEtEmail.getPaint().setFakeBoldText(true);
		mEtPwd1.getPaint().setFakeBoldText(true);
		mEtPwd2.getPaint().setFakeBoldText(true);
		mEtName.getPaint().setFakeBoldText(true);
		mEtAge.getPaint().setFakeBoldText(true);
		mTvUserPro = (TextView) findViewById(R.id.tv_user_pro);
		Paint paint = mTvUserPro.getPaint();
		paint.setColor(getResources().getColor(R.color.link_blue));
		paint.setUnderlineText(true);
		mIvRegist = (ImageView) findViewById(R.id.iv_regist);
		mIvRegist.setOnClickListener(this);
		mBtnBoy = (Button) findViewById(R.id.btn_boy);
		mBtnGirl = (Button) findViewById(R.id.btn_girl);
		mBtnBoy.setOnClickListener(this);
		mBtnGirl.setOnClickListener(this);
		mBtnBoy.setSelected(true);
		sex = 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_regist:
			register();
			break;
		case R.id.btn_boy:
			mBtnBoy.setSelected(true);
			mBtnGirl.setSelected(false);
			sex = 0;
			break;
		case R.id.btn_girl:
			mBtnBoy.setSelected(false);
			mBtnGirl.setSelected(true);
			sex = 1;
			break;

		}
	}

	public void register() {
		final String email = mEtEmail.getText().toString().trim();
		final String password2 = mEtPwd2.getText().toString().trim();
		String password1 = mEtPwd1.getText().toString().trim();
		String nickname = mEtName.getText().toString().trim();
		String ageStr = mEtAge.getText().toString().trim();
		int age = 0;
		if (email == null || "".equals(email)) {
			Toast.makeText(RegistActivity.this,
					getResources().getString(R.string.toast_regist_inputemail),
					Toast.LENGTH_SHORT).show();
		} else if (password1 == null || "".equals(password1)) {
			Toast.makeText(RegistActivity.this,
					getResources().getString(R.string.toast_regist_inputpwd),
					Toast.LENGTH_SHORT).show();
		} else if (password2 == null || "".equals(password2)) {
			Toast.makeText(
					RegistActivity.this,
					getResources().getString(
							R.string.toast_regist_inputpwdagain),
					Toast.LENGTH_SHORT).show();
		} else if (!password2.equals(password1)) {
			Toast.makeText(RegistActivity.this,
					getResources().getString(R.string.toast_regist_wrongpwd),
					Toast.LENGTH_SHORT).show();

		} else {
			if (nickname == null || "".equals(nickname)) {
				nickname = "";
			}
			if (ageStr != null && !"".equals(ageStr)) {
				try {
					age = Integer.parseInt(ageStr);
				} catch (NumberFormatException e) {
					Toast.makeText(
							RegistActivity.this,
							getResources().getString(
									R.string.toast_regist_wrongage),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			Account account = new Account();
			account.setEmail(email);
			account.setPassword(password2);
			account.setName(nickname);
			account.setAge(age);
			account.setSex(sex);
			accountImp.register(account);
		}
	}

	public void onEventMainThread(EventAccount eventAccount) {
		int result = eventAccount.getResult();
		String msg = eventAccount.getMsg();
		switch (result) {
		case CommConst.MESSAGE_LOGIN_SUCCESS:// 注册成功,登陆成功激活界面
			Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
			Intent exitIntent = new Intent(RegistActivity.this,
					UserCenterActivity.class);
			startActivity(exitIntent);
			RegistActivity.this.finish();
			break;

		case CommConst.MESSAGE_WHAT_FAULT:// 注册失败
			Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
