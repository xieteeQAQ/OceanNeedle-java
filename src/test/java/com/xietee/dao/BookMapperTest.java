package com.xietee.dao;

import com.xietee.entity.Book;
import com.xietee.support.H2Database;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BookMapper 的集成测试：真实执行 BookMapper.xml 里的 SQL，
 * 数据库使用 H2 内存库（MySQL 兼容模式），不需要本地 MySQL。
 */
class BookMapperTest {

    private static final String ISBN_JAVA = "9787111000001";
    private static final String ISBN_MYSQL = "9787121000002";

    private SqlSession session;
    private BookMapper mapper;

    @BeforeEach
    void setUp() {
        H2Database.resetSchema();
        session = H2Database.factory().openSession(true);
        mapper = session.getMapper(BookMapper.class);
    }

    @AfterEach
    void tearDown() {
        if (session != null) {
            session.close();
        }
    }

    /** 插入一本书并返回数据库中的完整记录（含自增 id）。 */
    private Book insert(String ISBN, String title, String author, boolean available) {
        int rows = mapper.insertBook(new Book(null, ISBN, title, author, available, null, null));
        assertEquals(1, rows);
        return mapper.findAll().stream()
                .max(Comparator.comparing(Book::getId))
                .orElseThrow();
    }

    /** 三本 Java（两本可借、一本借出）+ 一本可借的 MySQL。 */
    private Book seedCopies() {
        insert(ISBN_JAVA, "Java编程思想", "张三", true);
        insert(ISBN_JAVA, "Java编程思想", "张三", true);
        insert(ISBN_JAVA, "Java编程思想", "张三", false);
        Book mysql = insert(ISBN_MYSQL, "MySQL必知必会", "李四", true);
        return mysql;
    }

    // ---------------------------------------------------------------- 查询

    @Test
    @DisplayName("findAll：空表返回空列表")
    void findAllReturnsEmptyList() {
        assertTrue(mapper.findAll().isEmpty());
    }

    @Test
    @DisplayName("findById：字段完整映射（含 ISBN / title / available）")
    void findByIdMapsAllColumns() {
        Book saved = insert(ISBN_JAVA, "Java编程思想", "张三", true);

        Book found = mapper.findById(saved.getId()).orElseThrow();

        assertEquals(ISBN_JAVA, found.getISBN());
        assertEquals("Java编程思想", found.getTitle());
        assertEquals("张三", found.getAuthor());
        assertTrue(found.isAvailable());
    }

    @Test
    @DisplayName("findById：不存在的 id 返回空 Optional")
    void findByIdReturnsEmptyWhenMissing() {
        assertTrue(mapper.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("findAll：返回全部书籍")
    void findAllReturnsAllRows() {
        seedCopies();

        List<Book> books = mapper.findAll();

        assertEquals(4, books.size());
        assertEquals(
                Set.of(ISBN_JAVA, ISBN_MYSQL),
                books.stream().map(Book::getISBN).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("findByISBN：同一 ISBN 的多本副本都会返回")
    void findByISBNReturnsAllCopies() {
        seedCopies();

        List<Book> books = mapper.findByISBN(ISBN_JAVA);

        assertEquals(3, books.size());
        assertTrue(books.stream().allMatch(book -> book.getISBN().equals(ISBN_JAVA)));
    }

    @Test
    @DisplayName("findByISBN：不存在的 ISBN 返回空列表")
    void findByISBNReturnsEmptyWhenMissing() {
        seedCopies();

        assertTrue(mapper.findByISBN("9787999999999").isEmpty());
    }

    @Test
    @DisplayName("findByTitle：返回同名书籍，且是精确匹配")
    void findByTitleReturnsExactMatches() {
        seedCopies();

        assertEquals(3, mapper.findByTitle("Java编程思想").size());
        assertTrue(mapper.findByTitle("Java").isEmpty(), "findByTitle 目前是精确匹配");
    }

    @Test
    @DisplayName("findByAuthor：返回同一作者的全部书籍")
    void findByAuthorReturnsMatches() {
        seedCopies();

        List<Book> books = mapper.findByAuthor("张三");

        assertEquals(3, books.size());
        assertTrue(books.stream().allMatch(book -> book.getAuthor().equals("张三")));
    }

    @Test
    @DisplayName("findByAvailable：按可借状态筛选")
    void findByAvailableFiltersByStatus() {
        seedCopies();

        assertEquals(3, mapper.findByAvailable(true).size());
        assertEquals(1, mapper.findByAvailable(false).size());
        assertFalse(mapper.findByAvailable(false).getFirst().isAvailable());
    }

    // ---------------------------------------------------------------- 更新

    @Test
    @DisplayName("updateById：更新书名、作者和可借状态，不改 ISBN")
    void updateByIdChangesEditableColumns() {
        Book saved = insert(ISBN_JAVA, "Java编程思想", "张三", true);
        saved.setTitle("Effective Java");
        saved.setAuthor("李四");
        saved.setAvailable(false);

        assertEquals(1, mapper.updateById(saved));

        Book updated = mapper.findById(saved.getId()).orElseThrow();
        assertEquals("Effective Java", updated.getTitle());
        assertEquals("李四", updated.getAuthor());
        assertFalse(updated.isAvailable());
        assertEquals(ISBN_JAVA, updated.getISBN());
    }

    @Test
    @DisplayName("updateById：id 不存在时返回 0")
    void updateByIdReturnsZeroWhenMissing() {
        Book ghost = new Book(999L, ISBN_JAVA, "Java编程思想", "张三", true, null, null);

        assertEquals(0, mapper.updateById(ghost));
    }

    // ---------------------------------------------------------------- 删除

    @Test
    @DisplayName("deleteById：删除成功返回 1，再次删除返回 0")
    void deleteByIdRemovesRow() {
        Book saved = insert(ISBN_JAVA, "Java编程思想", "张三", true);

        assertEquals(1, mapper.deleteById(saved.getId()));
        assertTrue(mapper.findById(saved.getId()).isEmpty());
        assertEquals(0, mapper.deleteById(saved.getId()));
    }

    // ------------------------------------------------------------ 统计类查询
    @Test
    @DisplayName("findWithCountByISBN：命中 3 本，其中 2 本可借")
    void findWithCountByISBNCountsCopies() {
        seedCopies();

        List<Book> books = mapper.findWithCountByISBN(ISBN_JAVA);

        assertEquals(1, books.size());
        for (Book book : books) {
            assertEquals(3, book.getTotalCount());
            assertEquals(2, book.getAvailableCount());
            assertEquals("Java编程思想", book.getTitle(), "统计查询也必须返回 title");
        }
    }

    @Test
    @DisplayName("findWithCountByTitle：按书名统计副本数")
    void findWithCountByTitleCountsCopies() {
        seedCopies();

        List<Book> books = mapper.findWithCountByTitle("Java编程思想");

        assertEquals(1, books.size());
        assertEquals(3, books.getFirst().getTotalCount());
        assertEquals(2, books.getFirst().getAvailableCount());
    }

    @Test
    @DisplayName("findWithCountByAuthor：按作者统计副本数")
    void findWithCountByAuthorCountsCopies() {
        seedCopies();

        List<Book> books = mapper.findWithCountByAuthor("张三");

        assertEquals(1, books.size());
        assertEquals(3, books.getFirst().getTotalCount());
        assertEquals(2, books.getFirst().getAvailableCount());
    }

    @Test
    @DisplayName("findWithCountByAvailable：可借 / 已借出分别统计")
    void findWithCountByAvailableCountsCopies() {
        seedCopies();

        List<Book> available = mapper.findWithCountByAvailable(true);
        List<Book> borrowed = mapper.findWithCountByAvailable(false);

        assertEquals(2, available.size());

        assertEquals(1, borrowed.size());
        assertEquals(1, borrowed.getFirst().getTotalCount());
        assertEquals(0, borrowed.getFirst().getAvailableCount());
    }

    @Test
    @DisplayName("findAvailableWithCountByISBN：只返回可借副本")
    void findAvailableWithCountByISBNOnlyReturnsAvailableCopies() {
        seedCopies();

        List<Book> books = mapper.findAvailableWithCountByISBN(ISBN_JAVA);

        // 当前 SQL 的统计范围是“过滤后的行”，因此 totalCount 等于可借副本数
        assertEquals(1, books.size());
        assertTrue(books.stream().allMatch(book -> book.isAvailable()
                && book.getTotalCount() == 3
                && book.getAvailableCount() == 2));
    }

    @Test
    @DisplayName("findAvailableWithCountByTitle / Author：只返回可借副本")
    void findAvailableWithCountByTitleAndAuthor() {
        seedCopies();

        List<Book> byTitle = mapper.findAvailableWithCountByTitle("Java编程思想");
        assertEquals(1, byTitle.size());
        assertTrue(byTitle.stream().allMatch(book -> book.isAvailable()
                && book.getTotalCount() == 3
                && book.getAvailableCount() == 2));

        assertEquals(1, mapper.findAvailableWithCountByAuthor("张三").size());
    }

    // ------------------------------------------------------------ 模糊搜索

    @Test
    @DisplayName("fuzzySearch：按 ISBN / 书名 / 作者模糊匹配")
    void fuzzySearchMatchesAllFields() {
        seedCopies();

        assertEquals(3, mapper.fuzzySearch("Java").size());
        assertEquals(3, mapper.fuzzySearch("7111").size());
        assertEquals(1, mapper.fuzzySearch("李四").size());
        assertTrue(mapper.fuzzySearch("不存在").isEmpty());
    }

    @Test
    @DisplayName("findAllAvailable：只返回可借副本")
    void findAllAvailableReturnsAvailableCopies() {
        seedCopies();

        List<Book> books = mapper.findAllAvailable();

        assertEquals(3, books.size());
        assertTrue(books.stream().allMatch(Book::isAvailable));
    }

    @Test
    @DisplayName("fuzzySearchAvailable：模糊匹配且只返回可借副本")
    void fuzzySearchAvailableFiltersAvailable() {
        seedCopies();

        List<Book> books = mapper.fuzzySearchAvailable("Java");

        assertEquals(2, books.size());
        assertTrue(books.stream().allMatch(Book::isAvailable));
    }

    // ------------------------------------------------------------ 汇总查询

    @Test
    @DisplayName("findAllWithCount：按 ISBN 汇总全部副本")
    void findAllWithCountGroupsByISBN() {
        seedCopies();

        List<Book> books = mapper.findAllWithCount();

        assertEquals(2, books.size());
        Book java = books.stream()
                .filter(book -> book.getISBN().equals(ISBN_JAVA))
                .findFirst().orElseThrow();
        assertEquals(3, java.getTotalCount());
        assertEquals(2, java.getAvailableCount());
        assertEquals("Java编程思想", java.getTitle());
        assertEquals("张三", java.getAuthor());
    }

    @Test
    @DisplayName("findAllWithCount：空表返回空列表")
    void findAllWithCountReturnsEmptyList() {
        assertTrue(mapper.findAllWithCount().isEmpty());
    }

    @Test
    @DisplayName("fuzzySearchWithCount：按关键词汇总匹配的 ISBN")
    void fuzzySearchWithCountGroupsByISBN() {
        seedCopies();

        List<Book> books = mapper.fuzzySearchWithCount("Java");

        assertEquals(1, books.size());
        assertEquals(3, books.getFirst().getTotalCount());
        assertEquals(2, books.getFirst().getAvailableCount());
    }

    @Test
    @DisplayName("findAllAvailableWithCount：只返回有可借副本的 ISBN")
    void findAllAvailableWithCountFiltersGroups() {
        seedCopies();
        insert("9787131000003", "已全部借出", "王五", false);

        List<Book> books = mapper.findAllAvailableWithCount();

        assertEquals(2, books.size());
        assertTrue(books.stream().noneMatch(book -> book.getISBN().equals("9787131000003")));
    }

    @Test
    @DisplayName("fuzzySearchAvailableWithCount：汇总时保留该 ISBN 的总副本数")
    void fuzzySearchAvailableWithCountKeepsTotalCount() {
        seedCopies();

        List<Book> books = mapper.fuzzySearchAvailableWithCount("Java");

        assertEquals(1, books.size());
        assertEquals(3, books.getFirst().getTotalCount());
        assertEquals(2, books.getFirst().getAvailableCount());
    }
}
