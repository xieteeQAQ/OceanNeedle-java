package com.xietee.dao;

import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentMapper {

    // 查询
    // 暂时想不到要靠密码查询的情况       "select id, studentID, password, name, gender, phoneNumber, isResiding, checkInTime from student")
    List<Student> findAll();

    Optional<Student> findById(long id);

    Optional<Student> findByStudentID(String studentID);

    List<Student> findByName(String name); // 名字可能会重名，查询到多个对象

    Optional<Student> findByPhoneNumber(String phoneNumber);

    // 插入
    int insertStudent(Student student);

    // 更新
    int updateById(Student student);

    // 删除
    int deleteById(Long id);
}
