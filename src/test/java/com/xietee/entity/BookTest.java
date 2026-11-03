package com.xietee.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Book 实体的状态映射与展示文案测试。
 */
class BookTest {

    private static Book book(boolean available, Integer availableCount, Integer totalCount) {
        return new Book(1L, "9787111000001", "Java编程思想", "张三", available,
                availableCount, totalCount);
    }

    @Test
    @DisplayName("map(boolean)：可借为“是”，已借出为“否”")
    void mapsBooleanToChineseLabel() {
        assertEquals("是", Book.map(true));
        assertEquals("否", Book.map(false));
    }

    @Test
    @DisplayName("map(String)：“是”可借 “否”为不可借 其他默认可借")
    void mapsChineseLabelToBoolean() {
        assertTrue(Book.map("是"));
        assertTrue(Book.map("其他"));
        assertFalse(Book.map("否"));
    }

    @Test
    @DisplayName("toString：没有统计数据时展示“是否可借”")
    void toStringWithoutCountsShowsAvailability() {
        String text = book(true, null, null).toString();

        assertTrue(text.contains("9787111000001"));
        assertTrue(text.contains("Java编程思想"));
        assertTrue(text.contains("张三"));
        assertTrue(text.contains("是否可借: 是"));
    }

    @Test
    @DisplayName("toString：有统计数据时展示“剩余 x/y”")
    void toStringWithCountsShowsRemainingCopies() {
        String text = book(true, 2, 3).toString();

        assertTrue(text.contains("剩余: 2/3"));
    }
}
