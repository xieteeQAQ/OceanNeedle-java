create database if not exists libraryDB collate utf8mb4_unicode_ci;

use libraryDB;

create table if not exists student (

    id bigint not null auto_increment,
    studentID char(12) not null,
    name varchar(10) not null,
    password varchar(16) not null,
    phoneNumber char(11) not null,
    primary key (id),
    unique (studentID),
    unique (phoneNumber)
);

create table if not exists admin (

    id bigint not null auto_increment,
    adminID char(12) not null,
    password varchar(16) not null,
    phoneNumber char(11) not null,
    primary key (id),
    unique (adminID),
    unique (phoneNumber)
);

create table if not exists book (

    id bigint not null auto_increment,
    ISBN char(13) not null,
    title varchar(255) not null,
    author varchar(100) not null,
    available tinyint(1) default 1,
    primary key (id)
);

create table if not exists record (

    id bigint not null auto_increment,
    bookID bigint not null,
    studentID char(12) not null,
    ISBN char(13) not null,
    borrowTime DATETIME default now(),
    returnTime DATETIME default null,
    primary key (id)
)
