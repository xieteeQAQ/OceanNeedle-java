package com.xietee.dao;

import com.xietee.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookMapper {

    // 个体查询
    List<Book> findAll();

    Optional<Book> findById(long id);

    List<Book> findByISBN(String ISBN);

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByAvailable(boolean available);

    // 有没有借出的都可以查
    List<Book> findWithCountById(long id);

    List<Book> findWithCountByISBN(String ISBN);

    List<Book> findWithCountByTitle(String title);

    List<Book> findWithCountByAuthor(String author);

    List<Book> findWithCountByAvailable(boolean available);

    // 只查可以借的
    List<Book> findAvailableWithCountById(long id);

    List<Book> findAvailableWithCountByISBN(String ISBN);

    List<Book> findAvailableWithCountByTitle(String title);

    List<Book> findAvailableWithCountByAuthor(String author);

    // 插入
    int insertBook(Book book);

    // 更新
    int updateById(Book book);

    // 删除
    int deleteById(Long id);
}
