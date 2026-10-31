package com.xietee.dao;

import com.xietee.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecordMapper {

    // 查询
    List<Record> findAll();

    Optional<Record> findById(Long id);

    List<Record> findByBookID(Long id);

    List<Record> findByStudentID(String studentID);

    List<Record> findByISBN(String ISBN);

    List<Record> findUnReturned();

    List<Record> findUnReturnedByStudentID(String studentID);

    List<Record> findUnReturnedByStudentIDAndISBN(@Param("studentID") String studentID, @Param("ISBN") String ISBN);

    List<Record> fuzzySearch(@Param("keyword") String keyword);

    // 插入
    int insertRecord(Record record);

    // 更新
    int updateReturnTimeById(Long id);

    // 删除
    int deleteById(Long id);
}
