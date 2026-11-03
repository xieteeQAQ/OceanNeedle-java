package com.xietee.support;

import com.xietee.dao.BookMapper;
import com.xietee.entity.Book;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 内存版 BookMapper，用于 Service 的纯单元测试（不启动数据库）。
 *
 * <p>计数类方法模拟 BookMapper.xml 的 GROUP BY ISBN 语义：
 * 每个 ISBN 返回一条汇总记录，totalCount 为该 ISBN 的副本总数。</p>
 */
public class InMemoryBookMapper implements BookMapper {

    private final Map<Long, Book> rows = new LinkedHashMap<>();
    private long sequence = 0;

    /** 预置一本书，返回保存后的副本（id 已赋值）。 */
    public Book seed(String ISBN, String title, String author, boolean available) {
        Book book = new Book(++sequence, ISBN, title, author, available, null, null);
        rows.put(book.getId(), book);
        return copy(book);
    }

    /** 当前行数，方便测试断言删除是否生效。 */
    public int size() {
        return rows.size();
    }

    @Override
    public List<Book> findAll() {
        return rows.values().stream().map(InMemoryBookMapper::copy).toList();
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(rows.get(id)).map(InMemoryBookMapper::copy);
    }

    @Override
    public List<Book> findByISBN(String ISBN) {
        return find(book -> book.getISBN().equals(ISBN));
    }

    @Override
    public List<Book> findByTitle(String title) {
        return find(book -> book.getTitle().equals(title));
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return find(book -> book.getAuthor().equals(author));
    }

    @Override
    public List<Book> findByAvailable(boolean available) {
        return find(book -> book.isAvailable() == available);
    }

    @Override
    public List<Book> fuzzySearch(String keyword) {
        return find(book -> matches(book, keyword));
    }

    @Override
    public List<Book> findAllAvailable() {
        return find(Book::isAvailable);
    }

    @Override
    public List<Book> fuzzySearchAvailable(String keyword) {
        return find(book -> book.isAvailable() && matches(book, keyword));
    }

    @Override
    public List<Book> findAllWithCount() {
        return summarize(rows.values());
    }

    @Override
    public List<Book> findWithCountByISBN(String ISBN) {
        return summarize(findByISBN(ISBN));
    }

    @Override
    public List<Book> findWithCountByTitle(String title) {
        return summarize(findByTitle(title));
    }

    @Override
    public List<Book> findWithCountByAuthor(String author) {
        return summarize(findByAuthor(author));
    }

    @Override
    public List<Book> findWithCountByAvailable(boolean available) {
        return summarize(findByAvailable(available));
    }

    @Override
    public List<Book> fuzzySearchWithCount(String keyword) {
        return summarize(fuzzySearch(keyword));
    }

    @Override
    public List<Book> findAllAvailableWithCount() {
        return summarizeAvailable(rows.values());
    }

    @Override
    public List<Book> findAvailableWithCountByISBN(String ISBN) {
        return summarizeAvailable(findByISBN(ISBN));
    }

    @Override
    public List<Book> findAvailableWithCountByTitle(String title) {
        return summarizeAvailable(findByTitle(title));
    }

    @Override
    public List<Book> findAvailableWithCountByAuthor(String author) {
        return summarizeAvailable(findByAuthor(author));
    }

    @Override
    public List<Book> fuzzySearchAvailableWithCount(String keyword) {
        return summarizeAvailable(fuzzySearch(keyword));
    }

    @Override
    public int insertBook(Book book) {
        Book saved = copy(book);
        saved.setId(++sequence);
        rows.put(saved.getId(), saved);
        return 1;
    }

    @Override
    public int updateById(Book book) {
        Book existing = book.getId() == null ? null : rows.get(book.getId());
        if (existing == null) {
            return 0;
        }
        // 与 BookMapper.xml 的 updateById 一致：只更新 title / author / available
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setAvailable(book.isAvailable());
        return 1;
    }

    @Override
    public int deleteById(Long id) {
        return rows.remove(id) == null ? 0 : 1;
    }

    private List<Book> find(Predicate<Book> condition) {
        return rows.values().stream()
                .filter(condition)
                .map(InMemoryBookMapper::copy)
                .toList();
    }

    private static boolean matches(Book book, String keyword) {
        return book.getISBN().contains(keyword)
                || book.getTitle().contains(keyword)
                || book.getAuthor().contains(keyword);
    }

    /** 按 ISBN 汇总，返回每个 ISBN 一条记录。 */
    private List<Book> summarize(Iterable<Book> books) {
        Map<String, List<Book>> groups = new LinkedHashMap<>();
        for (Book book : books) {
            groups.computeIfAbsent(book.getISBN(), key -> new ArrayList<>()).add(book);
        }
        return groups.values().stream().map(InMemoryBookMapper::groupToBook).toList();
    }

    /** 按 ISBN 汇总，只保留至少有一本可借的组（对应 SQL 的 HAVING availableCount > 0）。 */
    private List<Book> summarizeAvailable(Iterable<Book> books) {
        return summarize(books).stream()
                .filter(book -> book.getAvailableCount() > 0)
                .toList();
    }

    private static Book groupToBook(List<Book> group) {
        Book first = group.getFirst();
        int total = group.size();
        int available = (int) group.stream().filter(Book::isAvailable).count();
        return new Book(
                null,
                first.getISBN(),
                first.getTitle(),
                first.getAuthor(),
                available > 0, // 对应 SQL 的 MAX(available)
                available,
                total);
    }

    private static Book copy(Book book) {
        return new Book(
                book.getId(),
                book.getISBN(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable(),
                book.getAvailableCount(),
                book.getTotalCount());
    }
}
