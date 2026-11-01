package com.xietee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {

    private Long id;
    private Long bookID;
    private String studentID;
    private String ISBN;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;

    @Override
    public String toString() {
        return String.format(
                "学号: %s 编号: %d 书籍编号: %d ISBN: %s 借出时间: %s 归还时间: %s",
                studentID, id, bookID, ISBN, borrowTime, returnTime == null ? "未归还" : returnTime);
    }
}
