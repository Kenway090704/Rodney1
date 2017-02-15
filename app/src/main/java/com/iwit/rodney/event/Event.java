package com.iwit.rodney.event;

import de.greenrobot.event.EventBus;

public class Event {
	public final static int EVENT_GET_MUSIC_BY_TYPE = 1;
	public final static int EVENT_GET_MUSIC_BY_SEARCH = 2;
	public final static int EVENT_UPDATE_FAV = 3;
	public final static int EVENT_MUSIC_STOP = 4;
	private int type;
	private String msg;
	private boolean result;

	public Event(int type, String msg, boolean result) {
		this.type = type;
		this.msg = msg;
		this.result = result;
	}
	
	public void postEventToUI(){
		EventBus.getDefault().post(this);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

}
