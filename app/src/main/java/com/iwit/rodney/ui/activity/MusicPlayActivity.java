package com.iwit.rodney.ui.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.OnCallBack;
import com.iwit.rodney.service.IMusicManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.service.MusicManagerService;
import com.iwit.rodney.service.OnMusicStateChange;
import com.iwit.rodney.ui.view.IwitMusicLrcView;

public class MusicPlayActivity extends Activity implements OnMusicStateChange,
        OnClickListener {
    private String getName() {
        return this.getClass().getSimpleName();
    }

    private IMusicManager mIMusicManager;
    private MusicManagerService msgService;
    private IwitDownloadManager iwitDownloadManager;
    private MyContentObserver mMyContentObserver;
    private static final String TAG = "MusicPlayActivity";
    private boolean state;// 状态
    private String oldUrl = "";// 防止图片相同的时候更新
    private String url_path;
    private Music curMusic = null;
    private long totalTime;
    private boolean favoriteState = false;
    private int playMode;
    private TextView mTvMusicName;
    private TextView mTvMusicCurrentTime;
    private TextView mTvMusicTotalTime;

    private ImageView mIvMusicShow;
    private ImageView mIvMusicDownload;
    private ProgressBar mPbDownload;
    private RelativeLayout mRlDownload;
    private ImageView mIvMusicFavorite;
    private ImageView mIvMusicModel;
    private ImageView mIvMusicPlay;
    private IwitMusicLrcView mIlLMusicLrc;
    private ImageView mIVMusicMask;
    private SeekBar mSeekbar;
    private int[] mLrcSongPic = new int[]{R.drawable.ic_music_lrc_song_1,
            R.drawable.ic_music_lrc_song_2, R.drawable.ic_music_lrc_song_3,
            R.drawable.ic_music_lrc_song_4};
    private int[] mLrcStoryPic = new int[]{R.drawable.ic_music_lrc_story_1,
            R.drawable.ic_music_lrc_story_2, R.drawable.ic_music_lrc_story_3,
            R.drawable.ic_music_lrc_story_4};
    //就是这一部分将设备与手机通过蓝牙连接
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "service is disconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "service is connected");
            msgService = ((MusicManagerService.MsgBinder) service).getService();
            mIMusicManager = IMusicManager.Stub.asInterface(msgService.mBinder);
            initServiceData();
        }
    };
    private Handler handlerUI = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    if (curMusic != null) {
                        Log.v("player", "" + "tempMusic.getDownloaded():"
                                + curMusic.getDownloaded());
                        if (curMusic.getDownloaded() == 0) {
                            mIvMusicDownload.setVisibility(View.VISIBLE);
                            mPbDownload.setVisibility(View.GONE);
                            mRlDownload.setVisibility(View.VISIBLE);
                        } else if (curMusic.getDownloaded() == 1) {
                            mIvMusicDownload.setVisibility(View.GONE);
                            mPbDownload.setVisibility(View.VISIBLE);
                            mRlDownload.setVisibility(View.VISIBLE);
                        } else {
                            mIvMusicDownload.setVisibility(View.GONE);
                            mPbDownload.setVisibility(View.GONE);
                            mRlDownload.setVisibility(View.GONE);
                        }
                        // 当为录音的时候
                        if (curMusic.getIsrecord() != null
                                && curMusic.getIsrecord() == 1) {
                            mIvMusicDownload.setVisibility(View.GONE);
                            mPbDownload.setVisibility(View.GONE);
                            mRlDownload.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        }

    };

    private void bindService() {
        Intent intent = new Intent(this, MusicManagerService.class);
        boolean flag = getApplicationContext().bindService(intent, conn,
                Context.BIND_AUTO_CREATE);
        Log.v(TAG, "" + flag);
    }

    public void initServiceData() {
        Log.v(TAG, "msgService::" + msgService);
        if (msgService != null) {
            msgService.setmOnMusicStateChange(this, getName());
        }
    }

    private RelativeLayout rlRecordContainer;
    private TextView tvRecordTitle, tvRecordContent;
    private LinearLayout mLiFacorite;

    private void initView() {
        mTvMusicName = (TextView) findViewById(R.id.tv_music_title);
        mIvMusicShow = (ImageView) findViewById(R.id.iv_music_ic_show);
        mIvMusicDownload = (ImageView) findViewById(R.id.iv_music_download);
        mPbDownload = (ProgressBar) findViewById(R.id.pb_download);
        mRlDownload = (RelativeLayout) findViewById(R.id.li_download);
        mLiFacorite = (LinearLayout) findViewById(R.id.li_fav);
        mIvMusicFavorite = (ImageView) findViewById(R.id.iv_music_fav);
        mIvMusicModel = (ImageView) findViewById(R.id.iv_music_model);
        mIvMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
        mTvMusicTotalTime = (TextView) findViewById(R.id.tv_total_time);
        mTvMusicCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        mSeekbar = (SeekBar) findViewById(R.id.seekbar);
        mIlLMusicLrc = (IwitMusicLrcView) findViewById(R.id.il_music_lrc);
        mIVMusicMask = (ImageView) findViewById(R.id.iv_music_mask);

        tvRecordTitle = (TextView) findViewById(R.id.title);
        tvRecordContent = (TextView) findViewById(R.id.context);
        rlRecordContainer = (RelativeLayout) findViewById(R.id.record_lrc_container);
        rlRecordContainer.setVisibility(View.GONE);

        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (msgService != null) {
                    msgService.seek((int) (seekBar.getProgress() * totalTime / 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        });
        String uid = IManager.getIAccount().getCurrentUid();
        // 如果账户没有登录 不能点赞
        // mIvMusicFavorite.setClickable(false);
        if (!StringUtils.isEmpty(uid)) {
            if (curMusic != null && curMusic.getF() != null) {
                mIvMusicFavorite.setClickable(true);
                if (curMusic.getF() == 1) {
                    favoriteState = true;
                    mIvMusicFavorite.setImageBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_music_item_fav));
                } else {
                    favoriteState = false;
                    mIvMusicFavorite.setImageBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_music_item_fav_none));
                }
            }
        }
    }

    public void onEventMainThread(Event event) {
        switch (event.getType()) {
            case Event.EVENT_UPDATE_FAV:
                Toast.makeText(this, event.getMsg(), 1).show();
                if (event.isResult()) {

                } else {

                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addActivity(this);
        bindService();
        setContentView(R.layout.activity_music_play);
        initView();
        iwitDownloadManager = IwitDownloadManager.getInstance(this
                .getApplicationContext());
        mMyContentObserver = new MyContentObserver(this, iwitDownloadManager,
                mOnContentListener);
        rigisterContentObserver();

        setPlayImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unrigisterContentObserver();
        App.getInstance().removeActivity(this);
    }

    @Override
    public void updateMusic(int position) {

    }

    /**
     * @desc 初始化名称 总时长 歌词 是否下载 点赞 从其他地方计入此界面 退回的全部是 主界面 这样方便更新状态 例如点赞
     */
    private String name = "s1";

    @Override
    public void getMusicInformation(Music music, int playMode) {
        curMusic = music;
        String requestUrl = CommConst.SIT_ROOT_FILE_URL + music.getCoverpic();
        mTvMusicName.setText("" + music.getMname());
        if (name == null || !name.equals(music.getMname())) {
            //下载歌词  之后可
            downloadLrc(music);
            // WebRequest.loadImage(this, requestUrl, mIvMusicShow);
            oldUrl = requestUrl;
            totalTime = msgService.getDuration();
            String totalTime1 = StringUtils.formatTime(totalTime);
            Log.v(TAG, "totalTime:" + totalTime1);
            mTvMusicTotalTime.setText(totalTime1);
            if (curMusic.getIsrecord() == null || curMusic.getIsrecord() != 1) {
                rlRecordContainer.setVisibility(View.GONE);
                initUrl();
                // 是否有歌词 展示歌词
                showLrc(music);
            } else {
                // 如果是歌词
                String fileStr = Environment.getExternalStorageDirectory()
                        .getAbsolutePath()
                        + CommConst.RECORD_FILE_PATH
                        + curMusic.getLyric();
                File file = new File(fileStr);
                if (file.exists()) {
                    String lrc[] = FileUtils.getLrcFromFile(file, "gbk");
                    if (lrc == null || lrc.length != 2) {
                        throw new RuntimeException();
                        // 显示record 隐藏一些按钮
                    }
                    rlRecordContainer.setVisibility(View.VISIBLE);
                    if (lrc[0] != null)
                        tvRecordTitle.setText(lrc[0]);
                    if (lrc[1] != null)
                        tvRecordContent.setText(lrc[1]);
                    mRlDownload.setVisibility(View.GONE);
                    mLiFacorite.setVisibility(View.GONE);
                } else {
                    // 显示record 隐藏一些按钮
                    rlRecordContainer.setVisibility(View.GONE);
                    mRlDownload.setVisibility(View.GONE);
                    mLiFacorite.setVisibility(View.GONE);
                }
            }

            name = music.getMname();
            if (curMusic.getIsrecord() == null || curMusic.getIsrecord() != 1) {
                initUrl();
                // 第一次初始化
                List<DownloadInfoBean> downloadlist = iwitDownloadManager
                        .queryDownloadInfoByPackageNameAndUrl(getPackageName(),
                                url_path);
                scanDownloadInfoBean(downloadlist);
                handlerUI.sendEmptyMessage(1000);
            } else {

            }

            if (curMusic.getF() == null || curMusic.getF() == 0) {
                favoriteState = false;
                mIvMusicFavorite.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.ic_music_item_fav_none));
            } else {
                favoriteState = true;
                mIvMusicFavorite.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.ic_music_item_fav));
            }

        }
        this.playMode = playMode;
        initPlayMode(playMode);

    }

    private void setPlayImage() {
        Music music = MusicListControl.getInstance().getCurMusic();
        if (music == null) {
            return;
        }
        int num = (int) (Math.random() * 4);
        if (music.getSort() != null && music.getSort() == 1) {
            mIvMusicShow
                    .setImageBitmap(BitmapFactory.decodeResource(
                            MusicPlayActivity.this.getResources(),
                            mLrcSongPic[num]));
        } else {
            mIvMusicShow.setImageBitmap(BitmapFactory.decodeResource(
                    MusicPlayActivity.this.getResources(),
                    mLrcStoryPic[num]));
        }
        mTvMusicName.setText("" + music.getMname());
    }

    private void showLrc(Music music) {
        String lrc = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + CommConst.MUSIC_LRC_PATH
                + music.getMname() + ".lrc";
        mIlLMusicLrc.setLrc(lrc, mIVMusicMask, new OnCallBack() {

            @Override
            public void back(long time) {
                if (time > totalTime) {
                    return;
                }
                msgService.seek((int) (time));
            }
        });
    }

    void initPlayMode(int playMode) {
        switch (playMode) {
            case MusicManagerService.ALL:
                mIvMusicModel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ic_music_play_all));
                break;
            case MusicManagerService.SINGLE:
                mIvMusicModel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ic_music_play_single));
                break;
            case MusicManagerService.RANDOM:
                mIvMusicModel.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ic_music_play_random));
                break;
        }
    }

    @Override
    public void updateMusicStatus(long currentTime) {
        String currentStr = StringUtils.formatTime(currentTime);
        mTvMusicCurrentTime.setText(currentStr);
        if (totalTime == 0) {
            return;
        }
        int percent = (int) (currentTime * 100 / totalTime);
        mSeekbar.setProgress(percent);
        mIlLMusicLrc.onProcessChanged(currentTime);
    }

    @Override
    public void getMediaStatePlayer(boolean state, int playMode) {
        this.state = state;
        if (state) {
            mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.ic_music_play_pause));
        } else {
            mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.ic_music_play_play));
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.iv_music_back:
                    finish();
                    break;
                case R.id.iv_music_download:
                    if (curMusic != null) {
                        downloadMusic(curMusic);
                    }
                    break;
                case R.id.iv_music_fav:
                    if (curMusic != null) {
                        // 更新数据库
                        boolean isSuccess = IManager.getIMusic().updateCollection(
                                curMusic);
                        if (!isSuccess) {
                            Toast.makeText(this, "请先登录账户！", 1).show();
                            return;
                        }
                        if (curMusic.getF() == null || curMusic.getF() == 0) {

                            curMusic.setF(1);
                        } else {
                            curMusic.setF(0);
                        }
                        IManager.getIMusic().updateMusic(curMusic);
                        IManager.getIMusic().updateCollection(curMusic);

                        if (curMusic.getF() == null || curMusic.getF() == 0) {
                            mIvMusicFavorite.setImageBitmap(BitmapFactory
                                    .decodeResource(getResources(),
                                            R.drawable.ic_music_item_fav_none));
                        } else {
                            mIvMusicFavorite.setImageBitmap(BitmapFactory
                                    .decodeResource(getResources(),
                                            R.drawable.ic_music_item_fav));
                        }
                    }
                    break;
                case R.id.iv_music_model:
                    if (playMode < 2) {
                        playMode++;
                    } else {
                        playMode = 0;
                    }
                    mIMusicManager.setPlayMode(playMode);
                    initPlayMode(playMode);
                    break;
                case R.id.iv_music_delete:
                    if (curMusic.getIsrecord() != null
                            && curMusic.getIsrecord() == 1) {
                        Record rd = new Record();
                        rd.setName(curMusic.getMname());
                        boolean is = IManager.getIRecord().delete(rd);
                        if (is) {
                            Toast.makeText(this, "删除成功", 1).show();
                        } else {
                            Toast.makeText(this, "删除失败", 1).show();
                        }
                        //MusicListControl.getInstance().removeMusic();
                        mIMusicManager.nextMusic();
                        mIMusicManager.pauseMusic();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (StringUtils.isListEmpty(MusicListControl.getInstance().getMusics())) {
                                    finish();
                                }

                            }
                        }, 1000);

                    }
                    if (curMusic != null && curMusic.getDownloaded() == 4) {
                        Toast.makeText(this, "删除成功", 1).show();
                        MusicDas.upDateMusicState(curMusic, 0);// 更新数据库数据
                        // 删除下载数据库
                        String url_mp3;
                        if (curMusic.getMid().startsWith("s1")) {
                            url_mp3 = CommConst.SIT_ROOT_FILE_URL
                                    + curMusic.getMfname();
                        } else {
                            url_mp3 = curMusic.getMfname();
                        }

                        iwitDownloadManager.deleteDatabaseAndLocalFile(url_mp3,
                                getPackageName());
                        MusicDas.deleteNativeSource(curMusic);// 删除下载资源
                        curMusic.setDownloaded(0);
                        handlerUI.sendEmptyMessage(1000);
                    }

                    break;
                case R.id.iv_music_prior:
                    mIMusicManager.priorMusic();
                    setPlayImage();
                    break;
                case R.id.iv_music_play:

                    if (CommConst.isSet) {
                        mIMusicManager.playMusic(MusicListControl.getInstance()
                                .getCurPosition());
                        CommConst.isSet = false;
                    } else {

                        if (state) {
                            mIMusicManager.pauseMusic();
                        } else {
                            mIMusicManager.resumeMusic();
                        }
                    }

				/*if (state) {
					mIMusicManager.pauseMusic();
				} else {
					mIMusicManager.resumeMusic();
				}*/
                    break;
                case R.id.iv_music_next:
                    mIMusicManager.nextMusic();
                    setPlayImage();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void downloadMusic(Music music) {
        if (NetWorkTools.getAPNType(this) == -1) {
            Toast.makeText(this, "没有连接网络.", 1).show();
            return;
        }
        String mp3Name = music.getMfname();
        String url_mp3 = CommConst.SIT_ROOT_FILE_URL + music.getMfname();
        if (mp3Name == null) {
            Toast.makeText(this, "没有MP3资源可提供下载", 1).show();
            return;
        }
        if (music.getMid().startsWith("s1")) {
            iwitDownloadManager.startDownload(url_mp3, mp3Name,
                    CommConst.MUSIC_FILE_PATH);
        } else {
            url_mp3 = music.getMfname();
            mp3Name = FileUtils.getFilename(url_mp3);
            iwitDownloadManager.startDownload(url_mp3, mp3Name,
                    CommConst.MUSIC_FILE_PATH);
        }
        downloadLrc(music);
        music.setDownloaded(1);
    }

    private void downloadLrc(Music music) {
        if (StringUtils.isEmpty(music.getLyric()) || StringUtils.isEmpty(music.getMid())) {
            return;
        }
        String lrcName = music.getMname() + ".lrc";
        String url_file = CommConst.SIT_ROOT_FILE_URL + music.getLyric();
        String lrc = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + CommConst.MUSIC_LRC_PATH
                + music.getMname() + ".lrc";

        File file = new File(lrc);
        if (!file.exists()) {
            if (music.getMid().startsWith("s1")) {
                //判断是够正在下载或者是否有该文件
                iwitDownloadManager.startDownload(url_file, lrcName,
                        CommConst.MUSIC_LRC_PATH);
            }
        }
    }

    // 监听
    private void rigisterContentObserver() {
        if (mMyContentObserver == null) {
            return;
        }
        App.ctx.getContentResolver().registerContentObserver(
                Downloads.Impl.CONTENT_URI, true, mMyContentObserver);
    }

    // 取消监听
    private void unrigisterContentObserver() {
        if (mMyContentObserver == null) {
            return;
        }
        App.ctx.getContentResolver().unregisterContentObserver(
                mMyContentObserver);
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
            scanDownloadInfoBean(listDownloadInfoBean);
        }
    };

    private void initUrl() {
        if (curMusic.getMid().startsWith("s1")) {
            url_path = CommConst.SIT_ROOT_FILE_URL + curMusic.getMfname();
        } else {
            url_path = curMusic.getMfname();
        }
    }

    private void initDownloadInfoBean() {
        List<DownloadInfoBean> listDownloadInfoBean = iwitDownloadManager
                .queryDownloadInfoByPackageName(CommConst.PACKAGE_NAME);
        scanDownloadInfoBean(listDownloadInfoBean);
    }

    private void scanDownloadInfoBean(
            List<DownloadInfoBean> listDownloadInfoBean) {
        if (curMusic == null) {
            return;
        }
        if (listDownloadInfoBean != null) {
            for (DownloadInfoBean info : listDownloadInfoBean) {
                String url = info.getDownload_uri();
                Log.v("player",
                        "info.getDownload_uri():" + info.getDownload_uri());
                Log.v("player", "url_path:" + curMusic.getMname());
                int status = info.getDownload_status();
                Log.v("player", "status:" + status);
                if (url.equals(url_path)) {
                    curMusic.setDownloaded(1);
                    if (status == CommConst.STATUS_READY) {
                        curMusic.setDownloaded(4);
                        MusicDas.insertMusicBean2Db(curMusic);
                    }
                }
            }
        }
        // 如果在数据库找不到 则为未下载 录音去外
        if (curMusic.getIsrecord() != null) {
            if (curMusic.getIsrecord() == 0) {
                if (listDownloadInfoBean == null
                        || listDownloadInfoBean.size() <= 0
                        || listDownloadInfoBean.isEmpty()) {
                    curMusic.setDownloaded(0);
                }
            }
        }
        handlerUI.sendEmptyMessage(1000);
    }

    ;

    // 判断是否为已下载的音乐 如果是的话可以删除 如果不是不可点击
    private void deleteMusic() {

    }

    @Override
    public void updateDuration() {
        totalTime = msgService.getDuration();
        String totalTime1 = StringUtils.formatTime(totalTime);
        Log.v(TAG, "totalTime:" + totalTime1);
        mTvMusicTotalTime.setText(totalTime1);
    }

    @Override
    public void startLoadMusic() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.v("MainActivity", "ss:" + keyCode);
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    mIMusicManager.resumeMusic();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    mIMusicManager.pauseMusic();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    mIMusicManager.nextMusic();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    mIMusicManager.priorMusic();
                    return true;
            }
            return super.onKeyDown(keyCode, event);
        } catch (Exception e) {
            return super.onKeyDown(keyCode, event);
        }
    }
}
