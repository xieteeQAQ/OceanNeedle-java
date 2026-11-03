package com.xietee.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TimeUtils.nowTime 的测试：只验证返回的是约定的四个时段之一。
 * 具体时段依赖系统时间，不做逐小时断言。
 */
class TimeUtilsTest {

    private static final Set<String> PERIODS = Set.of("上午", "中午", "下午", "晚上");

    @Test
    @DisplayName("nowTime：返回上午/中午/下午/晚上之一")
    void nowTimeReturnsKnownPeriod() {
        assertTrue(PERIODS.contains(TimeUtils.nowTime()),
                () -> "未知时段: " + TimeUtils.nowTime());
    }
}
