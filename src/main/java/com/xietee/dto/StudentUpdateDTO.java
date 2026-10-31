package com.xietee.dto;

import com.xietee.entity.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentUpdateDTO(


        @NotBlank(message = "密码不能为空")
        @Size(max = 16, min = 8, message = "密码长度至少为 8 ，最长不超过 16")
        String password,

        @NotBlank(message = "电话号码不能为空")
        @Size(max = 11,  min = 11, message = "电话号码应为 11 位数")
        String phoneNumber) {

        public boolean equals(Student student) {
                return student.getPassword().equals(password)
                        && student.getPhoneNumber().equals(phoneNumber);
        }
}