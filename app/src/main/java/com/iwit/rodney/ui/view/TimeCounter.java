package com.iwit.rodney.ui.view;

public class TimeCounter {
	public static final int STATE_COUNTING = 1;
	public static final int STATE_PAUSING = 2;

	private int state;
	private long interval;// 暂停的时间 in millisecond
	private long startTime;

	private long pauseStart;

	public void start() {
		state = STATE_COUNTING;
		startTime = System.currentTimeMillis();
		interval = 0;
	}

	public void pause() {
		if (state != STATE_COUNTING) {
			throw new IllegalStateException();
		}
		state = STATE_PAUSING;
		pauseStart = System.currentTimeMillis();
	}

	public void resume() {
		if (state != STATE_PAUSING) {
			throw new IllegalStateException();
		}
		state = STATE_COUNTING;
		interval += System.currentTimeMillis() - pauseStart;
	}

	public long get() {
		if (state == STATE_PAUSING) {
			return pauseStart - startTime - interval;
		}
		return System.currentTimeMillis() - startTime - interval;
	}
}
