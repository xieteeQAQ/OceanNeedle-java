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

    public static String map(boolean available) {
        return available ? "是" : "否";
    }

    public static boolean map(String available) {
        return available.equals("是");
    }

    @Override
    public String toString() {
        return "ISBN: "
                + ISBN
                + " 书名: "
                + title
                + " 作者: "
                + author
                + " 是否借出: "
                + available;
    }
}
