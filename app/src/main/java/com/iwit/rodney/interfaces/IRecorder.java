package com.iwit.rodney.interfaces;

public interface IRecorder {

	/**
	 * 开始录制
	 */
	void start();

	/**
	 * 结束录制
	 * 
	 * @return 返回保存录音的文件路径
	 */
	String stop();

	/**
	 * 暂停录制
	 * 
	 * @return 返回保存录音的文件路径
	 */
	String pause();

	/**
	 * 恢复录制
	 */
	void resume();

}
