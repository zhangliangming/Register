package com.zlm.libs.register.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangliangming on 2018-06-02.
 */

public class DateUtils {
    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }
}
