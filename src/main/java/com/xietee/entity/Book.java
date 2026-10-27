package com.xietee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    private Long id;
    private String ISBN;
    private String title;
    private String author;
    private boolean available;

    private Integer availableCount;
    private Integer totalCount;

    public static String map(boolean available) {
        return available ? "是" : "否";
    }

    public static boolean map(String available) {
         if (available.equals("是")) {
             return true;
         } else return !available.equals("否");
    }

    @Override
    public String toString() {
        String string;

        if (availableCount != null && totalCount != null) {
            string = "ISBN: "
                    + ISBN
                    + " 书名: "
                    + title
                    + " 作者: "
                    + author
                    + " 剩余: "
                    + availableCount + '/' + totalCount;
        } else {
            string = " 序号: "
                    + id
                    + "ISBN: "
                    + ISBN
                    + " 书名: "
                    + title
                    + " 作者: "
                    + author
                    + " 是否借出: "
                    + map(available);
        }
        return string;
    }
}
