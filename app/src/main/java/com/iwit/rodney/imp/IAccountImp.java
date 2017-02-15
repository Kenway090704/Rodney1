package com.iwit.rodney.imp;

import java.util.Map;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.event.EventAccount;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IAccount;
import com.iwit.rodney.ui.activity.ActiveActivity;
import com.iwit.rodney.ui.activity.UserCenterActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class IAccountImp implements IAccount {
	private Context mContext;

	public IAccountImp(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public IAccountImp() {
		if(mContext == null){
			mContext = App.ctx;
		}
	}

	@Override
	public void register(final Account account) {
		// TODO Auto-generated method stub
		final String email = account.getEmail();
		final String password = account.getPassword();
		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {
			EventAccount eventAccount;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				if (arg0 == 200) {
					String result = new String(arg2);
					try {
						Map<String, Object> mResult = JsonUtil
								.jsonString2Map(result);
						int status = Integer.parseInt(String.valueOf(mResult
								.get(CommConst.JSON_KEY_RESULT)));
						if (status > 0) {
							Object uid = mResult.get(CommConst.JSON_KEY_UID);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_UID, uid);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_EMAIL, email);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_PassWord,
									password);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_RESTYPES,
									CommConst.RESTYPE_FREE);
							account.setUid(Integer.parseInt(uid.toString()));
							account.setEmail(email);
							account.setPhonenum("");
							account.setRestypes(CommConst.RESTYPE_FREE);
							account.setIscurrent(0);
							account.setNickname("");
							saveOrUpdateAccount(account);
							int eResult = CommConst.MESSAGE_LOGIN_SUCCESS;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						} else {
							int eResult = CommConst.MESSAGE_WHAT_FAULT;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						}
					} catch (IwitApiException e) {
						int eResult = CommConst.MESSAGE_WHAT_FAULT;
						String eMsg = mContext.getResources().getString(
								R.string.toast_no_net);
						eventAccount = new EventAccount(eResult, eMsg);
						eventAccount.postToUi();
						e.printStackTrace();
					}

				} else {
					int eResult = CommConst.MESSAGE_WHAT_FAULT;
					String eMsg = mContext.getResources().getString(
							R.string.toast_no_net);
					eventAccount = new EventAccount(eResult, eMsg);
					eventAccount.postToUi();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				int eResult = CommConst.MESSAGE_WHAT_FAULT;
				String eMsg = mContext.getResources().getString(
						R.string.toast_no_net);
				eventAccount = new EventAccount(eResult, eMsg);
				eventAccount.postToUi();
			}

		}, CommConst.METHOD_REGIST, new String[][] {
				{ CommConst.HTTP_POST_KEY_EMAIL, email },
				{ CommConst.HTTP_POST_KEY_PWD, password } });
	}

	@Override
	public void saveOrUpdateAccount(Account account) {
		// TODO Auto-generated method stub
		String uid = getCurrentUid();
		String sql = "select count(*) from lw_user where uid=?";
		try {
			String count = App.getDbUtil().queryFirst(String.class, sql,
					Integer.valueOf(uid));
			int num = Integer.parseInt(count);
			System.out.println("xnum:" + num);
			if (num > 0) {// Update
				StringBuilder sb = new StringBuilder(
						"update lw_user set name='").append(account.getName())
						.append("',email='").append(account.getEmail())
						.append("',phonenum='").append(account.getPhonenum())
						.append("',restypes='").append(account.getRestypes())
						.append("',password='").append(account.getPassword())
						.append("',iscurrent='").append(account.getIscurrent())
						.append("' where uid=?");
				App.getDbUtil().cud(sb.toString(), Integer.parseInt(uid));
			} else {// save/insert
				StringBuilder sb = new StringBuilder(
						"insert into lw_user values('")
						.append(account.getUid()).append("', '")
						.append(account.getName()).append("', '")
						.append(account.getEmail()).append("', '")
						.append(account.getPhonenum()).append("', '")
						.append(account.getRestypes()).append("', '")
						.append(account.getPassword()).append("', '")
						.append(account.getIscurrent()).append("', '")
						.append(account.getNickname()).append("', '")
						.append(account.getAge()).append("', '")
						.append(account.getSex()).append("')");
				App.getDbUtil().cud(sb.toString(), null);//警告
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IwitApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void login(final Account account) {
		final String email = account.getEmail();
		final String password = account.getPassword();

		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {
			EventAccount eventAccount;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				if (arg0 == 200) {
					String result = new String(arg2);
					System.out.println(result);//使用Log来打印登陆的状态!--xk
				//result =={"restypes":"a0a1b1","uid":945,"result":1,"msg":"Login success"}--xk
					try {
						Map<String, Object> mResult = JsonUtil
								.jsonString2Map(result);
						int status = Integer.valueOf(mResult.get(
								CommConst.JSON_KEY_RESULT).toString());//正常登陆的话此处为status==1--xk
						if (status > 0) {
							String restypes = mResult.get(
									CommConst.JSON_KEY_RESTYPES).toString();
                                  //"restypes":"a0a1b1"--测试账号
							Object uid = mResult.get(CommConst.JSON_KEY_UID);
							      //"uid":945
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_UID, uid);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_EMAIL, email);
							App.getSp()
									.putKv(CommConst.SP_KEY_CURRENT_RESTYPES,
											restypes);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_USER_PassWord,
									password);
							if(StringUtils.isEmpty(restypes) || restypes.equals("a0")){
								App.getSp().putKv2Region(
										CommConst.SP_REGION_ISTABLETACTIVED,
										getCurrentUid(), CommConst.DEV_NO_ACTIVATE);
							}else{
								App.getSp().putKv2Region(
										CommConst.SP_REGION_ISTABLETACTIVED,
										getCurrentUid(), CommConst.DEV_HAD_ACTIVATE);
							}
							

							account.setUid(Integer.parseInt(uid.toString()));
							Account currentAccount = getCurrentAccount();
							String name = "";
							int sex = 0;
							int age = 0;
							if (currentAccount.getName() != null) {
								name = currentAccount.getName();
							} else {
								name = "";
							}
							if (currentAccount.getSex() != null) {
								sex = currentAccount.getSex();
							}
							if (currentAccount.getAge() != null) {
								age = currentAccount.getAge();
							}
							account.setName(name);
							account.setIscurrent(1);
							account.setRestypes(restypes);
							account.setSex(sex);
							account.setAge(age);
							account.setPhonenum("");
							saveOrUpdateAccount(account);
							Intent exitIntent = new Intent(mContext,
									UserCenterActivity.class);
							mContext.startActivity(exitIntent);
						} else {
							int eResult = CommConst.MESSAGE_WHAT_FAULT;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();

						}
					} catch (IwitApiException e) {
						// TODO Auto-generated catch block
						int eResult = CommConst.MESSAGE_WHAT_FAULT;
						String eMsg = mContext.getResources().getString(
								R.string.toast_no_net);
						eventAccount = new EventAccount(eResult, eMsg);
						eventAccount.postToUi();
						e.printStackTrace();
					}

				} else {
					int eResult = CommConst.MESSAGE_WHAT_FAULT;
					String eMsg = mContext.getResources().getString(
							R.string.toast_no_net);
					eventAccount = new EventAccount(eResult, eMsg);
					eventAccount.postToUi();
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

				int eResult = CommConst.MESSAGE_WHAT_FAULT;
				String eMsg = mContext.getResources().getString(
						R.string.toast_no_net);
				eventAccount = new EventAccount(eResult, eMsg);
				eventAccount.postToUi();
			}

		}, CommConst.METHOD_LOGIN, new String[][] {
				{ CommConst.HTTP_POST_KEY_EMAIL, email },
				{ CommConst.HTTP_POST_KEY_PWD, password } });

	}

	@Override
	public void active(Account account) {
		// TODO Auto-generated method stub
		int status = getCurrentAccountStaus();
		String uid = getCurrentUid();
		String restypes = getCurrentAccountRestype();
		switch (status) {
		case 1:
			if (restypes.equals(CommConst.RESTYPE_FREE)) {// 账号未激活，要求输入激活码
				Intent activeIntent = new Intent(mContext, ActiveActivity.class);
				mContext.startActivity(activeIntent);
			} else {// 账号已激活,激活设备
				judgeActivateTimes(uid);
			}
			break;
		case 2:// 账号已激活
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.toast_activate_success),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	public void accountActivate(String code) {
		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {
			EventAccount eventAccount;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				if (arg0 == 200) {
					String result = new String(arg2);
					try {
						Map<String, Object> mResult = JsonUtil
								.jsonString2Map(result);
						int status = Integer.valueOf(mResult.get(
								CommConst.JSON_KEY_RESULT).toString());
						if (status > 0) {
							App.getSp()
									.putKv2Region(
											CommConst.SP_REGION_ISTABLETACTIVED,
											getCurrentUid(),
											CommConst.DEV_HAD_ACTIVATE);
							App.getSp().putKv(
									CommConst.SP_KEY_CURRENT_RESTYPES,
									mResult.get(CommConst.JSON_KEY_RESTYPES));
							Account account = getCurrentAccount();
							account.setRestypes("a0a1b1");
							saveOrUpdateAccount(account);
							int eResult = CommConst.MESSAGE_ACTIVATE_SUCCESS;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						} else {
							int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						}
					} catch (IwitApiException e) {
						int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
						String eMsg = mContext.getResources().getString(
								R.string.toast_no_net);
						eventAccount = new EventAccount(eResult, eMsg);
						eventAccount.postToUi();
						e.printStackTrace();
					}
				} else {
					int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
					String eMsg = mContext.getResources().getString(
							R.string.toast_no_net);
					eventAccount = new EventAccount(eResult, eMsg);
					eventAccount.postToUi();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
				String eMsg = mContext.getResources().getString(
						R.string.toast_no_net);
				eventAccount = new EventAccount(eResult, eMsg);
				eventAccount.postToUi();

			}
		}, CommConst.METHOD_ACTIVATE, new String[][] {
				{ CommConst.HTTP_POST_KEY_UID, getCurrentUid() },
				{ CommConst.HTTP_POST_KEY_CDK, code } });
	}

	private void judgeActivateTimes(final String uid) {
		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {
			EventAccount eventAccount;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				if (arg0 == 200) {
					String result = new String(arg2);
					try {
						Map<String, Object> mResult = JsonUtil
								.jsonString2Map(result);
						int status = Integer.parseInt(String.valueOf(mResult
								.get(CommConst.JSON_KEY_RESULT)));
						if (status > 0) {

							App.getSp().putKv2Region(
									CommConst.SP_REGION_ISTABLETACTIVED, uid,
									CommConst.DEV_HAD_ACTIVATE);
							int eResult = CommConst.MESSAGE_ACTIVATE_SUCCESS;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						} else {
							int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
							String eMsg = mResult.get(CommConst.JSON_KEY_MSG)
									.toString();
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						}
					} catch (IwitApiException e) {
						// TODO Auto-generated catch block
						int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
						String eMsg = mContext.getResources().getString(
								R.string.toast_no_net);
						eventAccount = new EventAccount(eResult, eMsg);
						eventAccount.postToUi();
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				int eResult = CommConst.MESSAGE_ACTIVATE_FAULT;
				String eMsg = mContext.getResources().getString(
						R.string.toast_no_net);
				eventAccount = new EventAccount(eResult, eMsg);
				eventAccount.postToUi();
			}
		}, CommConst.METHOD_ACTIVATE_TIMES, new String[] {
				CommConst.METHOD_ACTIVATE_TIMES, uid });
	}

	public void sendSuggest(String suggest) {
		String email = "";
		WebRequest.requestFromWeb(
				new AsyncHttpResponseHandler() {
					EventAccount eventAccount;

					@Override
					public void onSuccess(int status, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						if (status == 200) {
							String result = new String(arg2);
							try {
								Map<String, Object> mResult = JsonUtil
										.jsonString2Map(result);
								int flag = (Integer) mResult
										.get(CommConst.JSON_KEY_RESULT);
								if (flag == 1) {
									int eResult = flag;
									String eMsg = mContext
											.getResources()
											.getString(
													R.string.toast_sent_success);
									eventAccount = new EventAccount(eResult,
											eMsg);
									eventAccount.postToUi();
								} else {
									int eResult = flag;
									String eMsg = mContext
											.getResources()
											.getString(R.string.toast_sent_fail);
									eventAccount = new EventAccount(eResult,
											eMsg);
									eventAccount.postToUi();
								}
							} catch (IwitApiException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								int eResult = 0;
								String eMsg = mContext.getResources()
										.getString(R.string.toast_sent_fail);
								eventAccount = new EventAccount(eResult, eMsg);
								eventAccount.postToUi();
							}

						} else {
							int eResult = 0;
							String eMsg = mContext.getResources().getString(
									R.string.toast_sent_fail);
							eventAccount = new EventAccount(eResult, eMsg);
							eventAccount.postToUi();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						int eResult = 0;
						String eMsg = mContext.getResources().getString(
								R.string.toast_sent_fail);
						eventAccount = new EventAccount(eResult, eMsg);
						eventAccount.postToUi();
					}
				}, CommConst.REQUEST_SUGGEST_METHOD,
				new String[][] { { "lang", App.getCountryCode() },
						{ "content", "" + suggest }, { "synctime", "0" },
						{ "email", "" + email } });
	}

	// 获取当前账户的状态，账户的状态 0 表示未登录 1表示登陆未激活账户
	// 2表示激活账户，设备中也已经激活。
	@Override
	public int getCurrentAccountStaus() {
		String uid = getCurrentUid();
		String restypes = getCurrentAccountRestype();

		if (uid != null && !"".equals(uid)) {
			if (restypes.equals("a0")) {
				return 1;
			} else if (restypes.equals("a0a1")||restypes.equals("a0a1b1")) {
				Object isActivate = App.getSp().getKvOfRegion(
						CommConst.SP_REGION_ISTABLETACTIVED, uid);
				System.out.println("zsm:" + isActivate + "");
				if (isActivate == null
						|| CommConst.DEV_NO_ACTIVATE == Integer.valueOf(String
								.valueOf(isActivate))) {// 设备未激活

					return 1;
				} else {
					return 2;
				}

			}
		}
		return 0;
	}

	public String getCurrentAccountRestype() {
		// TODO Auto-generated method stub
		Object restypes = App.getSp().get(CommConst.SP_KEY_CURRENT_RESTYPES);
		if (restypes == null) {
			return CommConst.RESTYPE_FREE;
		}
		return String.valueOf(restypes);
	}

	@Override
	public String getCurrentAccountRestypes() {
		// TODO Auto-generated method stub
		String uid = getCurrentUid();
		if (uid == null) {
			return CommConst.RESTYPE_FREE;
		}
		Object restypes = App.getSp().getKvOfRegion(
				CommConst.SP_REGION_ISTABLETACTIVED, getCurrentUid());
		if (restypes == null
				|| Integer.parseInt(restypes.toString()) == CommConst.DEV_NO_ACTIVATE) {
			return CommConst.RESTYPE_FREE;
		}
		return "a0a1b1";
	}

	@Override
	public String getCurrentUid() {
		// TODO Auto-generated method stub
		Object uid = App.getSp().get(CommConst.SP_KEY_CURRENT_USER_UID);
		if (uid != null) {
			return String.valueOf(uid);
		}
		return "0";
	}

	@Override
	public Account getCurrentAccount() {
		// TODO Auto-generated method stub
		Object uid = App.getSp().get(CommConst.SP_KEY_CURRENT_USER_UID);
		if (uid != null) {
			Account account = null;
			String sql = "select * from lw_user where uid = ? ";
			try {
				account = App.getDbUtil().queryFirst(Account.class, sql,
						Integer.valueOf(String.valueOf(uid)));
				if (account == null) {
					account = new Account();
				}
			} catch (IwitApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return account;
		}
		return null;

	}

	/*
	 * 找回密码
	 */
	public void findPwd(String email) {
		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {
			EventAccount eventAccount;
			int status;
			String eMsg;

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				if (arg0 == 200) {
					String result = new String(arg2);
					Map<String, Object> mResult;

					try {
						mResult = JsonUtil.jsonString2Map(result);
						status = Integer.valueOf(mResult.get(
								CommConst.JSON_KEY_RESULT).toString());
						eMsg = mResult.get(CommConst.JSON_KEY_MSG).toString();
						eventAccount = new EventAccount(status, eMsg);
						eventAccount.postToUi();
					} catch (IwitApiException e) {
						status = CommConst.MESSAGE_WHAT_FAULT;
						eMsg = mContext.getResources().getString(
								R.string.toast_sent_fail);
						eventAccount = new EventAccount(status, eMsg);
						eventAccount.postToUi();
					}

				} else {
					status = CommConst.MESSAGE_WHAT_FAULT;
					eMsg = mContext.getResources().getString(
							R.string.toast_sent_fail);
					eventAccount = new EventAccount(status, eMsg);
					eventAccount.postToUi();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				status = CommConst.MESSAGE_WHAT_FAULT;
				eMsg = mContext.getResources().getString(
						R.string.toast_sent_fail);
				eventAccount = new EventAccount(status, eMsg);
				eventAccount.postToUi();

			}
		}, CommConst.METHOD_FIND, new String[][] { {
				CommConst.HTTP_POST_KEY_EMAILADRESS, email } ,{"psign","a1"}});
	}

}
