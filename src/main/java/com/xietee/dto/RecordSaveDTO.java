package com.xietee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordSaveDTO(

        @NotBlank(message = "学生学号不能为空")
        @Size(max = 12, min = 12, message = "学生学号必须为 12 位数")
        String studentID
) {
}
