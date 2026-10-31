package com.xietee.dto;

import com.xietee.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookUpdateDTO(

        @NotBlank(message = "书名不能为空")
        @Size(max = 255, message = "书名长度过长")
        String title,

        @NotBlank(message = "作者名字不能为空")
        @Size(max = 100, message = "作者名字过长")
        String author) {

    /**
     * 书籍的可借状态由借阅/归还流程维护，管理员更新书籍时只比较书名和作者。
     */
    public boolean equals(Book book) {
        return book.getTitle().equals(title)
                && book.getAuthor().equals(author);
    }
}
