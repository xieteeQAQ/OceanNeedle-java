package com.xietee.service;

import com.alibaba.fastjson2.JSONObject;
import com.xietee.cli.UserType;
import com.xietee.dao.BookMapper;
import com.xietee.dao.RecordMapper;
import com.xietee.dao.StudentMapper;
import com.xietee.dto.RecordSaveDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Book;
import com.xietee.entity.Record;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static com.xietee.exception.BusinessExceptionEnum.*;

public class StudentService {

    private final StudentMapper studentMapper;
    private final BookMapper bookMapper;
    private final RecordMapper recordMapper;

    public StudentService(StudentMapper studentMapper, BookMapper bookMapper, RecordMapper recordMapper) {
        this.studentMapper = studentMapper;
        this.bookMapper = bookMapper;
        this.recordMapper = recordMapper;
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
            throw new BusinessException(AVAILABLE_BOOK_NOT_FOUND);
        }
        return books;
    }

    // 同书籍可借查询
    public List<Book> findAllAvailableBookWithCount() {
        List<Book> books = bookMapper.findAllAvailableWithCount();

        if (books.isEmpty()) {
            throw new BusinessException(AVAILABLE_BOOK_NOT_FOUND);
        }
        return books;
    }

    // 模糊搜索
    public List<Book> fuzzySearchAvailableBook (String keyword) {

        if (keyword.isEmpty()) return findAllAvailableBook();

        return bookMapper.fuzzySearchAvailable(keyword);
    }

    public List<Book> fuzzySearchAvailableBookWithCount (String keyword) {

        if (keyword.isEmpty()) return findAllAvailableBookWithCount();

        return bookMapper.fuzzySearchAvailableWithCount(keyword);
    }

    public int borrowByBookId(Long id, String studentID, RecordSaveDTO recordSaveDTO) {

        Book book = bookMapper.findById(id).orElseThrow(() -> new BusinessException(AVAILABLE_BOOK_NOT_FOUND));

        // 已被借出的书不允许再借
        if (!book.isAvailable()) {
            throw new BusinessException(AVAILABLE_BOOK_NOT_FOUND);
        }

        // 不允许重复借同一本书
        List<Record> records = recordMapper
                .findUnReturnedByStudentIDAndISBN(studentID, book.getISBN());
        if (!records.isEmpty()) {
            throw new BusinessException(ALREADY_BORROWED_BOOK);
        }

        // 将状态改为不可借
        book.setAvailable(false);
        bookMapper.updateById(book);

        Record _record = new Record(
                null,
                book.getId(),
                recordSaveDTO.studentID(),
                book.getISBN(),
                null,
                null
        );

        return recordMapper.insertRecord(_record);
    }

    public int returnBookByRecordIDAndStudentID(Long id, String studentID) {

        Optional<Record> recordOptional = recordMapper.findById(id);
        if (recordOptional.isEmpty()) throw new BusinessException(UNEXISTED_RECORD);

        // 检查是不是本人的书 在查询命令中 程序应不显示其他人的借书记录
        Record record = recordOptional.get();
        if (!record.getStudentID().equals(studentID)) throw new BusinessException(UNEXISTED_RECORD);

        // 获取书籍数据
        Optional<Book> bookOptional = bookMapper.findById(record.getBookID());
        if (bookOptional.isEmpty()) throw new BusinessException(SQL_FOREIGN_KEY_ERROR);

        // 更新书籍状态
        Book book = bookOptional.get();
        book.setAvailable(true);
        bookMapper.updateById(book);

        // 更新历史记录归还时间
        recordMapper.updateReturnTimeById(id);

        return 0;
    }

    public List<Record> findAllRecordByStudentID(String studentID) {

        List<Record> records = recordMapper.findByStudentID(studentID);

        if (records.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }

        return records;
    }

    public List<Record> findUnReturnedRecordByStudentID(String studentID) {

        List<Record> records = recordMapper.findUnReturnedByStudentID(studentID);

        if (records.isEmpty()) {
            throw new BusinessException(NO_DATA);
        }

        return records;
    }
    // json
    public static void writeToJSON(Student student) {

        Map<String, String> json = new HashMap<>(Map.of(
                "userType", UserType.STUDENT.getString(),
                "id", student.getStudentID(),
                "name", student.getName()
        ));

        String jsonString = JSONObject.toJSONString(json, PrettyFormat);
        Path path = Paths.get("src", "main", "resources", "user.json");
        File file = path.toFile();
        try (FileWriter writer = new FileWriter(file, false)) {
            if (!Files.exists(path)) Files.createFile(path);
            writer.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
