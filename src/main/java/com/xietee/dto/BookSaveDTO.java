package com.xietee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookSaveDTO(
        @NotBlank(message = "ISBN不能为空")
        @Size(max = 13, min = 13, message = "ISBN-13 必须为 13 位数")
        String ISBN,

        @NotBlank(message = "书名不能为空")
        @Size(max = 255, message = "书名长度过长")
        String title,

        @NotBlank(message = "作者名字不能为空")
        @Size(max = 100, message = "作者名字过长")
        String author,

        Boolean available
) {}
