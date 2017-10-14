package hiapp.modules.dm.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Calendar getNextDaySqlString() {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, 1);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        return curDay;
    }

    public static Calendar getCurDayStartSqlString() {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, 0);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        return curDay;
    }

    public static Calendar getCurDayEndSqlString() {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, 0);
        curDay.set(Calendar.HOUR_OF_DAY, 23);
        curDay.set(Calendar.MINUTE, 59);
        curDay.set(Calendar.SECOND, 59);
        curDay.set(Calendar.MILLISECOND, 0);
        return curDay;
    }

    public static Boolean isSameDay(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDay() == date2.getDay();
    }

    public static Date getNextXDay(int deltaDayNum) {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, deltaDayNum);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        return curDay.getTime();
    }

    public static Date getNextXMinute(int deltaMinuteNum) {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.MINUTE, deltaMinuteNum);
        return curDay.getTime();
    }


}
