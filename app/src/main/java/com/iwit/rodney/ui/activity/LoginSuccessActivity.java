package com.iwit.rodney.ui.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.imp.IAccountImp;
import com.iwit.rodney.ui.view.CircleImageView;

import de.greenrobot.event.EventBus;

public class LoginSuccessActivity extends Activity implements OnClickListener {
	private TextView mTvName, mTvEmail;
	private IAccountImp accountImp;
	private LinearLayout mLlExit, mLlActive;
	private Account account;
	private ImageView mIvBack;
	private CircleImageView mIvImg;
	private LinearLayout mLlName, mLlEmail, mLlPic;
	private final int REQUESTNAME = 0X123;
	private final int REQUESTEMAIL = 0X124;
	private final int REQUESTPIC = 0X125;
	private String imgPath;
	private ImageView mIvIsActived;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_account);
		accountImp = new IAccountImp(this);
		EventBus.getDefault().register(this);
		init();
	}

	private void init() {
		mTvName = (TextView) findViewById(R.id.tv_name);
		account = accountImp.getCurrentAccount();
		mTvName.setText(account.getName());
		mTvEmail = (TextView) findViewById(R.id.tv_email);
		mTvEmail.setText(account.getEmail());
		mLlExit = (LinearLayout) findViewById(R.id.ll_exit);
		mLlExit.setOnClickListener(this);
		mLlActive = (LinearLayout) findViewById(R.id.ll_active);
		mLlActive.setOnClickListener(this);
		mLlName = (LinearLayout) findViewById(R.id.ll_account_name);
		mLlName.setOnClickListener(this);
		mLlEmail = (LinearLayout) findViewById(R.id.ll_account_email);
		mLlPic = (LinearLayout) findViewById(R.id.ll_account_pic);
		mLlPic.setOnClickListener(this);
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(this);
		mIvImg = (CircleImageView) findViewById(R.id.iv_img);
		mIvIsActived = (ImageView) findViewById(R.id.iv_isActive);
		String restypes = accountImp.getCurrentAccountRestypes();
		if (restypes.equals(CommConst.RESTYPE_FREE)) {
			mIvIsActived.setImageResource(R.drawable.btn_account_unactived);
		} else {
			mIvIsActived.setImageResource(R.drawable.btn_account_actived);
		}
		imgPath = Environment.getExternalStorageDirectory()
				+ CommConst.ROOT_USER_IMG_PATH + "/"
				+ accountImp.getCurrentUid() + ".jpg";
		File file = new File(imgPath);
		if (file.exists()) {
			mIvImg.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_exit:
			if (account != null) {
				account.setIscurrent(0);
				accountImp.saveOrUpdateAccount(account);
				App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_UID);
				App.getSp().remove(CommConst.SP_KEY_CURRENT_RESTYPES);
				App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_EMAIL);
				App.getSp().remove(CommConst.SP_KEY_CURRENT_USER_PassWord);
				App.getSp().remove(CommConst.SP_REGION_ISTABLETACTIVED);
				Intent exitIntent = new Intent(LoginSuccessActivity.this,
						UserCenterActivity.class);
				startActivity(exitIntent);

			}
			break;
		case R.id.ll_active:// 激活
			accountImp.active(account);

			break;
		case R.id.ll_account_name:
			Intent nameIntent = new Intent(LoginSuccessActivity.this,
					SetNewInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("changeitem", "name");
			nameIntent.putExtras(bundle);
			startActivityForResult(nameIntent, REQUESTNAME);
			break;
		case R.id.ll_account_email:
			Intent emailIntent = new Intent(LoginSuccessActivity.this,
					SetNewInfoActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putString("changeitem", "email");
			emailIntent.putExtras(bundle2);
			startActivityForResult(emailIntent, REQUESTEMAIL);
			break;

		case R.id.ll_account_pic:
			Intent ImgIntent;
			if(StringUtils.isEmpty(Build.MODEL) || Build.MODEL.startsWith("Coolpad")){
				ImgIntent = new Intent(LoginSuccessActivity.this,
						 SetImgDialog.class);
			}else{
				ImgIntent = new Intent(LoginSuccessActivity.this,
								TakeImgDialog.class);
			}
			startActivityForResult(ImgIntent, REQUESTPIC);
			break;

		case R.id.iv_back:
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		account = accountImp.getCurrentAccount();
		if (requestCode == REQUESTNAME && resultCode == Activity.RESULT_OK) {// 修改了名字
			mTvName.setText(account.getName());
		} else if (requestCode == REQUESTEMAIL
				&& resultCode == Activity.RESULT_OK) {// 修改了邮箱
			mTvEmail.setText(account.getEmail());
		} else if (requestCode == REQUESTPIC
				&& resultCode == Activity.RESULT_OK) {// 修改了照片
			mIvImg.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		}
	}

	public void onEventMainThread(EventAccount eventAccount) {
		int result = eventAccount.getResult();
		String msg = eventAccount.getMsg();
		if (result == CommConst.MESSAGE_ACTIVATE_SUCCESS) {
			mIvIsActived.setImageResource(R.drawable.btn_account_actived);
		} else {
			Toast.makeText(LoginSuccessActivity.this, msg, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
