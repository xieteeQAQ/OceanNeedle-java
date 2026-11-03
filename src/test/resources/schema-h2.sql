-- 测试用建表脚本（H2 的 MySQL 兼容模式）。
-- 结构与 MySQL 版的 init.sql 中的 student / admin / book / record 表保持一致，
-- 测试类在每个用例前重建，保证互不影响。

DROP TABLE IF EXISTS record;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS student;

CREATE TABLE student (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    studentID   CHAR(12)     NOT NULL,
    name        VARCHAR(10)  NOT NULL,
    password    VARCHAR(16)  NOT NULL,
    phoneNumber CHAR(11)     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_student_studentID UNIQUE (studentID),
    CONSTRAINT uk_student_phoneNumber UNIQUE (phoneNumber)
);

CREATE TABLE admin (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    adminID     CHAR(12)     NOT NULL,
    password    VARCHAR(16)  NOT NULL,
    phoneNumber CHAR(11)     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_admin_adminID UNIQUE (adminID),
    CONSTRAINT uk_admin_phoneNumber UNIQUE (phoneNumber)
);

CREATE TABLE book (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    ISBN      CHAR(13)     NOT NULL,
    title     VARCHAR(255) NOT NULL,
    author    VARCHAR(100) NOT NULL,
    available TINYINT(1)   DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE TABLE record (
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    bookID     BIGINT   NOT NULL,
    studentID  CHAR(12) NOT NULL,
    ISBN       CHAR(13) NOT NULL,
    borrowTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    returnTime DATETIME DEFAULT NULL,
    PRIMARY KEY (id)
);
