package com.xietee.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    private Long id;
    private String AdminID;
    private String password;
    private String phoneNumber;

    @Override
    public String toString() {
        return "管理员ID: "
                + AdminID
                + " 密码: "
                + password
                + " 电话号码: "
                + phoneNumber;
    }
}
