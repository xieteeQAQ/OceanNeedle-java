package com.xietee.cli;

import com.xietee.cli.cmd.Cmd;
import com.xietee.common.Result;
import com.xietee.exception.BusinessException;
import com.xietee.service.AdminService;
import com.xietee.service.LoginService;
import com.xietee.service.StudentService;
import com.xietee.ui.AnsiColors;
import com.xietee.ui.AppUI;
import lombok.Getter;
import lombok.Setter;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

import java.io.IOException;

public class App {

    @Getter @Setter private boolean running;

    @Getter private static UserType user;

    @Getter private final StudentService studentService;
    @Getter private final AdminService adminService;
    @Getter private final LoginService loginService;

    @Getter private final AppUI ui;

    private final Command command;
    private Cmd cmd;

    public App(StudentService studentService, AdminService adminService, LoginService loginService) throws IOException {
        this.running = true;
        this.studentService = studentService;
        this.adminService = adminService;
        this.loginService = loginService;
        this.ui = new AppUI();
        user = UserType.NULL;
        this.command = new Command(this);

        this.ui.initTerminal();
    }

    public void setUser(UserType _user) {
        user = _user;
        this.command.setUser(user);
    }

    public static void DebugSetUser(UserType _user) {
        user = _user;
    }

    public void run() {

        while (running) {
            while (running && user == UserType.NULL) {
                try {
                    Cmd login = command.parseAndGetCommand("login").data();
                    var result = login.execute();
                    if (result.code() == 200) {
                        ui.clearConsole();
                        ui.barPrintSuccess(result.message());
                    } else {
                        ui.clearConsole();
                        ui.barPrintWarning(result.message());
                    }
                } catch (BusinessException e) {
                    ui.barPrintWarning(e.getMessage());
                } catch (UserInterruptException e) {
                    ui.barPrintError(e.getMessage());
                } catch (EndOfFileException e) {
                    ui.barPrintError(e.getMessage());
                    running = false;
                } finally {
                    if (cmd != null)
                        cmd.reset();
                }
            }
            if (!running) break;

            String prompt = "Command > ";

            ui.showBanner();
            ui.showWelcome();
            ui.consoleWrite(
                    "<xieteeQAQ> 按 "
                            + AnsiColors.BLACK__WHITE
                            + " Tab "
                            + AnsiColors.RESET
                            + " 可以补全命令哦 (QWQ)/~");

            while (running) {
                try {
                    String line;

                    line = ui.readInput(prompt).trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    Result<Cmd> parseResult = command.parseAndGetCommand(line);
                    if (parseResult.code() == 200) {
                        cmd = parseResult.data();
                        var executeResult = cmd.execute();
                        cmd.reset();
                        if (executeResult.code() == 200) {
                            ui.barPrintSuccess(executeResult.message());
                        } else {
                            ui.barPrintWarning(executeResult.message());
                        }
                    } else {
                        ui.barPrintWarning(parseResult.message());
                    }

                } catch (BusinessException e) {
                    ui.barPrintWarning(e.getMessage());
                } catch (UserInterruptException e) {
                    ui.barPrintError(e.getMessage());
                } catch (EndOfFileException e) {
                    ui.barPrintError(e.getMessage());
                    running = false;
                } finally {
                    if (cmd != null)
                        cmd.reset();
                }
            }
        }
        ui.barPrintInfo("bye.");
    }
}
