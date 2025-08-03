package com.yupi.yuaiagent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL 数据库连接测试 (不依赖 Spring 上下文)
 */
@Slf4j
public class MySQLConnectionTest {

    // 从配置文件读取的数据库连接信息
    private static final String URL = "jdbc:mysql://192.168.2.102:3306/yu_ai_agent?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";


    /**
     * 简单的连接测试方法
     */
    @Test
    public void simpleConnectionTest() {
        log.info("=== 简单连接测试 ===");
        
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            if (connection != null && !connection.isClosed()) {
                log.info("✅ 数据库连接正常");
                log.info("连接URL: {}", URL);
                log.info("用户名: {}", USERNAME);
            }
        } catch (SQLException e) {
            log.error("❌ 数据库连接失败: {}", e.getMessage());
            log.error("请检查以下配置:");
            log.error("1. 数据库服务是否启动");
            log.error("2. 数据库地址和端口是否正确: 192.168.2.102:3306");
            log.error("3. 数据库名称是否存在: yu_ai_agent");
            log.error("4. 用户名密码是否正确: {}/{}", USERNAME, "******");
            log.error("5. 防火墙是否允许连接");
        }
    }
}