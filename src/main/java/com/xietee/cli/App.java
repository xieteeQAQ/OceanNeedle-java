package com.xietee.cli;

import com.xietee.cli.cmd.Cmd;
import com.xietee.common.Result;
import com.xietee.exception.BusinessException;
import com.xietee.service.AdminService;
import com.xietee.ui.AnsiColors;
import com.xietee.ui.AppUI;
import lombok.Getter;
import lombok.Setter;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App {

    @Getter @Setter private boolean running;

    @Getter private final AdminService adminService;

    @Getter private final AppUI ui;

    private final Command command;
    private Cmd cmd;

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public App(AdminService adminService) throws IOException {
        this.running = true;
        this.adminService = adminService;
        this.ui = new AppUI();
        this.command = new Command(this);

        this.ui.initTerminal();
    }

    public void run() {

        String prompt = "Command > ";
        boolean mySweetTip = true;

        ui.showBanner();
        ui.showWelcome();

        while (running) {

            try {
                if (mySweetTip) {
                    ui.consoleWrite(
                                    "<xieteeQAQ> 按 "
                                            + AnsiColors.BLACK__WHITE
                                            + " Tab "
                                            + AnsiColors.RESET
                                            + " 可以补全命令哦 (QWQ)/~");
                    mySweetTip = false;
                }

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
                        if (!executeResult.message().isBlank()) {
                            if (executeResult.code() == 200) {
                                ui.barPrintSuccess(executeResult.message());
                            } else {
                                ui.barPrintWarning(executeResult.message());
                            }
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
        ui.barPrintInfo("bye.");
    }
}
