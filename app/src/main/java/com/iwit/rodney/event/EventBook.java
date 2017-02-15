package com.iwit.rodney.event;

public class EventBook {
	private boolean result;
	private String msg;

	public EventBook(boolean result, String msg) {
		super();
		this.result = result;
		this.msg = msg;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
