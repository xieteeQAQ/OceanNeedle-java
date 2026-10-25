package com.xietee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private Long id;
    private String studentID;
    private String password;
    private String name;
    private String phoneNumber;

    @Override
    public String toString() {
        return "学号: "
                + studentID
                + " 密码: "
                + password
                + " 姓名: "
                + name
                + " 电话号码: "
                + phoneNumber;
    }
}
