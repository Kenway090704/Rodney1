package com.iwit.rodney.comm.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (s == null || s.equals("null") || s.equals("NULL")) {
			return true;
		} else if ("".equals(s.trim())) {
			return true;
		}
		return false;
	}

	public static <T> boolean isListEmpty(List<T> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * ��֤�Ƿ����ֻ�����
	 */
	public static boolean isPhoneNum(String phoneNum) {
		String str = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(phoneNum);
		return m.matches();
	}

	/**
	 * @param param
	 */
	public static boolean checkParam(Object[] param) {
		if (param == null) {
			return false;
		}
		if (param.length != 2) {
			return false;
		}

		/*
		 * if (isEmpty(param[0]) || isEmpty(param[1])) { return false; }
		 */
		return true;
	}

	/**
	 * @param imei
	 * @return
	 */
	public static boolean isImei(String imei) {
		String str1 = "^[0-9]{10}$";
		String str2 = "^[0-9]{10}[,]{1}[0-9]{2}$";
		Pattern p1 = Pattern.compile(str1);
		Pattern p2 = Pattern.compile(str2);
		Matcher m1 = p1.matcher(imei);
		Matcher m2 = p2.matcher(imei);
		return m1.matches() || m2.matches();
	}

	public static String doubleNum(int num) {
		if (num >= 0 && num < 10) {
			return "0" + num;
		}
		return String.valueOf(num);
	}

	public static int getProgressValue(long total_size, long current_size) {
		int progress = 0;
		progress = (int) (100 * current_size / total_size);
		return progress;
	}

	public static String formatTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int hour;
		int minute;
		int seconds;
		if (totalSeconds >= 60) {
			seconds = totalSeconds % 60;
			minute = totalSeconds / 60;
			if (minute >= 60) {
				hour = minute / 60;
				minute = minute % 60;
				return doubleNum(hour) + ":" + doubleNum(minute) + ":"
						+ doubleNum(seconds);
			} else {
				return doubleNum(minute) + ":" + doubleNum(seconds);
			}

		} else {
			return "00:" + doubleNum(totalSeconds);
		}

	}
}
