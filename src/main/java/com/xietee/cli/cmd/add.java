package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.dto.StudentSaveDTO;
import com.xietee.exception.BusinessException;
import com.xietee.ui.AppUI;
import com.xietee.util.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class add implements Cmd {

    private final App app;

    private String object = "";

    private final Set<String> objects = new HashSet<>(Set.of(
            "student", "admin", "developer", "record", "book"
    ));

    public add(App app) {
        this.app = app;
    }

    @Override
    public Result<Void> parse(List<String> args) {

        if (args.isEmpty()) {
            return Result.success(NONE);
        }
        String argsObject = args.getFirst();

        if (objects.contains(argsObject)) {
            this.object = argsObject;
        } else {
            return Result.warn(COMMAND_PARAM_ERROR);
        }

        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (object.isEmpty()) {
            ui.barPrintOption("要增加的对象是? \n[0]取消 \n[1]学生 \n[2]楼栋 \n[3]宿舍 \n[4]床位 \n[5]报修单");
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(NOT_A_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("1")) {

            String studentID = ui.readInput("学号 > ").trim();
            String password = ui.readInput("密码 > ").trim();
            String name = ui.readInput("名字 > ").trim();
            String phoneNumber = ui.readInput("电话号码 > ").trim();

            // 创建学生 DTO 对象
            StudentSaveDTO studentSaveDTO =
                    new StudentSaveDTO(
                            studentID,
                            password,
                            name,
                            phoneNumber);

            // 校验参数
            try {
                ValidationUtils.validateOrThrow(studentSaveDTO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PARAM_ERROR.getCode(), e.getMessage());
            }

            // 检查是否违反 unique 字段
            if (app.getStudentService().existedStudentID(studentID)) {
                return Result.warn(STUDENT_ID_EXISTED);
            } else if (app.getStudentService().existedPhoneNumber(phoneNumber)) {
                return Result.warn(STUDENT_CONTRACT_NUMBER_EXISTED);
            }

            // 添加学生
            app.getStudentService().insertStudent(studentSaveDTO);

            return Result.success("学生添加成功", null);
        } else {
            return Result.success(FEATURE_NOT_DEVELOPED);
        }
    }
}
