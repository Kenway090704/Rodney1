package com.iwit.rodney.event;

import de.greenrobot.event.EventBus;

public class EventAccount {
	private int result;
	private String msg;

	public EventAccount(int result, String msg) {
		// TODO Auto-generated constructor stub
		this.result = result;
		this.msg = msg;
	}

	public void postToUi() {
		EventBus.getDefault().post(this);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
