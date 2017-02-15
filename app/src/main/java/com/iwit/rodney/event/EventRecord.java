package com.iwit.rodney.event;

public class EventRecord {
	private boolean result;
	private String msg;
	public EventRecord(){
		
	}
	public EventRecord(boolean result, String msg) {
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
