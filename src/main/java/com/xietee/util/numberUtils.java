package com.xietee.util;

public class numberUtils {

    private numberUtils() {}

    public static boolean isInteger(String number) {

        return number.matches("^[1-9]\\d*$");
    }
}
