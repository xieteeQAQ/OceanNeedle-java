package com.xietee.cli;

import com.xietee.service.AdminService;
import com.xietee.support.InMemoryStudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.xietee.exception.BusinessExceptionEnum.COMMAND_UNEXISTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private InMemoryStudentMapper mapper;
    private AdminService service;
    private App app;
    private Command command;

    @BeforeEach
    void setUp() throws IOException {
        mapper = new InMemoryStudentMapper();
        service = new AdminService(mapper);
        app = new App(service);
        command = new Command(app);
    }

    @Test
    @DisplayName("help输出帮助信息")
    public void helpExecuteTest() {
        String line = "help help";
        assertEquals(200, command.parseAndGetCommand(line).data().execute().code());
    }

    @Test
    @DisplayName("help解析命令错误")
    public void helpParseTest() {
        String line = "help unexisted";
        assertEquals(COMMAND_UNEXISTED.getCode(), command.parseAndGetCommand(line).code());
    }
}
