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

public class AdminService {

    private final StudentMapper studentMapper;
    private final BookMapper bookMapper;

    public AdminService(StudentMapper studentMapper, BookMapper bookMapper) {
        this.studentMapper = studentMapper;
        this.bookMapper = bookMapper;
    }

    /*
    操作学生数据
     */
    public List<Student> findAllStudent() {
        List<Student> students = studentMapper.findAll();

        if (students.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }
        return students;
    }

    public Student findStudentById(long id) {
        Student student = studentMapper.findById(id).orElseThrow(
                () -> new BusinessException(
                        STUDENT_NOT_FOUND.getCode(),
                        STUDENT_NOT_FOUND.getMessage() + " id: " + id));
        return student;
    }

    public Student findStudentByStudentID(String studentID) {
        Student student = studentMapper.findByStudentID(studentID).orElseThrow(
                () -> new BusinessException(
                        STUDENT_NOT_FOUND.getCode(),
                        STUDENT_NOT_FOUND.getMessage() + " 学号: " + studentID));
        return student;
    }

    public List<Student> findStudentByName(String name) {
        List<Student> students = studentMapper.findByName(name);
        if (students.isEmpty()) {
            throw new BusinessException(
                    STUDENT_NOT_FOUND.getCode(),
                    STUDENT_NOT_FOUND.getMessage() + " 姓名 " + name);
        }
        return students;
    }

    public Student findStudentByContractNumber(String contractNumber) {
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

    public int updateStudentById(Long id, StudentUpdateDTO studentUpdateDTO) {
        Student student = studentMapper.findById(id).orElseThrow(
                () -> new BusinessException(UPDATE_FAIL));

        student.setId(id);
        student.setPassword(studentUpdateDTO.password());
        student.setPhoneNumber(studentUpdateDTO.phoneNumber());

        return studentMapper.updateById(student);
    }

    public int deleteStudentById(Long id) {
        return studentMapper.deleteById(id);
    }

    // 模糊搜索
    public List<Student> fuzzySearchStudent (String keyword) {

        List<Student> allStudents = findAllStudent();
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

    // 模糊搜索
    public List<Book> fuzzySearchBook (String keyword) {

        List<Book> allBooks = findAllBook();
        if (keyword.isEmpty()) return allBooks;

        List<Book> result = new ArrayList<>();

        for (var book : allBooks) {
            if (book.getISBN().contains(keyword)
                    || book.getTitle().contains(keyword)
                    || book.getAuthor().contains(keyword)) {
                result.add(book);
            }
        }

        return result;
    }

    // 添加
    public int insertBook(BookSaveDTO bookSaveDTO) {
        Book book = new Book(
                null, bookSaveDTO.ISBN(),
                bookSaveDTO.title(),
                bookSaveDTO.author(),
                bookSaveDTO.available(), null, null);
        return bookMapper.insertBook(book);
    }

    // 更新
    public int updateBookById(Long id ,BookUpdateDTO bookUpdateDTO) {
        Book book = bookMapper.findById(id).orElseThrow(
                () -> new BusinessException(UPDATE_FAIL));

        book.setTitle(bookUpdateDTO.title());
        book.setAuthor(bookUpdateDTO.author());
        book.setAvailable(bookUpdateDTO.available());

        return bookMapper.updateById(book);
    }

    // 删除
    public int deleteBookById(Long id) {
        return bookMapper.deleteById(id);
    }
}
