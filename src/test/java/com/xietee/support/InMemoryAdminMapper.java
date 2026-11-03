package com.xietee.support;

import com.xietee.dao.AdminMapper;
import com.xietee.entity.Admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 内存版 AdminMapper，用于 LoginService 的纯单元测试。
 */
public class InMemoryAdminMapper implements AdminMapper {

    private final Map<Long, Admin> rows = new LinkedHashMap<>();
    private long sequence = 0;

    public Admin seed(String adminID, String password, String phoneNumber) {
        Admin admin = new Admin(++sequence, adminID, password, phoneNumber);
        rows.put(admin.getId(), admin);
        return copy(admin);
    }

    public int size() {
        return rows.size();
    }

    @Override
    public List<Admin> findAll() {
        return rows.values().stream().map(InMemoryAdminMapper::copy).toList();
    }

    @Override
    public Optional<Admin> findById(long id) {
        return Optional.ofNullable(rows.get(id)).map(InMemoryAdminMapper::copy);
    }

    @Override
    public Optional<Admin> findByAdminID(String adminID) {
        return rows.values().stream()
                .filter(admin -> admin.getAdminID().equals(adminID))
                .findFirst()
                .map(InMemoryAdminMapper::copy);
    }

    @Override
    public Optional<Admin> findByPhoneNumber(String phoneNumber) {
        return rows.values().stream()
                .filter(admin -> admin.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .map(InMemoryAdminMapper::copy);
    }

    @Override
    public int insertAdmin(Admin admin) {
        Admin saved = copy(admin);
        saved.setId(++sequence);
        rows.put(saved.getId(), saved);
        return 1;
    }

    @Override
    public int updateById(Admin admin) {
        if (admin.getId() == null || !rows.containsKey(admin.getId())) {
            return 0;
        }
        rows.put(admin.getId(), copy(admin));
        return 1;
    }

    @Override
    public int deleteById(Long id) {
        return rows.remove(id) == null ? 0 : 1;
    }

    private static Admin copy(Admin admin) {
        return new Admin(
                admin.getId(),
                admin.getAdminID(),
                admin.getPassword(),
                admin.getPhoneNumber());
    }
}
