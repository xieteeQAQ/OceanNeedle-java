package com.xietee.dao;

import com.xietee.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<Book> fuzzySearch(@Param("keyword") String keyword);

    // 个体可借查询

    List<Book> findAllAvailable();

    List<Book> fuzzySearchAvailable(@Param("keyword") String keyword);

    // 有没有借出的都可以查
    List<Book> findAllWithCount();

    List<Book> findWithCountByISBN(String ISBN);

    List<Book> findWithCountByTitle(String title);

    List<Book> findWithCountByAuthor(String author);

    List<Book> findWithCountByAvailable(boolean available);

    List<Book> fuzzySearchWithCount(@Param("keyword") String keyword);

    // 只查可以借的
    List<Book> findAllAvailableWithCount();

    List<Book> findAvailableWithCountByISBN(String ISBN);

    List<Book> findAvailableWithCountByTitle(String title);

    List<Book> findAvailableWithCountByAuthor(String author);

    List<Book> fuzzySearchAvailableWithCount(String keyword);

    // 插入
    int insertBook(Book book);

    // 更新
    int updateById(Book book);

    // 删除
    int deleteById(Long id);
}
