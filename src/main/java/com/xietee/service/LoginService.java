package com.xietee.service;

import com.xietee.dao.AdminMapper;
import com.xietee.dao.StudentMapper;
import com.xietee.entity.Admin;
import com.xietee.entity.Student;

import java.util.Optional;

public class LoginService {

    private final StudentMapper studentMapper;
    private final AdminMapper adminMapper;

    public LoginService(StudentMapper studentMapper, AdminMapper adminMapper) {
        this.studentMapper = studentMapper;
        this.adminMapper = adminMapper;
    }

    public Optional<Student> LoginFindStudentByStudentID(String studentID) {
        return studentMapper.findByStudentID(studentID);
    }

    public Optional<Admin> LoginFindAdminByAdminID(String adminID) {
        return adminMapper.findByAdminID(adminID);
    }
}
