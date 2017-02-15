package com.iwit.rodney.interfaces;

import com.iwit.rodney.entity.Account;

public interface IAccount {
	public void register(Account account);//注册账户
	public void saveOrUpdateAccount(Account account);//保存或者更新账户
	public void login(Account account);//账户登录
	public void active(Account account);//激活账户
	public int getCurrentAccountStaus();//获取当前账户的状态，账户的状态  0 表示未登录 1表示登陆未激活 2表示激活
	public String getCurrentAccountRestypes();
	public String getCurrentUid();
	public Account getCurrentAccount();
}
