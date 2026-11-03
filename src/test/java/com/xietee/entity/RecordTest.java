package com.xietee.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Record 实体展示文案测试。
 */
class RecordTest {

    @Test
    @DisplayName("toString：记录编号、书籍编号、ISBN 和借还时间都完整展示")
    void toStringContainsAllFields() {
        Record record = new Record(
                1L,
                10L,
                "202400010012",
                "9787111000001",
                LocalDateTime.of(2026, 1, 2, 9, 30),
                LocalDateTime.of(2026, 1, 5, 18, 0));

        String text = record.toString();

        assertTrue(text.contains("学号: 202400010012"));
        assertTrue(text.contains("学号: 202400010012 编号: 1 书籍编号: 10"),
                "编号应为记录 id，可传给 ret -i");
        assertTrue(text.contains("9787111000001"));
        assertTrue(text.contains("2026-01-02T09:30"));
        assertTrue(text.contains("2026-01-05T18:00"));
    }

    @Test
    @DisplayName("toString：未归还时归还时间显示 未归还")
    void toStringShowsNullReturnTime() {
        Record record = new Record(
                1L, 10L, "202400010012", "9787111000001",
                LocalDateTime.of(2026, 1, 2, 9, 30), null);

        assertTrue(record.toString().contains("归还时间: 未归还"));
    }
}
