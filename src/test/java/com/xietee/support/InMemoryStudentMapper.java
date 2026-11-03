package com.xietee.support;

import com.xietee.dao.StudentMapper;
import com.xietee.entity.Student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 内存版 StudentMapper，用于 StudentService 的纯单元测试（不启动数据库）。
 *
 * <p>实现语义尽量与 StudentMapper.xml 保持一致：
 * 读取返回副本，避免测试拿到 Service 内部的同一个对象而产生"假通过"。</p>
 */
public class InMemoryStudentMapper implements StudentMapper {

    private final Map<Long, Student> rows = new LinkedHashMap<>();
    private long sequence = 0;

    /** 预置一条数据，返回保存后的副本（id 已赋值）。 */
    public Student seed(String studentID, String password, String name, String phoneNumber) {
        Student student = new Student(++sequence, studentID, password, name, phoneNumber);
        rows.put(student.getId(), student);
        return copy(student);
    }

    /** 当前行数，方便测试断言删除是否生效。 */
    public int size() {
        return rows.size();
    }

    @Override
    public List<Student> findAll() {
        return rows.values().stream().map(InMemoryStudentMapper::copy).toList();
    }

    @Override
    public Optional<Student> findById(long id) {
        return Optional.ofNullable(rows.get(id)).map(InMemoryStudentMapper::copy);
    }

    @Override
    public Optional<Student> findByStudentID(String studentID) {
        return rows.values().stream()
                .filter(s -> s.getStudentID().equals(studentID))
                .findFirst()
                .map(InMemoryStudentMapper::copy);
    }

    @Override
    public List<Student> findByName(String name) {
        return rows.values().stream()
                .filter(s -> s.getName().equals(name))
                .map(InMemoryStudentMapper::copy)
                .toList();
    }

    @Override
    public Optional<Student> findByPhoneNumber(String phoneNumber) {
        return rows.values().stream()
                .filter(s -> s.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .map(InMemoryStudentMapper::copy);
    }

    @Override
    public int insertStudent(Student student) {
        Student saved = copy(student);
        saved.setId(++sequence);
        rows.put(saved.getId(), saved);
        return 1;
    }

    @Override
    public int updateById(Student student) {
        if (student.getId() == null || !rows.containsKey(student.getId())) {
            return 0;
        }
        rows.put(student.getId(), copy(student));
        return 1;
    }

    @Override
    public int deleteById(Long id) {
        return rows.remove(id) == null ? 0 : 1;
    }

    private static Student copy(Student student) {
        return new Student(
                student.getId(),
                student.getStudentID(),
                student.getPassword(),
                student.getName(),
                student.getPhoneNumber());
    }
}
