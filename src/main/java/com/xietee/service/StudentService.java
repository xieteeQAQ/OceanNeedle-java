package com.xietee.service;

import com.xietee.dao.BookMapper;
import com.xietee.dao.StudentMapper;
import com.xietee.dto.BookSaveDTO;
import com.xietee.dto.BookUpdateDTO;
import com.xietee.dto.StudentSaveDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class StudentService {

    private final StudentMapper studentMapper;
    private final BookMapper bookMapper;

    public StudentService(StudentMapper studentMapper, BookMapper bookMapper) {
        this.studentMapper = studentMapper;
        this.bookMapper = bookMapper;
    }

    // 学生端只提供个人信息修改功能
    public int updateStudentById(Long id, StudentUpdateDTO studentUpdateDTO) {
        Student student = studentMapper.findById(id).orElseThrow(
                () -> new BusinessException(UPDATE_FAIL));

        student.setId(id);
        student.setPassword(studentUpdateDTO.password());
        student.setPhoneNumber(studentUpdateDTO.phoneNumber());

        return studentMapper.updateById(student);
    }


    public boolean existedStudentID(String studentID) {

        Optional<Student> student = studentMapper.findByStudentID(studentID);
        return student.isPresent();
    }

    public boolean existedPhoneNumber(String phoneNumber) {

        Optional<Student> student = studentMapper.findByPhoneNumber(phoneNumber);
        return student.isPresent();
    }

    /*
    操作书籍数据
     */
    public List<Book> findAllBook() {
        List<Book> books = bookMapper.findAll();

        if (books.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }
        return books;
    }

    public Book findBookById(Long id) {
        Book book = bookMapper.findById(id).orElseThrow(
                () -> new BusinessException(
                        BOOK_NOT_FOUND.getCode(),
                        BOOK_NOT_FOUND.getMessage() + " id: " + id));
        return book;
    }

    public List<Book> findBookByISBN(String ISBN) {
        List<Book> books = bookMapper.findByISBN(ISBN);
        if (books.isEmpty()) {
            throw new BusinessException(
                    BOOK_NOT_FOUND.getCode(),
                    BOOK_NOT_FOUND.getMessage() + " ISBN: " + ISBN);
        }
        return books;
    }

    public List<Book> findBookByTitle(String title) {
        List<Book> books = bookMapper.findByTitle(title);
        if (books.isEmpty()) {
            throw new BusinessException(
                    BOOK_NOT_FOUND.getCode(),
                    BOOK_NOT_FOUND.getMessage() + " 书名: " + title);
        }
        return books;
    }

    public List<Book> findBookByAuthor(String author) {
        List<Book> books = bookMapper.findByAuthor(author);
        if (books.isEmpty()) {
            throw new BusinessException(
                    BOOK_NOT_FOUND.getCode(),
                    BOOK_NOT_FOUND.getMessage() + " 书籍作者: " + author);
        }
        return books;
    }

    public List<Book> findBookByAvailable(boolean available) {
        List<Book> books = bookMapper.findByAvailable(available);
        if (books.isEmpty()) {
            throw new BusinessException(
                    BOOK_NOT_FOUND.getCode(),
                    BOOK_NOT_FOUND.getMessage() + " 是否借出: " + Book.map(available));
        }
        return books;
    }

    // 个体可借查询

    public List<Book> findAllAvailableBook() {

        List<Book> books = bookMapper.findAllAvailable();

        if (books.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }
        return books;
    }

    // 模糊搜索
    public List<Book> fuzzySearchAvailableBook (String keyword) {

        if (keyword.isEmpty()) return findAllAvailableBook();

        return bookMapper.fuzzySearch(keyword);
    }
}
