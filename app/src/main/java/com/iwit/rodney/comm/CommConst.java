package com.iwit.rodney.comm;

public class CommConst {

	public static final String DB_NAME = "listenworld.db";
	public static final int DB_VER = 1;

	public static final String SIT_ROOT_URL = "http://ds.weeocean.com/action/client/";
	public static final String SIT_ROOT_FILE_URL = "http://ds.weeocean.com/file/ds/";// 锟斤拷图片锟斤拷锟斤拷锟�
	public static final String SIT_ROOT_FILE_URL_OSS = "http://ds.weeocean.com/file/ds/";// 锟斤拷mp3锟斤拷zip

	public static final String REQUEST_MUSICTYPE_METHOD = "listmtype";
	public static final String REQUEST_MUSICLIST_METHOD = "listmusic";

	public static final String SEND_MUSIC_FAVORITE = "like";
	public static final String REQUEST_BOOKLIST_METHOD = "listbook";
	public static final String REQUEST_SUGGEST_METHOD = "comment";

	public static final String PACKAGE_NAME = "com.iwit.rodney";

	// sp
	public static final String SP_FILE = "com.iwit.rodney.sp";

	public static final String KEY_RESLUT = "result";

	public static final String RECORD_FILE_PATH = "/rodney/music/";

	public static final String MUSIC_FILE_PATH = "/rodney/music/";
	
	public static final String MUSIC_LRC_PATH = "/rodney/lrc/";
	
	public static final String ROOT_PIC_PATH = "/rodney/pic";
	public static final String ROOT_BOOK_PATH = "/rodney/book/";
	public static final String ROOT_USER_IMG_PATH = "/rodney/userImg";

	public static final int STATUS_NO_DOWNLOAD = 0;
	public static final int STATUS_DOWNLOADING = 192;
	public static final int STATUS_PAUSE = 193;
	public static final int STATUS_READY = 200;
	public static final int STATUS_MOUNT_BEGIN = 194;
	public static final int STATUS_MOUNT_ING = 195;
	public static final int STATUS_MOUNT_END = 196;
	public static final int STATUS_UNZIPING = 197;

	public static final int STATUS_READ_BOOK = 2 << 2;
	public static final int DOWNLOAD_FINISH = 200;

	public static final int OVER_DOWNLOAD = 300;

	public static final int PROGRESS_FULL = 100;

	public static final int FLAG_DOWNLOAD_NOT_BEGIN = 0;
	public static final int FLAG_DOWNLOADING = 1;
	public static final int FLAG_DOWNLOAD_PAUSE = 2;
	public static final int FLAG_DOWNLOAD_FINISH = 3;
	public static final int FLAG_DOWNLOAD_UNZIPED = 4;

	public static final int SORT_MUSIC = 1;// 音乐
	public static final int SORT_STORY = 2;// 故事
	public static final String METHOD_FIND = "findpwd";
	public static final String METHOD_LOGIN = "login";
	public static final String METHOD_REGIST = "regist";
	public static final String METHOD_ACTIVATE = "activate";
	public static final String METHOD_ACTIVATE_TIMES = "activatetablet";
	public static final String HTTP_POST_KEY_EMAIL = "name";
	public static final String HTTP_POST_KEY_PWD = "pwd";
	public static final String HTTP_POST_KEY_LANG = "lang";
	public static final String HTTP_POST_KEY_UID = "uid";
	public static final String HTTP_POST_KEY_CDK = "cdk";
	public static final String HTTP_POST_KEY_EMAILADRESS = "email";
	public static final String JSON_KEY_RESTYPES = "restypes";
	public static final String JSON_KEY_RESULT = "result";
	public static final String JSON_KEY_MSG = "msg";
	public static final String JSON_KEY_UID = "uid";

	// message
	public static final int MESSAGE_LOGIN_SUCCESS = 1001;
	public static final int MESSAGE_ACTIVATE_SUCCESS = 1002;
	public static final int MESSAGE_CANCEL_SUCCESS = 1003;
	public static final int MESSAGE_ACTIVATE_FAULT = 1004;
	public static final int MESSAGE_HAS_ACTIVATE = 1005;
	public static final int MESSAGE_WHAT_FAULT = 1006;
	// sp

	public static final String SP_KEY_CURRENT_USER_PassWord = "sp_key_current_user_password";

	public static final String SP_KEY_CURRENT_USER_UID = "sp_key_current_user_uid";// sp_key_current_user
																					// ->uid
	public static final String SP_KEY_CURRENT_USER_EMAIL = "sp_key_current_user_email";// sp_key_current_user_email
																						// ->name
	public static final String SP_KEY_CURRENT_RESTYPES = "sp_key_current_restypes";// sp_key_current_restypes
																					// ->
																					// a0a1....
	public static final String SP_REGION_ISTABLETACTIVED = "sp_region_user_isactived";// sp_region_user_isactived|uid
																						// ->
																						// 0/1
	public static final String RESTYPE_FREE = "a0";
	public static final String SP_REGION_SYNCTIME = "sp_region_synctime";//

	public static final String ACTIVITY_FALG_MESSAGE_SUCCESS = "1";
	public static final String ACTIVITY_FALG_MESSAGE_FALUT = "2";
	public static final String ACTIVITY_FALG_MESSAGE_LOGIN_SUCCESS = "3";

	public static final String ACTIVITY_KEY_RESULT = "result";
	public static final String ACTIVITY_KEY_FALG = "flag";
	public static final String ACTIVITY_KEY_CONTENT = "content";

	//
	public static final int DEV_HAD_ACTIVATE = 1;
	public static final int DEV_NO_ACTIVATE = 0;
	public static final String PLATFORM_VAL = "2";

	public static final String ICON_PATH = "home_icon_path";
	
	public static String name = "";
	public static int CDNUMBER = 0;
	public final static String RECORD_TYPE = "10";
	public static boolean isSet = false;
}
