package com.xietee.cli;

import com.xietee.cli.cmd.Cmd;
import com.xietee.cli.cmd.add;
import com.xietee.cli.cmd.borrow;
import com.xietee.cli.cmd.clear;
import com.xietee.cli.cmd.del;
import com.xietee.cli.cmd.help;
import com.xietee.cli.cmd.history;
import com.xietee.cli.cmd.login;
import com.xietee.cli.cmd.ls;
import com.xietee.cli.cmd.quit;
import com.xietee.cli.cmd.ret;
import com.xietee.cli.cmd.search;
import com.xietee.cli.cmd.update;
import com.xietee.common.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xietee.exception.BusinessExceptionEnum.COMMAND_ERROR;
import static com.xietee.exception.BusinessExceptionEnum.COMMAND_PARAM_ERROR;
import static com.xietee.exception.BusinessExceptionEnum.COMMAND_UNEXISTED;
import static com.xietee.exception.BusinessExceptionEnum.INVALID_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Command 的解析与权限测试。
 *
 * <p>命令的 execute() 需要 JLine 终端，这里只覆盖纯解析阶段：
 * 命令名 + 参数 -> Cmd 实例 / 错误码，以及四类用户的命令可见性。</p>
 */
class CommandTest {

    private Command command;

    @BeforeEach
    void setUp() {
        App.DebugSetUser(UserType.NULL);
        command = new Command();
    }

    @AfterEach
    void tearDown() {
        App.DebugSetUser(UserType.NULL);
    }

    private Result<Cmd> parseAs(UserType user, String line) {
        App.DebugSetUser(user);
        return command.parseAndGetCommand(line);
    }

    // ------------------------------------------------------------ 基础解析

    @Test
    @DisplayName("空行：返回成功，但 data 为 null（App 会跳过空行）")
    void emptyLineReturnsSuccessWithoutCommand() {
        Result<Cmd> result = parseAs(UserType.ADMIN, "");

        assertEquals(200, result.code());
        assertEquals("", result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("未知命令：返回 COMMAND_ERROR")
    void unknownCommandReturnsError() {
        Result<Cmd> result = parseAs(UserType.ADMIN, "nonsense");

        assertEquals(COMMAND_ERROR.getCode(), result.code());
        assertEquals(COMMAND_ERROR.getMessage(), result.message());
        assertNull(result.data());
    }

    @Test
    @DisplayName("命令名区分大小写")
    void commandNameIsCaseSensitive() {
        assertEquals(COMMAND_ERROR.getCode(),
                parseAs(UserType.ADMIN, "LS -o book").code());
    }

    // ------------------------------------------------------------ 权限矩阵

    @Test
    @DisplayName("未登录（NULL）：只能使用通用命令")
    void nullUserCanOnlyUseGeneralCommands() {
        for (String line : new String[]{"help", "quit", "clear", "login", "ls", "search", "history"}) {
            assertEquals(200, parseAs(UserType.NULL, line).code(), line + " 应该可用");
        }
        for (String line : new String[]{"add -o book", "update -o book -k 1", "del -o book -k 1",
                "borrow -i 1", "ret -i 1"}) {
            assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.NULL, line).code(), line + " 应该不可用");
        }
    }

    @Test
    @DisplayName("学生：通用命令 + borrow/ret，不能用 add/update/del")
    void studentPermissions() {
        assertEquals(200, parseAs(UserType.STUDENT, "borrow -i 1").code());
        assertEquals(200, parseAs(UserType.STUDENT, "ret -i 1").code());
        assertEquals(200, parseAs(UserType.STUDENT, "history").code());

        assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.STUDENT, "add -o book").code());
        assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.STUDENT, "update -o book -k 1").code());
        assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.STUDENT, "del -o book -k 1").code());
    }

    @Test
    @DisplayName("管理员：通用命令 + add/update/del，不能用 borrow/ret")
    void adminPermissions() {
        assertEquals(200, parseAs(UserType.ADMIN, "add -o book").code());
        assertEquals(200, parseAs(UserType.ADMIN, "update -o book -k 1").code());
        assertEquals(200, parseAs(UserType.ADMIN, "del -o book -k 1").code());
        assertEquals(200, parseAs(UserType.ADMIN, "history").code());

        assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.ADMIN, "borrow -i 1").code());
        assertEquals(COMMAND_ERROR.getCode(), parseAs(UserType.ADMIN, "ret -i 1").code());
    }

    @Test
    @DisplayName("开发者：当前权限尚未开放，所有命令都返回 COMMAND_ERROR")
    void developerHasNoCommandsYet() {
        for (String line : new String[]{"help", "ls -o book", "add -o book", "borrow -i 1"}) {
            assertEquals(COMMAND_ERROR.getCode(),
                    parseAs(UserType.DEVELOPER, line).code(), line + " 应该不可用");
        }
    }

    // ------------------------------------------------------------ ls / search

    @Test
    @DisplayName("ls：学生只能列出 book，管理员可以列出 student/book")
    void lsObjectPermission() {
        assertEquals(200, parseAs(UserType.STUDENT, "ls -o book").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.STUDENT, "ls -o student").code());

        assertEquals(200, parseAs(UserType.ADMIN, "ls -o book").code());
        assertEquals(200, parseAs(UserType.ADMIN, "ls -o student").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "ls -o admin").code());
    }

    @Test
    @DisplayName("ls：-a/--all 与 -o/--object 可以组合")
    void lsAcceptsFlags() {
        assertEquals(200, parseAs(UserType.ADMIN, "ls -o book -a").code());
        assertEquals(200, parseAs(UserType.ADMIN, "ls --object student --all").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "ls -o").code());
    }

    @Test
    @DisplayName("search：对象权限与关键词参数")
    void searchObjectAndKeyword() {
        assertEquals(200, parseAs(UserType.STUDENT, "search -o book -k Java").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.STUDENT, "search -o student -k 张三").code());

        assertEquals(200, parseAs(UserType.ADMIN, "search -o student -k 张三").code());
        assertEquals(200, parseAs(UserType.ADMIN, "search --object book --keyword Java --all").code());
        assertEquals(200, parseAs(UserType.ADMIN, "search -k Java").code(),
                "对象缺省时执行阶段再询问");
    }

    // ------------------------------------------------------------ add/update/del

    @Test
    @DisplayName("add：管理员只能添加 book")
    void addObjectPermission() {
        assertEquals(200, parseAs(UserType.ADMIN, "add -o book").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "add -o student").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "add -o").code());
    }

    @Test
    @DisplayName("update：管理员可以更新 student/record/book")
    void updateObjectPermission() {
        assertEquals(200, parseAs(UserType.ADMIN, "update -o student -k 202400010012").code());
        assertEquals(200, parseAs(UserType.ADMIN, "update -o book -k 1").code());
        assertEquals(200, parseAs(UserType.ADMIN, "update -o record -k 1").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "update -o admin -k 1").code());
    }

    @Test
    @DisplayName("del：管理员只能删除 book")
    void deleteObjectPermission() {
        assertEquals(200, parseAs(UserType.ADMIN, "del -o book -k 1").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "del -o student -k 202400010012").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.ADMIN, "del -o").code());
    }

    // ------------------------------------------------------------ borrow/ret

    @Test
    @DisplayName("borrow：-i 后面必须跟正整数")
    void borrowParsesBookId() {
        assertEquals(200, parseAs(UserType.STUDENT, "borrow -i 1").code());
        assertEquals(200, parseAs(UserType.STUDENT, "borrow").code(), "缺省 -i 时执行阶段再提示");
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.STUDENT, "borrow -i").code());
        assertEquals(INVALID_NUMBER.getCode(),
                parseAs(UserType.STUDENT, "borrow -i abc").code());
        assertEquals(INVALID_NUMBER.getCode(),
                parseAs(UserType.STUDENT, "borrow -i 0").code());
        assertEquals(INVALID_NUMBER.getCode(),
                parseAs(UserType.STUDENT, "borrow -i -1").code());
    }

    @Test
    @DisplayName("ret：-i 后面必须跟正整数")
    void returnParsesRecordId() {
        assertEquals(200, parseAs(UserType.STUDENT, "ret -i 2").code());
        assertEquals(200, parseAs(UserType.STUDENT, "ret").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.STUDENT, "ret -i").code());
        assertEquals(INVALID_NUMBER.getCode(),
                parseAs(UserType.STUDENT, "ret -i abc").code());
    }

    // ------------------------------------------------------------ history/login

    @Test
    @DisplayName("history：-u 表示只看未归还记录")
    void historyAcceptsUnreturnedFlag() {
        assertEquals(200, parseAs(UserType.STUDENT, "history").code());
        assertEquals(200, parseAs(UserType.STUDENT, "history -u").code());
        assertEquals(200, parseAs(UserType.ADMIN, "history -u").code());
    }

    @Test
    @DisplayName("login：student / admin 合法，其他角色返回 COMMAND_PARAM_ERROR")
    void loginValidatesUserType() {
        assertEquals(200, parseAs(UserType.NULL, "login student").code());
        assertEquals(200, parseAs(UserType.NULL, "login admin").code());
        assertEquals(COMMAND_PARAM_ERROR.getCode(),
                parseAs(UserType.NULL, "login developer").code());
    }

    // ------------------------------------------------------------ help

    @Test
    @DisplayName("help：不带参数 / all / help 都能解析成功")
    void helpAcceptsKnownTopics() {
        assertEquals(200, parseAs(UserType.ADMIN, "help").code());
        assertEquals(200, parseAs(UserType.ADMIN, "help all").code());
        assertEquals(200, parseAs(UserType.ADMIN, "help help").code());
    }

    @Test
    @DisplayName("help：未知的帮助主题返回 COMMAND_UNEXISTED")
    void helpUnknownTopicReturnsError() {
        Result<Cmd> result = parseAs(UserType.ADMIN, "help unexisted");

        assertEquals(COMMAND_UNEXISTED.getCode(), result.code());
        assertEquals(COMMAND_UNEXISTED.getMessage(), result.message());
    }

    // ------------------------------------------------------------ 命令实例

    @Test
    @DisplayName("已注册命令：返回对应的 Cmd 实例")
    void registeredCommandsReturnCmdInstances() {
        assertInstanceOf(help.class, parseAs(UserType.ADMIN, "help").data());
        assertInstanceOf(quit.class, parseAs(UserType.ADMIN, "quit").data());
        assertInstanceOf(clear.class, parseAs(UserType.ADMIN, "clear").data());
        assertInstanceOf(login.class, parseAs(UserType.ADMIN, "login").data());
        assertInstanceOf(ls.class, parseAs(UserType.ADMIN, "ls -o book").data());
        assertInstanceOf(search.class, parseAs(UserType.ADMIN, "search -o book").data());
        assertInstanceOf(history.class, parseAs(UserType.ADMIN, "history").data());
        assertInstanceOf(add.class, parseAs(UserType.ADMIN, "add -o book").data());
        assertInstanceOf(update.class, parseAs(UserType.ADMIN, "update -o book -k 1").data());
        assertInstanceOf(del.class, parseAs(UserType.ADMIN, "del -o book -k 1").data());
        assertInstanceOf(borrow.class, parseAs(UserType.STUDENT, "borrow -i 1").data());
        assertInstanceOf(ret.class, parseAs(UserType.STUDENT, "ret -i 1").data());
    }
}
