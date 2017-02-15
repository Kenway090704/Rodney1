package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;

import de.greenrobot.event.EventBus;

public class ActiveActivity extends Activity implements OnClickListener {
	private EditText mEtLeft, mEtRight;
	private Button mBtnActive, mBtnCancel;
	private IAccountImp accountImp;

	private EditChangedListener editChangedListener;

	private void init() {
		mEtLeft = (EditText) findViewById(R.id.et_left);
		mEtRight = (EditText) findViewById(R.id.et_right);
		editChangedListener = new EditChangedListener();
		mEtLeft.addTextChangedListener(editChangedListener);
		mEtRight.addTextChangedListener(editChangedListener);
		mEtLeft.requestFocus();
		mBtnActive = (Button) findViewById(R.id.bn_start_activate);
		mBtnCancel = (Button) findViewById(R.id.bn_finish);
		mBtnActive.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}

	private String getCode() {
		String left = mEtLeft.getText().toString().trim();
		String right = mEtRight.getText().toString().trim();
		return left + right;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_account_activate);
		accountImp = new IAccountImp(this);
		EventBus.getDefault().register(this);
		init();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bn_start_activate:
			String Code = getCode();
			accountImp.accountActivate(Code);
			break;
		case R.id.bn_finish:
			finish();
			break;
		default:
			break;
		}
	}

	public void onEventMainThread(EventAccount eventAccount) {
		String msg = eventAccount.getMsg();
		Toast.makeText(ActiveActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private class EditChangedListener implements TextWatcher {
		private CharSequence temp;// 监听前的文本
		private final int charMaxNum = 4;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			if (count > charMaxNum && mEtRight.isFocusable()) {
				Toast.makeText(ActiveActivity.this,
						getString(R.string.word_size_over), Toast.LENGTH_SHORT)
						.show();

			} else {

			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (temp.length() == charMaxNum && mEtLeft.isFocusable()) {
				mEtRight.requestFocus();
			}
		}
	};

}
