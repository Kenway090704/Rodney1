/**
 * copyright @ iwit,. android team. 2014-2015
 */
package com.iwit.rodney.exception;

/**
 * 定工具包里的处理异常，通常可以作一些参数检查，异常归类
 * @author tao
 *
 */
public class IwitApiException extends Exception {
	private static final long serialVersionUID = -5273997714779097651L;
	
	public IwitApiException(String desc) {
		super(desc);
	}
	/**
	 * 捕获的异常，通常是在执行api相关处理时捕获的，统一归类为IwitApiException<br>
	 * 此异常供外部调用时再捕获
	 * @param desc
	 * @param e
	 */
	public IwitApiException(String desc, Exception e) {
		super(desc, e);
	}
}
