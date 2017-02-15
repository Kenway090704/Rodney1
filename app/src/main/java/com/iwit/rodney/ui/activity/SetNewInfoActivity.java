package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.imp.IAccountImp;

public class SetNewInfoActivity extends Activity implements OnClickListener {
	private ImageView mIvBack, mIvConfirm, mIvTitle;
	private EditText mEtNewName, mEtNewEmail;
	private LinearLayout mLlName, mLlEmail;
	private Intent intent;
	private String change;
	private IAccountImp accountImp;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeinfo);
		accountImp = new IAccountImp(this);
		account = accountImp.getCurrentAccount();
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		change = bundle.getString("changeitem");
		initView();
		if (change.equals("name")) {
			mLlName.setVisibility(View.VISIBLE);
			mLlEmail.setVisibility(View.GONE);
			mIvTitle.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_account_nametxt));
		} else {
			mLlName.setVisibility(View.GONE);
			mLlEmail.setVisibility(View.VISIBLE);
			mIvTitle.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_account_emailtxt));
		}

	}

	public void initView() {
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mIvConfirm = (ImageView) findViewById(R.id.iv_confirm);
		mIvConfirm.setOnClickListener(this);
		mEtNewName = (EditText) findViewById(R.id.et_new_name);
		mEtNewName.getPaint().setFakeBoldText(true);
		mEtNewEmail = (EditText) findViewById(R.id.et_new_email);
		mEtNewEmail.getPaint().setFakeBoldText(true);
		mLlName = (LinearLayout) findViewById(R.id.ll_account_name);
		mLlEmail = (LinearLayout) findViewById(R.id.ll_account_email);
		mIvTitle = (ImageView) findViewById(R.id.iv_title);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_confirm:// 暂时只改数据库中的信息
			if (change.equals("name")) {// 修改名字
				String name = mEtNewName.getText().toString().trim();
				account.setName(name);
				accountImp.saveOrUpdateAccount(account);
				SetNewInfoActivity.this.setResult(Activity.RESULT_OK, intent);
				SetNewInfoActivity.this.finish();
			} else {// 修改邮箱
				String email = mEtNewEmail.getText().toString().trim();
				if (StringUtils.isEmail(email)) {
					account.setEmail(email);
					accountImp.saveOrUpdateAccount(account);
					SetNewInfoActivity.this.setResult(Activity.RESULT_OK,
							intent);
					SetNewInfoActivity.this.finish();
				} else {
					Toast.makeText(
							SetNewInfoActivity.this,
							getResources().getString(
									R.string.toast_account_correctemail),
							Toast.LENGTH_LONG).show();

				}
			}

			break;

		}

	}
}
