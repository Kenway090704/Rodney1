package com.iwit.rodney.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.imp.IAccountImp;

public class SetImgDialog extends Activity implements OnClickListener {
	private TextView mTvLocalPic, mTvTakePic;
	private Intent intent;
	private IAccountImp accountImp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_account_pic);
		setFinishOnTouchOutside(true);
		intent = getIntent();
		accountImp = new IAccountImp();
		init();
	}

	private void init() {
		mTvLocalPic = (TextView) findViewById(R.id.tv_localpic);
		mTvTakePic = (TextView) findViewById(R.id.tv_takepic);
		mTvLocalPic.setOnClickListener(this);
		mTvTakePic.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_localpic:// 本地相册
			Intent iPhoto = new Intent(Intent.ACTION_PICK, null);
			iPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(iPhoto, 1);
			break;

		case R.id.tv_takepic:// 拍照
			Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(iCamera, 2);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (data != null && data.getData() != null) {
				startPhotoZoom(data.getData());
			}
			break;
		case 2:// 拍照
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				return;
			}
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
					savePicture(bitmap);
					// 保存图片到相机指定位置
					File temp = new File(
							Environment.getExternalStorageDirectory()
									+ CommConst.ROOT_USER_IMG_PATH + "/"
									+ accountImp.getCurrentUid() + ".jpg");
					startPhotoZoom(Uri.fromFile(temp));
				}
			}
			break;
		case 3:// 取得裁剪后的图片
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			// 将裁减的bitmap保存到指定的目录
			savePicture(photo);

		}
	}

	private void savePicture(Bitmap bitmap) {
		FileOutputStream b = null;
		String fileName = Environment.getExternalStorageDirectory()
				+ CommConst.ROOT_USER_IMG_PATH + "/"
				+ accountImp.getCurrentUid() + ".jpg";
		File file = new File(Environment.getExternalStorageDirectory()
				+ CommConst.ROOT_USER_IMG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 删除上一个 保存下一个
		try {
			b = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			setResult(Activity.RESULT_OK);
			finish();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
