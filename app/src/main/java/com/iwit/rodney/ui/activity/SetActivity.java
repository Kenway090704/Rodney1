package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;

import de.greenrobot.event.EventBus;

public class SetActivity extends Activity implements OnClickListener {
	private ImageView mIvBack, mIvRow, mIvUpdateRow, mIvSuggestRow,
			mIvBgSwitch,mIvBlueSwitch;
	private LinearLayout mLineExit, mLineAboutTxt, mLineAbout, mLineUpdate,
			mLineUpdateTxt, mLineSuggest, mLineSuggestInput, mLineIntrodece;
	private IAccountImp accountImp;
	private boolean isAboutShow, isUpdateShow, isSuggestShow = false;
	private EditText mEtSuggest;
	private Button mBtnSent;
	private TextView mTcDesc;
	boolean isOpen = true;
	boolean isBlueOpen = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		EventBus.getDefault().register(this);
		accountImp = new IAccountImp();
		initView();
		initSwitch();
		initSwitchView();
	}

	public void initView() {
		mTcDesc = (TextView) findViewById(R.id.desc);
		mTcDesc.setMovementMethod(LinkMovementMethod.getInstance());
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mLineExit = (LinearLayout) findViewById(R.id.ll_exit);
		mLineExit.setOnClickListener(this);
		mLineAboutTxt = (LinearLayout) findViewById(R.id.ll_about_txt);
		mLineUpdateTxt = (LinearLayout) findViewById(R.id.ll_update_txt);
		mLineUpdate = (LinearLayout) findViewById(R.id.ll_update);
		mLineUpdate.setOnClickListener(this);
		mLineAbout = (LinearLayout) findViewById(R.id.ll_about);
		mLineAbout.setOnClickListener(this);
		mIvRow = (ImageView) findViewById(R.id.iv_row);
		mIvUpdateRow = (ImageView) findViewById(R.id.iv_updaterow);
		mIvSuggestRow = (ImageView) findViewById(R.id.iv_suggestrow);
		mLineSuggest = (LinearLayout) findViewById(R.id.ll_suggest);
		mLineSuggest.setOnClickListener(this);
		mLineSuggestInput = (LinearLayout) findViewById(R.id.ll_suggest_input);
		mEtSuggest = (EditText) findViewById(R.id.et_suggest);
		mBtnSent = (Button) findViewById(R.id.btn_sent);
		
		mLineIntrodece = (LinearLayout) findViewById(R.id.ll_introduce);
		mLineIntrodece.setOnClickListener(this);
		mBtnSent.setOnClickListener(this);

		mIvBgSwitch = (ImageView) findViewById(R.id.iv_bg_switch);
		mIvBgSwitch.setOnClickListener(this);
		mIvBlueSwitch = (ImageView) findViewById(R.id.iv_blue_switch);
		mIvBlueSwitch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.ll_exit:// 退出
			exit();
			break;
		case R.id.ll_about:// 关于
			if (isAboutShow) {
				isAboutShow = false;
				mLineAboutTxt.setVisibility(View.GONE);
				mIvRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_row));
			} else {
				isAboutShow = true;
				mLineAboutTxt.setVisibility(View.VISIBLE);
				mIvRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_selectrow));
			}
			break;
		case R.id.ll_update:
			if (isUpdateShow) {
				isUpdateShow = false;
				mLineUpdateTxt.setVisibility(View.GONE);
				mIvUpdateRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_row));
			} else {
				isUpdateShow = true;
				mLineUpdateTxt.setVisibility(View.VISIBLE);
				mIvUpdateRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_selectrow));
			}
			break;
		case R.id.ll_suggest:
			if (isSuggestShow) {
				isSuggestShow = false;
				mLineSuggestInput.setVisibility(View.GONE);
				mIvSuggestRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_row));
			} else {
				isSuggestShow = true;
				mLineSuggestInput.setVisibility(View.VISIBLE);
				mIvSuggestRow.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_common_selectrow));
			}
			break;
		case R.id.btn_sent:// 提交建议
			String suggest = mEtSuggest.getText().toString().trim();
			accountImp.sendSuggest(suggest);
			break;
		case R.id.iv_bg_switch:
			if(isOpen){
				isOpen = false;
			}else{
				isOpen = true;
			}
			App.getSp().putKv("bg_switch", isOpen);
			initSwitchView();
			break;
		case R.id.iv_blue_switch:
			if(isBlueOpen){
				isBlueOpen = false;
			}else{
				isBlueOpen = true;
			}
			App.getSp().putKv("blue_ararm_switch", isBlueOpen);
			initSwitchView();
			break;
		case R.id.ll_introduce:
			Intent i = new Intent(this, IntroduceActivity.class);
			startActivity(i);
			break;
		}

	}
	private void initSwitch(){
		Object obj = App.getSp().get("bg_switch");
		if(obj == null){
			isOpen = true;
		}else{
			isOpen = Boolean.valueOf(String.valueOf(obj));
		}
		
		Object blue = App.getSp().get("blue_ararm_switch");
		if(blue == null){
			isBlueOpen = true;
		}else{
			isBlueOpen = Boolean.valueOf(String.valueOf(blue));
		}
	}
	private void initSwitchView(){
		if(isOpen){
			mIvBgSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_open));
		}else{
			mIvBgSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_close));
		}
		
		if(isBlueOpen){
			mIvBlueSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_open));
		}else{
			mIvBlueSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_close));
		}
	}
	private void exit() {// 删除当前账户，改变数据库。
		Account account = accountImp.getCurrentAccount();

		if (account != null) {

			account.setIscurrent(0);
			accountImp.saveOrUpdateAccount(account);

			App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_UID);
			App.getSp().remove(CommConst.SP_KEY_CURRENT_RESTYPES);
			App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_EMAIL);
			App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_PassWord);
			App.getSp().remove(CommConst.SP_REGION_ISTABLETACTIVED);

			Toast.makeText(SetActivity.this,
					getResources().getString(R.string.toast_exit_success),
					Toast.LENGTH_SHORT).show();
			Intent exitIntent = new Intent(SetActivity.this,
					UserCenterActivity.class);
			startActivity(exitIntent);

		}
	}

	public void onEventMainThread(EventAccount eventAccount) {
		if (eventAccount.getResult() == 1) {
			mEtSuggest.setText("");
		}
		Toast.makeText(this, eventAccount.getMsg(), Toast.LENGTH_LONG).show();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
