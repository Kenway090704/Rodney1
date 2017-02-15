package com.iwit.rodney.ui.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.imp.IAccountImp;

public class TakeImgDialog extends Activity implements OnClickListener{
	private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private final static int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 300;
	private static Uri fileUri;
	private static boolean isGalleryUri = true;
	private static Uri fileDesUri;
	
	
	private TextView mTvLocalPic, mTvTakePic;
	private Intent intent;
	private IAccountImp accountImp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_account_pic);
		setFinishOnTouchOutside(true);
		intent = getIntent();
		accountImp = new IAccountImp();
		checkImageFile(this);
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
		switch (v.getId()) {
		case R.id.tv_localpic:// 本地相册
			Intent intent = null;
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			if (intent.resolveActivity(getPackageManager()) != null) {
				isGalleryUri = true;
				startActivityForResult(intent,
						GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);

			} else {
				Toast.makeText(this, "no app for gallery",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.tv_takepic:// 拍照
			Intent intentCamera = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			if (intentCamera.resolveActivity(getPackageManager()) != null) {
				isGalleryUri = false;
				startActivityForResult(intentCamera,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			} else {
				Toast.makeText(this, "no app for camera",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	
	public void processOnActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) { // ���շ���
			if (resultCode == Activity.RESULT_OK) {
				startPhotoZoom(context, fileUri); // ȥ�ü�
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "camera fail", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) { // ͼ�ⷵ��
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				startPhotoZoom(context, uri); // ȥ�ü�
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "gallery fail", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) { 
			if (resultCode == Activity.RESULT_OK) {
				Uri temp = null; 
				if (isGalleryUri) {
					temp = fileUri;
				} else {
					if (Build.VERSION.SDK_INT <= 18) {
						temp = fileUri;
					} else {
						temp = fileDesUri;
					}
				}
				getBitmapFromUri(context, temp);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "crop fail", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private  void startPhotoZoom(Context context, Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false);
		Uri temp = null; 
		if (isGalleryUri) {
			temp = fileUri;
		} else {
			if (Build.VERSION.SDK_INT <= 18) {
				temp = fileUri;
			} else {
				temp = fileDesUri;
			}
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, temp);
		intent.putExtra("return-data", false);
		startActivityForResult(intent,CROP_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private Bitmap getBitmapFromUri(Context context, Uri uri) {
		ParcelFileDescriptor parcelFileDescriptor;
		Bitmap image = null;
		try {
			parcelFileDescriptor = context.getContentResolver()
					.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor
					.getFileDescriptor();
			image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			savePicture(image);
			parcelFileDescriptor.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
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
			bitmap.isRecycled();
			bitmap =null;
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
	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			return false;
		}
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				return false;
			}
		}
		try {
			if (file.createNewFile()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		processOnActivityResult(this, requestCode, resultCode, data);
	}

	private static void checkImageFile(Context context) {
		File file1 = new File(Environment.getExternalStorageDirectory()
				+ "/rondey/userImg/");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		String path = Environment.getExternalStorageDirectory()
				+ "/rondey/userImg/temp.jpg";
		String path1 = Environment.getExternalStorageDirectory()
				+ "/rondey/userImg/des_temp.jpg";

		createFile(path);
		createFile(path1);
		if (new File(path).exists() && new File(path1).exists()) {
			fileUri = Uri.fromFile(new File(path));
			fileDesUri = Uri.fromFile(new File(path1));
		} else {
			Toast.makeText(context, "create file fail", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
