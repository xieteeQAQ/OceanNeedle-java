package com.xietee.support;

import com.xietee.dao.RecordMapper;
import com.xietee.entity.Record;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 内存版 RecordMapper，用于 StudentService / AdminService 的纯单元测试。
 *
 * <p>语义与 RecordMapper.xml 对齐：插入时 borrowTime 取当前时间，
 * 归还时把 returnTime 置为当前时间。</p>
 */
public class InMemoryRecordMapper implements RecordMapper {

    private final Map<Long, Record> rows = new LinkedHashMap<>();
    private long sequence = 0;

    /** 预置一条借阅记录，返回保存后的副本（id 已赋值）。 */
    public Record seed(Long bookID, String studentID, String ISBN,
                       LocalDateTime borrowTime, LocalDateTime returnTime) {
        Record record = new Record(++sequence, bookID, studentID, ISBN, borrowTime, returnTime);
        rows.put(record.getId(), record);
        return copy(record);
    }

    /** 预置一条“未归还”记录。 */
    public Record seedBorrowed(Long bookID, String studentID, String ISBN) {
        return seed(bookID, studentID, ISBN, LocalDateTime.now().minusDays(1), null);
    }

    public int size() {
        return rows.size();
    }

    @Override
    public List<Record> findAll() {
        return rows.values().stream().map(InMemoryRecordMapper::copy).toList();
    }

    @Override
    public Optional<Record> findById(Long id) {
        return Optional.ofNullable(rows.get(id)).map(InMemoryRecordMapper::copy);
    }

    @Override
    public List<Record> findByBookID(Long id) {
        return find(record -> record.getBookID().equals(id));
    }

    @Override
    public List<Record> findByStudentID(String studentID) {
        return find(record -> record.getStudentID().equals(studentID));
    }

    @Override
    public List<Record> findByISBN(String ISBN) {
        return find(record -> record.getISBN().equals(ISBN));
    }

    @Override
    public List<Record> findUnReturned() {
        return find(record -> record.getReturnTime() == null);
    }

    @Override
    public List<Record> findUnReturnedByStudentID(String studentID) {
        return find(record -> record.getReturnTime() == null
                && record.getStudentID().equals(studentID));
    }

    @Override
    public List<Record> findUnReturnedByStudentIDAndISBN(String studentID, String ISBN) {
        return find(record -> record.getReturnTime() == null
                && record.getStudentID().equals(studentID)
                && record.getISBN().equals(ISBN));
    }

    @Override
    public List<Record> fuzzySearch(String keyword) {
        return find(record -> String.valueOf(record.getBookID()).contains(keyword)
                || record.getStudentID().contains(keyword)
                || record.getISBN().contains(keyword));
    }

    @Override
    public int insertRecord(Record record) {
        Record saved = copy(record);
        saved.setId(++sequence);
        if (saved.getBorrowTime() == null) {
            saved.setBorrowTime(LocalDateTime.now());
        }
        saved.setReturnTime(null);
        rows.put(saved.getId(), saved);
        return 1;
    }

    @Override
    public int updateReturnTimeById(Long id) {
        Record record = rows.get(id);
        if (record == null) {
            return 0;
        }
        record.setReturnTime(LocalDateTime.now());
        return 1;
    }

    @Override
    public int deleteById(Long id) {
        return rows.remove(id) == null ? 0 : 1;
    }

    private List<Record> find(java.util.function.Predicate<Record> condition) {
        return rows.values().stream()
                .filter(condition)
                .map(InMemoryRecordMapper::copy)
                .toList();
    }

    private static Record copy(Record record) {
        return new Record(
                record.getId(),
                record.getBookID(),
                record.getStudentID(),
                record.getISBN(),
                record.getBorrowTime(),
                record.getReturnTime());
    }
}
