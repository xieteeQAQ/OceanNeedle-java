package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.dto.BookUpdateDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;
import com.xietee.ui.AppUI;
import com.xietee.util.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class update implements Cmd {

    private final App app;

    private String object = "";

    private String key = "";

    private final Set<String> objects = new HashSet<>(Set.of(
            "student", "admin", "developer", "record", "book"
    ));

    public update(App app) {
        this.app = app;
    }

    @Override
    public void reset() {
        this.object = "";
        this.key = "";
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

        if (args.size() > 1) {
            key = args.get(1);
        }

        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (this.object.isEmpty()) {
            ui.barPrintOption("要更新的对象是? \n[0]取消 \n[1]学生 \n[2]书籍 \n[3]记录 \n[4]管理员 \n[5]开发者");
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("1")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要更新的学生的学号");
                key = ui.readInput("学号 > ").trim();

                if (key.isEmpty()) {
                    return Result.warn(COMMAND_NEED_PARAM);
                }

            } else if (key.length() != 12) {
                return Result.warn(PARAM_ERROR.getCode(), "学生学号必须为 12 位数");
            }

            Student updateStudent = app.getAdminService().findStudentByStudentID(key);
            // 获取要更新的数据 直接按回车即可跳过
            ui.getStatusBar().printInfo("(按回车可跳过)");
            String password = ui.readInput("密码(" + updateStudent.getPassword() + ") > ").trim();
            String phoneNumber = ui.readInput("电话号码(" + updateStudent.getPhoneNumber() + ") > ").trim();

            // 创建学生 DTO 对象
            StudentUpdateDTO studentUpdateDTO =
                    new StudentUpdateDTO(
                            password.isEmpty() ? updateStudent.getPassword() : password,
                            phoneNumber.isEmpty() ? updateStudent.getPhoneNumber() : phoneNumber);

            // 没更新数据视作更新成功
            if (studentUpdateDTO.equals(updateStudent)) return Result.success(DATA_UNCHANGED);

            try {
                ValidationUtils.validateOrThrow(studentUpdateDTO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PARAM_ERROR.getCode(), e.getMessage());
            }

            // 检查是否违反 unique 字段
            if (app.getAdminService().existedPhoneNumber(phoneNumber)) {
                return Result.warn(STUDENT_CONTRACT_NUMBER_EXISTED);
            }

            // 通过之前查到的学生对象获取其在数据库中的 id
            // 按 id 更新对象
            app.getAdminService().updateStudentById(updateStudent.getId(), studentUpdateDTO);

            return Result.success("更新成功", null);
        } else if (object.equals("book") || object.equals("2")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要更新的书籍的序号");
                key = ui.readInput("序号 > ").trim();

                if (key.isEmpty()) {
                    return Result.warn(COMMAND_NEED_PARAM);
                }

            } else if (!key.matches("^[1-9]\\d*$")) {
                return Result.warn(INVALID_NUMBER);
            }

            long id = Long.parseLong(key);

            Book updateBook = app.getAdminService().findBookById(id);
            // 获取要更新的数据 直接按回车即可跳过
            ui.getStatusBar().printInfo("(按回车可跳过)");
            String title = ui.readInput("书名(" + updateBook.getTitle() + ") > ").trim();
            String author = ui.readInput("作者(" + updateBook.getAuthor() + ") > ").trim();
            String available = ui.readInput("是否借出(" + Book.map(updateBook.isAvailable()) + ") > ").trim();

            // 创建学生 DTO 对象
            BookUpdateDTO bookUpdateDTO =
                    new BookUpdateDTO(
                            title.isEmpty() ? updateBook.getTitle() : title,
                            author.isEmpty() ? updateBook.getAuthor() : author,
                            Book.map(available));

            // 没更新数据视作更新成功
            if (bookUpdateDTO.equals(updateBook)) return Result.success(DATA_UNCHANGED);

            try {
                ValidationUtils.validateOrThrow(bookUpdateDTO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PARAM_ERROR.getCode(), e.getMessage());
            }

            // 通过之前查到的学生对象获取其在数据库中的 id
            // 按 id 更新对象
            app.getAdminService().updateBookById(id, bookUpdateDTO);

            return Result.success("更新成功", null);
        } else {
            return Result.success(FEATURE_NOT_DEVELOPED);
        }
    }
}
