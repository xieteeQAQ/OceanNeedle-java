package com.xietee.util;

import java.time.LocalTime;

public class TimeUtils {

    private TimeUtils() {};

    public static String nowTime() {

        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(6, 0))
                || now.isAfter(LocalTime.of(18, 0))) {
            return "晚上";
        } else if (now.isAfter(LocalTime.of(13, 0))) {
            return "下午";
        } else if (now.isAfter(LocalTime.of(12, 0))) {
            return "中午";
        } else {
            return "上午";
        }
    }
}
