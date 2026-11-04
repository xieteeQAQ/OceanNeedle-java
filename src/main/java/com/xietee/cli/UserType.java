package com.xietee.cli;

public enum UserType {
    STUDENT("STUDENT"),
    ADMIN("ADMIN"),
    NULL("NULL");

    private final String userType;

    UserType(String userType) {
        this.userType = userType;
    }

    public String getString() {
        return userType;
    }

    public static UserType map(String userType) {
        switch (userType) {
            case "STUDENT" -> {
                return STUDENT;
            }
            case "ADMIN" -> {
                return ADMIN;
            }
            default -> {
                return NULL;
            }
        }
    }
}
