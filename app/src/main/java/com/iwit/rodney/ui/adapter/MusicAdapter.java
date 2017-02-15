package com.iwit.rodney.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.bussess.MusicDas;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.FileUtils;
import com.iwit.rodney.comm.tools.NetWorkTools;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.downloader.downloadmgr.DownloadInfoBean;
import com.iwit.rodney.downloader.downloadmgr.IwitDownloadManager;
import com.iwit.rodney.downloader.helperclass.MyContentObserver;
import com.iwit.rodney.downloader.helperclass.OnContentListener;
import com.iwit.rodney.downloader.provider.Downloads;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.interfaces.IManager;

/**
 * 
 * @author qh
 * @desc download lrc and mp3
 * @desc 可以先下载lrc 然后下载MP3
 * @desc 如果没有lrc 就直接下载MP3
 * @date 2016-03-21
 */
public class MusicAdapter extends BaseAdapter {
	private Context ctx;
	private List<Music> musicList;
	private MyContentObserver mMyContentObserver;
	private IwitDownloadManager iwitDownloadManager;
	private final static String TAG = "Download";

	/**
	 * @param ctx
	 * @param musicList
	 * @param type
	 *            true 儿歌 false 故事
	 */
	public MusicAdapter(Context ctx, List<Music> musicList) {
		this.ctx = ctx;
		this.musicList = musicList;
		iwitDownloadManager = IwitDownloadManager.getInstance(ctx.getApplicationContext());
		mMyContentObserver = new MyContentObserver(ctx, iwitDownloadManager,
				mOnContentListener);
		rigisterContentObserver();
		initFirstDataToBean();
	}

	@Override
	public int getCount() {
		return musicList == null ? 0 : musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.item_music, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Music music = musicList.get(position);
		final String requestUrl = CommConst.SIT_ROOT_FILE_URL + music.getCoverpic();
		holder.mIvIcon.setImageBitmap(BitmapFactory.decodeResource(
				ctx.getResources(), R.drawable.ic_common_music_icon_default));
		if(!StringUtils.isEmpty(music.getCoverpic())){
//			WebRequest.loadImage(ctx, requestUrl, holder.mIvIcon);
		}
		holder.mTvName.setText(music.getMname());
		holder.mTvFavNum.setText("" + music.getFavourites());
		holder.mTvFavNum.setVisibility(View.GONE);
		if (music.getSort() != null && music.getSort() == 1) {
			holder.mIvBelong.setImageBitmap(BitmapFactory.decodeResource(
					ctx.getResources(), R.drawable.ic_music_type_logo));
		} else { 
			holder.mIvBelong.setImageBitmap(BitmapFactory.decodeResource(
					ctx.getResources(), R.drawable.ic_music_type_logo1));
		}
		if (music.getIschoose() == 0) {
			holder.mIvPannel.setBackgroundDrawable(ctx.getResources()
					.getDrawable(R.drawable.ic_music_item_white));
		} else {
			holder.mIvPannel.setBackgroundDrawable(ctx.getResources()
					.getDrawable(R.drawable.ic_music_item_blue));
		}
		if (music.getF() == null || music.getF() == 0) {
			holder.mIvFavorite.setImageBitmap(BitmapFactory.decodeResource(
					ctx.getResources(), R.drawable.ic_music_item_fav_none));
		} else {
			holder.mIvFavorite.setImageBitmap(BitmapFactory.decodeResource(
					ctx.getResources(), R.drawable.ic_music_item_fav));
		}
		holder.mIvFavorite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(!StringUtils.isEmpty(music.getCoverpic())){
					WebRequest.loadImage(ctx, requestUrl, holder.mIvIcon);
				}*/
				//更新数据库
				Account account = IManager.getIAccount().getCurrentAccount();
				if(account == null){
					Toast.makeText(ctx, "请先登录账户！", 1).show();
					return;
				}
				if(music.getF() == null || music.getF() == 0){
					music.setF(1);
				}else{
					music.setF(0);
				}
				IManager.getIMusic().updateMusic(music);
				IManager.getIMusic().updateCollection(music);
				notifyDataSetChanged();
			}
		});
		holder.mIvDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				musicDownload(music);
			}
		});

		if (music.getDownloaded() == 1) {
			holder.mIvDownload.setVisibility(View.GONE);
			holder.mPbDownload.setVisibility(View.VISIBLE);
		} else {
			holder.mIvDownload.setVisibility(View.VISIBLE);
			holder.mPbDownload.setVisibility(View.GONE);
		}
		if (music.getStatus() == CommConst.STATUS_READY) {
			holder.mIvDownload.setVisibility(View.GONE);
			holder.mPbDownload.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView mIvIcon, mIvBelong, mIvFavorite, mIvDownload, mIvPannel;
		TextView mTvName, mTvFavNum;
		ProgressBar mPbDownload;

		ViewHolder(View view) {
			mIvIcon = (ImageView) view.findViewById(R.id.iv_music_icon);
			mIvBelong = (ImageView) view.findViewById(R.id.iv_belong);
			mIvFavorite = (ImageView) view.findViewById(R.id.iv_favorite);
			mIvDownload = (ImageView) view.findViewById(R.id.iv_download);
			mIvPannel = (ImageView) view.findViewById(R.id.iv_music_pannel);
			mTvName = (TextView) view.findViewById(R.id.tv_music_name);
			mTvFavNum = (TextView) view.findViewById(R.id.tv_favorite_num);
			mPbDownload = (ProgressBar) view.findViewById(R.id.pb_download);
		}
	}

	private void musicDownload(Music music) {
		if(NetWorkTools.getAPNType(ctx) == -1){
			Toast.makeText(ctx, "没有连接网络.", 1).show();
			return;
		}
		String mp3Name = music.getMfname();
		String lrcName = music.getMname() + ".lrc";
		String url_mp3 = CommConst.SIT_ROOT_FILE_URL + music.getMfname();
		String url_file = CommConst.SIT_ROOT_FILE_URL + music.getLyric();
		if (mp3Name == null) {
			Toast.makeText(ctx, "没有MP3资源可提供下载", 1).show();
		 	return;
		}
		if (music.getMid().startsWith("s1")) {
			iwitDownloadManager.startDownload(url_mp3, mp3Name,
					CommConst.MUSIC_FILE_PATH);
			iwitDownloadManager.startDownload(url_file, lrcName,
					CommConst.MUSIC_LRC_PATH);
		} else {
			url_mp3 = music.getMfname();
			mp3Name = FileUtils.getFilename(url_mp3);
			iwitDownloadManager.startDownload(url_mp3, mp3Name,
					CommConst.MUSIC_FILE_PATH);
		}
		music.setDownloaded(1);
	}

	/**
	 * 观察者
	 */
	private OnContentListener mOnContentListener = new OnContentListener() {

		@Override
		public void onChange(List<DownloadInfoBean> listDownloadInfoBean) {
			if (listDownloadInfoBean == null) {
				Log.v(TAG, "no download");
			} else {
				Log.v(TAG, "download:" + listDownloadInfoBean.size());
			}
			scanDownloadInfoBean(listDownloadInfoBean, false);
			notifyDataSetChanged();
		}
	};

	/**
	 * 数据变化
	 */
	public void initFirstDataToBean() {
		if (StringUtils.isListEmpty(musicList)) {
			return;
		}
		for(Music music : musicList){
			music.setDownloaded(0);
			music.setStatus(CommConst.STATUS_NO_DOWNLOAD);
		}
		List<DownloadInfoBean> listDownloadInfoBean = iwitDownloadManager
				.queryDownloadInfoByPackageName(CommConst.PACKAGE_NAME);
		scanDownloadInfoBean(listDownloadInfoBean, true);
		notifyDataSetChanged();
	}

	/**
	 * @param listDownloadInfoBean
	 */
	private void scanDownloadInfoBean(
			List<DownloadInfoBean> listDownloadInfoBean, boolean isFirst) {
		String url;
		int status;
		long total_size;
		long current_size;
		int progress;
		long downloadId;
		for (DownloadInfoBean downloadbean : listDownloadInfoBean) {
			url = downloadbean.getDownload_uri();
			status = downloadbean.getDownload_status();
			total_size = downloadbean.getDownload_total_bytes();
			current_size = downloadbean.getDownload_current_bytes();
			progress = StringUtils.getProgressValue(total_size, current_size);
			downloadId = downloadbean.getDownload_id();
			if (StringUtils.isListEmpty(musicList)) {
				return;
			}
			for (Music music : musicList) {
				if (music.getMfname() == null) {
					continue;
				}
				String music_mfname;
				if (music.getMid().startsWith("s1")) {
					music_mfname = CommConst.SIT_ROOT_FILE_URL
							+ music.getMfname();
				} else {
					music_mfname = music.getMfname();
				}

				if (url.equals(music_mfname)) {
					music.setDownloaded(1);
					music.setStatus(status);
					music.setProgress(progress);
					music.setDownloadId(downloadId);
					if (status == CommConst.STATUS_READY) {
						music.setDownloaded(4);
						MusicDas.UpdateDownloadMusic(music);
					}
				}
			}
		}
	}

	// 监听
	public void rigisterContentObserver() {
		if (mMyContentObserver == null) {
			return;
		}
		App.ctx.getContentResolver().registerContentObserver(
				Downloads.Impl.CONTENT_URI, true, mMyContentObserver);
	}

	// 取消监听
	public void unrigisterContentObserver() {
		if (mMyContentObserver == null) {
			return;
		}
		App.ctx.getContentResolver().unregisterContentObserver(
				mMyContentObserver);
	}
}
