package com.xietee.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xietee.cli.UserType;
import com.xietee.dao.AdminMapper;
import com.xietee.dao.StudentMapper;
import com.xietee.entity.Admin;
import com.xietee.entity.Student;
import com.xietee.exception.LoginException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat;
import static com.xietee.exception.BusinessExceptionEnum.LOGIN_SESSION_EXPIRED;

public class LoginService {

    private final StudentMapper studentMapper;
    private final AdminMapper adminMapper;

    public LoginService(StudentMapper studentMapper, AdminMapper adminMapper) {
        this.studentMapper = studentMapper;
        this.adminMapper = adminMapper;
    }

    public Optional<Student> LoginFindStudentByStudentID(String studentID) {
        return studentMapper.findByStudentID(studentID);
    }

    public Optional<Admin> LoginFindAdminByAdminID(String adminID) {
        return adminMapper.findByAdminID(adminID);
    }

    // json
    public static JSONObject getUserFromJSON() {
        try {
            // 1. 读取文件全部内容为字符串（Java 11+）
            Path path = Paths.get("src", "main", "resources", "user.json");
            String content = Files.readString(path, StandardCharsets.UTF_8);

            checkLoginSessionStatus();

            // 2. 解析为 JSONObject
            return (JSON.parseObject(content));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkLoginSessionStatus() {

        try {
            Path path = Paths.get("src", "main", "resources", "user.json");
            String content = Files.readString(path, StandardCharsets.UTF_8);

            if (content.isBlank() ||
                    !(content.contains("name")
                            && content.contains("userType")
                            && content.contains("id"))) {
                Map<String, String> json = new HashMap<>(Map.of(
                        "userType", UserType.NULL.getString(),
                        "id", "",
                        "name", ""
                ));

                String jsonString = JSONObject.toJSONString(json, PrettyFormat);
                File file = path.toFile();
                try (FileWriter writer = new FileWriter(file, false)) {
                    if (!Files.exists(path)) Files.createFile(path);
                    writer.write(jsonString);
                }
                throw new LoginException(LOGIN_SESSION_EXPIRED);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
