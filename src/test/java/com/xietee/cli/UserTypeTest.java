package com.xietee.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * UserType 的字符串映射测试。
 */
class UserTypeTest {

    @Test
    @DisplayName("getString：返回写入 user.json 的字符串")
    void getString() {
        assertEquals("STUDENT", UserType.STUDENT.getString());
        assertEquals("ADMIN", UserType.ADMIN.getString());
        assertEquals("DEVELOPER", UserType.DEVELOPER.getString());
        assertEquals("NULL", UserType.NULL.getString());
    }

    @Test
    @DisplayName("map：识别四类用户")
    void mapKnownTypes() {
        assertEquals(UserType.STUDENT, UserType.map("STUDENT"));
        assertEquals(UserType.ADMIN, UserType.map("ADMIN"));
        assertEquals(UserType.DEVELOPER, UserType.map("DEVELOPER"));
        assertEquals(UserType.NULL, UserType.map("NULL"));
    }

    @Test
    @DisplayName("map：未知值和小写值都映射为 NULL")
    void mapUnknownTypesToNull() {
        assertEquals(UserType.NULL, UserType.map("student"));
        assertEquals(UserType.NULL, UserType.map(""));
        assertEquals(UserType.NULL, UserType.map("UNKNOWN"));
    }
}
