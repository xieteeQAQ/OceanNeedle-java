package com.xietee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentSaveDTO(
        @NotBlank(message = "学生学号不能为空")
        @Size(max = 12, min = 12, message = "学生学号必须为 12 位数")
        String studentID,

        @NotBlank(message = "密码不能为空")
        @Size(max = 32, min = 8, message = "密码长度至少为 8 ，最长不超过 16")
        String password,

        @NotBlank(message = "姓名不能为空")
        @Size(max = 10, message = "姓名长度不能超过 10")
        String name,

        @NotBlank(message = "电话号码不能为空")
        @Size(max = 11, min = 11, message = "电话号码应为 11 位数")
        String phoneNumber) {}
