package com.pocketdigi.plib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * 获取某天的开始时间 00:00:00.000
     * @param calendar
     * @return
     */
    public static long getStartTimeOfDay(Calendar calendar)
    {
        Calendar c=(Calendar)calendar.clone();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }

    /**
     * 今天的开始时间  00:00:00.000
     * @return
     */
    public static long getStartOfToday()
    {
        Calendar c=Calendar.getInstance();
        return getStartTimeOfDay(c);
    }

    /**
     * 传入时间是否为今日时间
     * @param timestamp
     * @return
     */
    public static boolean isToday(long timestamp)
    {
        long todayStart=getStartOfToday();
        long delta=timestamp-todayStart;
        return delta>0&&delta<24*60*60*1000;
    }

    /**
     * 传入时间 是否为明日 时间
     * @param timestamp
     * @return
     */
    public static boolean isTomorrow(long timestamp)
    {
        long tomorrowStart=getStartOfToday()+24*60*60*1000;
        long delta=timestamp-tomorrowStart;
        return delta>0&&delta<24*60*60*1000;
    }

    /**
     * 返回中文星期几
     * @param timestamp
     * @param mode 模式:0为周日到周六,1为星期日到星期六
     * @return 周日-周六 或 星期日-星期六
     */
    public static String getChineseWeekDay(long timestamp,int mode) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String[] days={"日","一","二","三","四","五","六"};
        int dayOfWeak = calendar.get(Calendar.DAY_OF_WEEK);
        switch (mode){
            case 0:
                return "周"+days[dayOfWeak-1];
            default:
                return "星期"+days[dayOfWeak-1];
        }
    }

}
