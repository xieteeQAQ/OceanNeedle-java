package com.xietee.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * numberUtils.isInteger 的测试：只接受正整数，且不允许前导 0 / 符号 / 小数。
 */
class numberUtilsTest {

    @Test
    @DisplayName("正整数：通过")
    void positiveIntegersPass() {
        assertTrue(numberUtils.isInteger("1"));
        assertTrue(numberUtils.isInteger("42"));
        assertTrue(numberUtils.isInteger("1000"));
    }

    @Test
    @DisplayName("0、负数、前导 0：不通过")
    void nonPositiveOrLeadingZeroFails() {
        assertFalse(numberUtils.isInteger("0"));
        assertFalse(numberUtils.isInteger("-1"));
        assertFalse(numberUtils.isInteger("01"));
        assertFalse(numberUtils.isInteger("+1"));
    }

    @Test
    @DisplayName("小数、字母、空串、空格：不通过")
    void nonNumericFails() {
        assertFalse(numberUtils.isInteger("1.0"));
        assertFalse(numberUtils.isInteger("abc"));
        assertFalse(numberUtils.isInteger(""));
        assertFalse(numberUtils.isInteger(" 1"));
        assertFalse(numberUtils.isInteger("1 "));
    }
}
