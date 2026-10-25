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
        String author,

        Boolean available) {

    public boolean equals(Book book) {
        return book.getTitle().equals(title)
                && book.getAuthor().equals(author)
                && book.isAvailable() == Boolean.TRUE.equals(available);
    }
}
