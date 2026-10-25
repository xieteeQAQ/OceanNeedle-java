package com.xietee.dao;

import com.xietee.entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookMapper {

    // 查询
    List<Book> findAll();

    Optional<Book> findById(long id);

    List<Book> findByISBN(String ISBN);

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByAvailable(boolean available);

    // 插入
    int insertBook(Book book);

    // 更新
    int updateById(Book book);

    // 删除
    int deleteById(Long id);
}
