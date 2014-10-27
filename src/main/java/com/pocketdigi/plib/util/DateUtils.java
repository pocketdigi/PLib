package com.pocketdigi.plib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理类
 * @author fhp
 *
 */
public class DateUtils {
	/**
	 * 把字符串转换成Date
	 * @param format yyyy-MM-dd
	 * @param str 2012-12-30
	 * @return
	 */
	public static Date str2Date(String format, String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 把date转换成字符串
	 * @param format　yyyy-MM-dd
	 * @param date
	 * @return　2012-12-30
	 */
	public static String date2Str(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);

	}

	/**
	 * 把间隔时间转换成时间形式
	 * @param duration 毫秒
	 * @return HH:mm:ss 如参数为100*1000时，返回00:01:40
	 */
	public static String duration2Time(long duration)
	{
		int hour=(int)Math.floor(duration/(60*60*1000));
		int minute=(int)Math.floor((duration%(60*60*1000))/(60*1000));
		int second=(int)Math.floor((duration%(60*60*1000))%(60*1000)/1000);
		String str=String.format("%02d:%02d:%02d", hour,minute,second);
		return str;
	}
}
