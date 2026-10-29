package com.xietee.dao;

import com.xietee.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminMapper {

    // 查询
    List<Admin> findAll();

    Optional<Admin> findById(long id);

    Optional<Admin> findByAdminID(String adminID);

    Optional<Admin> findByPhoneNumber(String phoneNumber);

    // 插入
    int insertAdmin(Admin admin);

    // 更新
    int updateById(Admin admin);

    // 删除
    int deleteById(Long id);
}
