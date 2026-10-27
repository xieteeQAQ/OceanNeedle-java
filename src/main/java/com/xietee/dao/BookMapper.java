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

    // 有没有借出的都可以查
    List<Book> findAllWithCount();

    List<Book> findWithCountByISBN(String ISBN);

    List<Book> findWithCountByTitle(@Param("title") String title);

    List<Book> findWithCountByAuthor(@Param("author") String author);

    List<Book> findWithCountByAvailable(boolean available);

    List<Book> fuzzySearchWithCount(@Param("keyword") String keyword);

    // 只查可以借的
    List<Book> findAllAvailableWithCount();

    List<Book> findAvailableWithCountByISBN(String ISBN);

    List<Book> findAvailableWithCountByTitle(@Param("title") String title);

    List<Book> findAvailableWithCountByAuthor(@Param("author") String author);

    // 插入
    int insertBook(Book book);

    // 更新
    int updateById(Book book);

    // 删除
    int deleteById(Long id);
}
