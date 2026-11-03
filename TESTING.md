# 测试说明

## 运行方式

- IntelliJ IDEA：右键 `src/test/java` → `Run 'All Tests'`，或点击测试类/方法左侧的 ▶。
- 命令行：`mvn test`
- 只跑一个类：`mvn test -Dtest=StudentServiceTest`
- 只跑一个方法：`mvn test -Dtest=CommandTest#studentPermissions`

测试**不依赖本地 MySQL**：Mapper 集成测试使用 H2 内存数据库（`pom.xml` 中已有 test 作用域的 H2 依赖），
不需要先启动 MySQL 或导入 `init.sql`。

> `LoginServiceTest` 会读写 `src/main/resources/user.json`，已在每个用例前后备份/还原原文件。

## 测试结构（共 24 个测试类、264 个用例）

| 测试类 | 类型 | 用例数 | 覆盖内容 |
| --- | --- | ---: | --- |
| `AdminServiceTest` | 单元测试 | 59 | 学生/书籍查询、书籍汇总、书籍增删改、借阅记录查询 |
| `StudentServiceTest` | 单元测试 | 28 | 个人信息、可借书籍、借阅/归还、学生历史记录 |
| `BookMapperTest` | 集成测试 | 26 | 书籍 CRUD、可借筛选、模糊搜索、GROUP BY ISBN 汇总 |
| `StudentMapperTest` | 集成测试 | 14 | 学生 CRUD、列名映射、唯一约束 |
| `RecordMapperTest` | 集成测试 | 13 | 借阅记录 CRUD、未归还筛选、模糊搜索、归还时间 |
| `AdminMapperTest` | 集成测试 | 11 | 管理员 CRUD、登录查询、唯一约束 |
| `LoginServiceTest` | 单元测试 | 9 | 学生/管理员登录查询、user.json 会话读写与失效 |
| `CommandTest` | 单元测试 | 20 | 命令解析、四类用户权限矩阵、borrow/ret/history 参数 |
| `CommandDocumentationTest` | 单元测试 | 2 | 每个命令都有说明文件，且 all.txt 索引包含全部命令 |
| `StudentSaveDTOValidationTest` | 单元测试 | 16 | 学号/密码/姓名/手机号校验（密码 8~16 位） |
| `StudentUpdateDTOValidationTest` | 单元测试 | 9 | 更新 DTO 校验、`equals(Student)` |
| `BookSaveDTOValidationTest` | 单元测试 | 10 | ISBN/书名/作者/可借状态校验 |
| `BookUpdateDTOValidationTest` | 单元测试 | 7 | 更新 DTO 校验（仅书名/作者）、`equals(Book)` |
| `RecordSaveDTOValidationTest` | 单元测试 | 3 | 借阅 DTO 学号校验 |
| `ConsoleWriterTest` | 单元测试 | 8 | 居中排版、ANSI 宽度、标题/正文/列表输出 |
| `ResultTest` | 单元测试 | 8 | `Result` 工厂方法 |
| `BookTest` | 单元测试 | 4 | 可借状态文案、`toString` |
| `RecordTest` | 单元测试 | 2 | 借阅记录展示文案 |
| `UserTypeTest` | 单元测试 | 3 | 用户类型与字符串互转 |
| `numberUtilsTest` | 单元测试 | 3 | 正整数校验 |
| `TimeUtilsTest` | 单元测试 | 1 | 时段文案返回值 |
| `ValidationUtilsTest` | 单元测试 | 2 | 多条校验错误拼接 |
| `BusinessExceptionTest` | 单元测试 | 3 | 业务错误码与异常构造 |
| `LoginExceptionTest` | 单元测试 | 3 | 登录错误码与异常构造 |

## 测试基础设施

- `src/test/resources/mybatis-config-test.xml`：H2（MySQL 兼容模式）数据源，
  注册 `StudentMapper.xml`、`BookMapper.xml`、`AdminMapper.xml`、`RecordMapper.xml`。
- `src/test/resources/schema-h2.sql`：建 `student` / `admin` / `book` / `record` 表，
  每个集成测试用例前重建，保证互不影响。
- `src/test/resources/init.sql`：MySQL 初始化脚本，已与实体同步（`record.bookID`、`char(12)`）。
- `com.xietee.support.H2Database`：整套测试共用一个 `SqlSessionFactory`。
- `InMemoryStudentMapper` / `InMemoryBookMapper` / `InMemoryRecordMapper` / `InMemoryAdminMapper`：
  内存版 Mapper，供 Service 单元测试使用；读取返回副本，避免“假通过”。
  其中 `InMemoryBookMapper` 的汇总方法模拟 SQL 的 `GROUP BY ISBN` 语义。

## 本轮按业务调整同步的改动

1. `search` 的管理员分支改为调用 `fuzzySearchBook(keyword)` /
   `fuzzySearchBookWithCount(keyword)`，不再忽略关键词。
2. 管理员更新书籍时只能修改书名和作者；`BookUpdateDTO` 移除了 `available`，
   `AdminService.updateBookById` 不再写可借状态，改由借阅/归还流程维护。
3. `RecordMapper.findByBookID` 由 `Optional<Record>` 改为 `List<Record>`，
   同一本书的多条历史记录不会再触发 `TooManyResultsException`。
4. 登录密码长度统一为 8~16 位（与数据库 `varchar(16)` 一致），
   `StudentSaveDTO` / `StudentUpdateDTO` 的密码上限同步改为 16。

## 之前已修复的测试阻塞问题

1. `AdminServiceTest` 中的 `recordMapper` 从未初始化（一直是 null），已补上。
2. `RecordMapper.xml` 的 `fuzzySearch` SQL 非法（`where bookID, studentID like ...`），
   已改为 `bookID like ... OR studentID like ... OR ISBN like ...`。
3. `StudentService.borrowByBookId` 未校验书籍是否已被借出，已补上。
4. `init.sql` 的 `record` 表缺 `bookID`，且 `studentID` 还是 `char(11)`，已同步。
5. 测试用 H2 建表脚本补齐 `admin` / `record` 表，并注册 `AdminMapper.xml`、`RecordMapper.xml`。
6. `InMemoryRecordMapper` 从“全部返回空”的空壳补成可用的内存实现。

## 当前已知限制 / 建议

- `Cmd.execute(App)` 依赖 `AppUI`（JLine 终端），所以 `login` / `borrow` / `ret` / `history` /
  `add` / `update` / `del` 的交互流程没有单元测试；`CommandTest` 只覆盖纯解析与权限。
  若要补交互测试，建议把 `AppUI` 抽象成接口后注入。
- `help` 使用 `src/main/resources/...` 相对路径读取 `description/*.txt`，
  打成 jar 后会找不到；解决方案待研究。
- `user.json` 也是按 `src/main/resources` 相对路径读写的运行时状态文件，打成 jar 后不可写；
  建议移到用户目录（如 `~/.itaemAssessment/user.json`）或通过配置指定。
- `BookSaveDTO.available` 现在允许为 `null`，但 `AdminService.insertBook` 会直接拆箱传给
  `Book` 的 `boolean` 字段；如果调用方没有兜底，会抛 `NullPointerException`。
- 开发者（`DEVELOPER`）的权限目前整体未开放，所有命令都会返回 `COMMAND_ERROR`。
