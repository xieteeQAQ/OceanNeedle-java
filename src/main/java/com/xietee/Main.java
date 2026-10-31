package com.xietee;

import com.xietee.cli.App;
import com.xietee.dao.AdminMapper;
import com.xietee.dao.BookMapper;
import com.xietee.dao.RecordMapper;
import com.xietee.dao.StudentMapper;
import com.xietee.service.AdminService;
import com.xietee.service.LoginService;
import com.xietee.service.StudentService;
import com.xietee.ui.AnsiColors;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /** SqlSessionFactory 昂贵且线程安全，整个应用只应该建一个。 */
    private static final SqlSessionFactory SQL_SESSION_FACTORY = buildSqlSessionFactory();

    private static SqlSessionFactory buildSqlSessionFactory() {
        // mybatis-config.xml 位于 src/main/resources，会被打包到 classpath 根目录
        try (InputStream in = Resources.getResourceAsStream("mybatis-config.xml")) {
            return new SqlSessionFactoryBuilder().build(in);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "加载 mybatis-config.xml 失败，请检查 src/main/resources/ 下的 mybatis-config.xml 与 jdbc.properties", e);
        }
    }

    public static void main(String[] args) {
        try (SqlSession session = SQL_SESSION_FACTORY.openSession(true)) { // true = 自动提交
            StudentMapper studentMapper = session.getMapper(StudentMapper.class);
            BookMapper bookMapper = session.getMapper(BookMapper.class);
            AdminMapper adminMapper = session.getMapper(AdminMapper.class);
            RecordMapper recordMapper = session.getMapper(RecordMapper.class);
            StudentService studentService = new StudentService(studentMapper, bookMapper, recordMapper);
            AdminService adminService = new AdminService(studentMapper, bookMapper, recordMapper);
            LoginService loginService = new LoginService(studentMapper, adminMapper);

            runApp(studentService, adminService, loginService);

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println(AnsiColors.BRIGHT_RED + "程序异常" + AnsiColors.RESET);
        }
    }

    private static void runApp(StudentService studentService, AdminService adminService, LoginService loginService) throws Exception {
        App app = new App(studentService, adminService, loginService);

        app.run();
    }
}
