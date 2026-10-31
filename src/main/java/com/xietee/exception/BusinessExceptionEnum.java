package com.xietee.exception;

// 具体业务错误码枚举实现
public enum BusinessExceptionEnum implements StatusCode {

    // 公共状态码
    NONE(200, ""),
    SUCCESS(200, "操作成功"),
    DATA_UNCHANGED(200, "没有数据更新"),
    SYSTEM_ERROR(500, "系统异常"),
    PARAM_ERROR(400, "参数校验失败"),

    // 对象模块错误码
    NO_DATA(2000, "还没有数据"),
    STUDENT_NOT_FOUND(2001, "未查找到学生"),
    STUDENT_ID_EXISTED(2002, "所填学号已存在"),
    STUDENT_CONTRACT_NUMBER_EXISTED(2003, "所填电话号码已存在"),
    BOOK_NOT_FOUND(2004, "未查找到书籍"),
    AVAILABLE_BOOK_NOT_FOUND(2005, "未查找到可借书籍"),
    ALREADY_BORROWED_BOOK(2006, "不可重复借阅同一本书籍"),
    BOOK_UNRETURNED(2007, "该书籍还未归还"),
    UNEXISTED_RECORD(2008, "该记录不存在"),

    // 非法参数错误码
    COMMAND_ERROR(4001, "无效命令"),
    COMMAND_PARAM_ERROR(4002, "无效参数"),
    COMMAND_NEED_PARAM(4003, "该命令需要参数"),
    OUT_OF_RANGE(4004, "应输入在范围之内的序号"),
    INVALID_NUMBER(4005, "应输入正整数"),
    COMMAND_UNEXISTED(4006, "要查询的命令不存在"),

    // 系统错误码
    FEATURE_NOT_DEVELOPED(5000, "功能待开发"),
    UPDATE_FAIL(5003, "更新失败"),
    NO_SUCH_FILE(5004, "文件不存在"),
    SQL_FOREIGN_KEY_ERROR(5005, "外键错误"),

    // 登录错误码
    INCORRECT_PASSWORD(6000, "密码错误"),
    UNEXISTED_STUDENT_ID(6001, "该学号不存在"),
    UNEXISTED_ADMIN_ID(6002, "该ID不存在"),
    LOGIN_SESSION_EXPIRED(6003, "登录失效，需重新登录");

    private final int code;
    private final String message;

    BusinessExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
