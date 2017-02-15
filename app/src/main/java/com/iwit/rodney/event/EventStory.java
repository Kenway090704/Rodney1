package com.iwit.rodney.event;

public class EventStory {
	private boolean result;
	private String msg;

	public EventStory(boolean result, String msg) {
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
