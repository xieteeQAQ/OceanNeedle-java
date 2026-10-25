package com.xietee.service;

import com.xietee.dao.StudentMapper;
import com.xietee.dto.StudentSaveDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class AdminService {

    private final StudentMapper studentMapper;

    public AdminService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    public List<Student> findAll() {
        List<Student> students = studentMapper.findAll();

        if (students.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }
        return students;
    }

    public Student findById(long id) {
        Student student = studentMapper.findById(id).orElseThrow(
                () -> new BusinessException(
                        STUDENT_NOT_FOUND.getCode(),
                        STUDENT_NOT_FOUND.getMessage() + " id: " + id));
        return student;
    }

    public Student findByStudentID(String studentID) {
        Student student = studentMapper.findByStudentID(studentID).orElseThrow(
                () -> new BusinessException(
                        STUDENT_NOT_FOUND.getCode(),
                        STUDENT_NOT_FOUND.getMessage() + " 学号: " + studentID));
        return student;
    }

    public List<Student> findByName(String name) {
        List<Student> students = studentMapper.findByName(name);
        if (students.isEmpty()) {
            throw new BusinessException(
                    STUDENT_NOT_FOUND.getCode(),
                    STUDENT_NOT_FOUND.getMessage() + " 姓名 " + name);
        }
        return students;
    }

    public Student findByContractNumber(String contractNumber) {
        Student student = studentMapper.findByPhoneNumber(contractNumber).orElseThrow(
                () -> new BusinessException(
                        STUDENT_NOT_FOUND.getCode(),
                        STUDENT_NOT_FOUND.getMessage() + " 电话号码 = " + contractNumber));
        return student;
    }

    public int insertStudent(StudentSaveDTO studentSaveDTO) {
        Student student = new Student(
                null, studentSaveDTO.studentID(),
                studentSaveDTO.password(),
                studentSaveDTO.name(),
                studentSaveDTO.phoneNumber());
        return studentMapper.insertStudent(student);
    }

    public int updateById(Long id, StudentUpdateDTO studentUpdateDTO) {
        Student student = studentMapper.findById(id).orElseThrow(
                () -> new BusinessException(UPDATE_FAIL));

        student.setId(id);
        student.setPassword(studentUpdateDTO.password());
        student.setPhoneNumber(studentUpdateDTO.phoneNumber());

        return studentMapper.updateById(student);
    }

    public int deleteById(Long id) {
        return studentMapper.deleteById(id);
    }

    // 模糊搜索
    public List<Student> fuzzySearchStudent (String keyword) {

        List<Student> allStudents = findAll();
        if (keyword.isEmpty()) return allStudents;

        List<Student> result = new ArrayList<>();

        for (var student : allStudents) {
            if (student.getStudentID().contains(keyword)
                    || student.getName().contains(keyword)
                    || student.getPhoneNumber().contains(keyword)) {
                result.add(student);
            }
        }

        return result;
    }

    public boolean existedStudentID(String studentID) {

        Optional<Student> student = studentMapper.findByStudentID(studentID);
        return student.isPresent();
    }

    public boolean existedPhoneNumber(String phoneNumber) {

        Optional<Student> student = studentMapper.findByPhoneNumber(phoneNumber);
        return student.isPresent();
    }
}
