package com.xietee.support;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * H2 内存数据库测试支持：整个测试 JVM 共用一套 SqlSessionFactory，
 * 每个测试用例开始前重建 student 表，避免用例之间互相污染。
 */
public final class H2Database {

    private static final String CONFIG = "mybatis-config-test.xml";
    private static final String SCHEMA = "schema-h2.sql";

    /** SqlSessionFactory 昂贵且线程安全，测试里也只建一次。 */
    private static final SqlSessionFactory FACTORY = buildFactory();

    private H2Database() {
    }

    public static SqlSessionFactory factory() {
        return FACTORY;
    }

    /** 删除并重建 student 表，让每个测试都从干净的数据开始。 */
    public static void resetSchema() {
        try (SqlSession session = FACTORY.openSession(true)) {
            ScriptRunner runner = new ScriptRunner(session.getConnection());
            runner.setLogWriter(null);   // 不要在控制台打印 SQL
            runner.setStopOnError(true);
            try (Reader reader = Resources.getResourceAsReader(SCHEMA)) {
                runner.runScript(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("执行测试建表脚本失败: " + SCHEMA, e);
        }
    }

    private static SqlSessionFactory buildFactory() {
        try (InputStream in = Resources.getResourceAsStream(CONFIG)) {
            return new SqlSessionFactoryBuilder().build(in);
        } catch (IOException e) {
            throw new IllegalStateException("加载测试用 MyBatis 配置失败: " + CONFIG, e);
        }
    }
}
