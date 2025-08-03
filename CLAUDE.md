# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述 | Project Overview

这是一个基于 Spring Boot 3.4.8 的 AI 代理项目，专注于构建恋爱心理咨询应用。项目集成了多种 AI 框架和模型，包括 Spring AI、LangChain4j、阿里云 DashScope 和 Ollama。

This is a Spring Boot 3.4.8-based AI agent project focused on building a love psychology consultation application. The project integrates multiple AI frameworks and models including Spring AI, LangChain4j, Alibaba Cloud DashScope, and Ollama.

## 开发环境和构建命令 | Development Environment and Build Commands

### Maven 命令 | Maven Commands
```bash
# 编译项目 | Compile project
mvn compile

# 运行测试 | Run tests
mvn test

# 运行单个测试类 | Run single test class
mvn test -Dtest=LoveAppTest

# 清理并编译 | Clean and compile
mvn clean compile

# 打包应用 | Package application
mvn clean package

# 启动应用 | Start application
mvn spring-boot:run

# 跳过测试打包 | Package without tests
mvn clean package -DskipTests
```

### 配置文件 | Configuration Files
- `application.yml`: 主配置文件，包含服务器端口(8123)、Ollama 配置、API 文档配置、JPA 配置、记忆存储类型配置
- `application-local.yml`: 本地开发配置，包含 DashScope API 密钥、MySQL 数据库连接配置
- 默认激活 local 配置文件 | Default active profile: local
- 支持配置化选择对话记忆存储方式: `app.chat.memory.type` (mysql/file/memory)

### 服务端点 | Service Endpoints
- 应用端口: `8123`
- 上下文路径: `/api`
- 健康检查: `GET /api/health`
- Swagger UI: `http://localhost:8123/api/swagger-ui.html`
- API 文档: `http://localhost:8123/api/v3/api-docs`

## 项目架构 | Project Architecture

### 核心模块结构 | Core Module Structure

```
com.yupi.yuaiagent/
├── app/                    # 应用核心逻辑 | Core application logic
│   └── LoveApp.java       # 恋爱咨询主应用类 | Main love consultation app
├── advisor/               # 拦截器和增强器 | Advisors and enhancers
│   ├── MyLoggerAdvisor.java      # 自定义日志拦截器 | Custom logging advisor
│   └── ReReadingAdvisor.java     # 重读拦截器 | Re-reading advisor
├── chatmemory/           # 对话记忆管理 | Chat memory management
│   ├── FileBasedChatMemory.java  # 基于文件的对话记忆 | File-based chat memory
│   ├── MySQLChatMemory.java      # 基于MySQL的对话记忆 | MySQL-based chat memory
│   ├── entity/                   # 数据库实体类 | Database entities
│   │   └── ChatMessageEntity.java # 聊天消息实体 | Chat message entity
│   └── repository/               # 数据访问层 | Data access layer
│       └── ChatMessageRepository.java # 聊天消息存储库 | Chat message repository
├── controller/           # REST 控制器 | REST controllers
│   └── HealthController.java     # 健康检查控制器 | Health check controller
└── demo/invoke/          # AI 调用示例 | AI invocation examples
    ├── HttpAiInvoke.java         # HTTP 方式调用 | HTTP invocation
    ├── LangChainAiInvoke.java    # LangChain4j 调用 | LangChain4j invocation
    ├── OllamaAIInvoke.java       # Ollama 调用 | Ollama invocation
    ├── SdkAiInvoke.java          # SDK 调用 | SDK invocation
    ├── SpringAIInvoke.java       # Spring AI 调用 | Spring AI invocation
    └── TestApiKey.java           # API 密钥测试 | API key testing
```

### 技术栈 | Technology Stack

**核心框架 | Core Frameworks:**
- Spring Boot 3.4.8 (Java 21)
- Spring AI 1.0.0-M6 (Ollama + Alibaba Cloud AI)
- LangChain4j 1.0.0-beta2 (DashScope Community)

**AI 集成 | AI Integration:**
- **DashScope**: 阿里云通义千问模型 (qwen-plus) | Alibaba Cloud Qwen models
- **Ollama**: 本地模型支持 (gemma3:1b) | Local model support
- **Spring AI**: 统一 AI 接口抽象 | Unified AI interface abstraction

**数据持久化 | Data Persistence:**
- Spring Data JPA: ORM 框架和数据访问层 | ORM framework and data access layer
- MySQL 8.0.33: 关系型数据库，用于对话记忆持久化 | Relational database for chat memory persistence
- Kryo 5.6.2: 文件存储方式的对话记忆序列化 | Chat memory serialization for file storage
- 支持多种存储方式: MySQL、文件系统、内存 | Multiple storage options: MySQL, file system, in-memory

**开发工具 | Development Tools:**
- Lombok: 代码简化 | Code simplification
- Knife4j: API 文档增强 | Enhanced API documentation
- Hutool: 工具类库 | Utility library

### 关键架构概念 | Key Architectural Concepts

**1. ChatClient 架构 | ChatClient Architecture**
- 基于 Builder 模式构建 ChatClient
- 支持系统提示词 (System Prompt) 配置
- 可插拔的 Advisor 机制用于功能增强

**2. Advisor 模式 | Advisor Pattern**
- `MessageChatMemoryAdvisor`: 对话记忆管理
- `MyLoggerAdvisor`: 请求/响应日志记录
- `ReReadingAdvisor`: 重读机制 (可选)
- 支持链式调用和参数传递

**3. 对话记忆管理 | Chat Memory Management**
- 抽象接口: `ChatMemory`
- MySQL实现: `MySQLChatMemory` (基于 Spring Data JPA)
- 文件实现: `FileBasedChatMemory` (基于 Kryo 序列化)
- 内存实现: `InMemoryChatMemory` (可选)
- 会话 ID 隔离和消息数量限制
- 支持配置化切换存储方式 (`app.chat.memory.type`)

**4. 多模型支持 | Multi-Model Support**
- 配置化切换不同 AI 模型
- 统一的 ChatModel 接口
- 环境隔离的配置管理

## 开发指南 | Development Guidelines

### 添加新的 AI 调用方式 | Adding New AI Invocation Methods
1. 在 `demo/invoke/` 包下创建新的调用类
2. 实现 `CommandLineRunner` 接口 (可选)
3. 使用 `@Component` 注解但默认注释以避免自动启动
4. 参考现有的 `SpringAIInvoke.java` 或 `LangChainAiInvoke.java`

### 扩展 Advisor 功能 | Extending Advisor Functionality
1. 实现 `CallAroundAdvisor` 或 `StreamAroundAdvisor` 接口
2. 重写 `getName()`, `getOrder()`, `aroundCall()` 方法
3. 在 LoveApp 构建 ChatClient 时添加到 `defaultAdvisors()`

### 对话记忆存储扩展 | Extending Chat Memory Storage
1. 实现 `ChatMemory` 接口
2. 重写 `add()`, `get()`, `clear()` 方法
3. 在 LoveApp 构造器中替换 ChatMemory 实现
4. **MySQL 实现参考**: 继承 `MySQLChatMemory`，使用 JPA Repository 模式
5. **自定义实体类**: 继承或修改 `ChatMessageEntity`，添加自定义字段
6. **数据库迁移**: 使用 `database/init.sql` 或 JPA DDL 自动更新

### 配置管理 | Configuration Management
- 敏感信息 (如 API 密钥) 配置在 `application-local.yml`
- 公共配置放在 `application.yml`
- 使用 Spring Profiles 进行环境隔离
- **MySQL 配置**: 数据库连接、JPA 设置均在配置文件中管理
- **记忆存储选择**: 通过 `app.chat.memory.type` 配置项选择存储方式 (mysql/file/memory)

### 测试策略 | Testing Strategy
- 单元测试位于 `src/test/java/` 对应包结构下
- 集成测试可以使用 `@SpringBootTest` 注解
- AI 调用测试建议使用 Mock 或测试专用 API 密钥

### API 开发规范 | API Development Standards
- 控制器放在 `controller` 包下
- 使用 RESTful 风格的 URL 设计
- 统一的错误处理和响应格式
- Swagger/OpenAPI 文档自动生成

## 重要注意事项 | Important Notes

1. **API 密钥安全**: 不要将真实 API 密钥提交到版本控制系统
2. **对话记忆文件**: `tmp/chat-memory/` 目录包含序列化的对话数据，注意隐私保护
3. **模型配置**: Ollama 需要本地安装并运行在 localhost:11434
4. **Java 版本**: 项目要求 Java 21，确保本地环境匹配
5. **MySQL 数据库**: 
   - 需要 MySQL 8.0+ 数据库实例
   - 使用 `database/init.sql` 初始化数据库和表结构
   - 默认连接配置: `192.168.2.102:3306/yu_ai_agent`
6. **存储方式选择**: 
   - 生产环境推荐使用 MySQL (`app.chat.memory.type: mysql`)
   - 开发测试可使用文件存储 (`app.chat.memory.type: file`)
   - 临时测试可使用内存存储 (`app.chat.memory.type: memory`)
7. **数据库权限**: 确保 MySQL 用户具有对 `yu_ai_agent` 数据库的完整读写权限