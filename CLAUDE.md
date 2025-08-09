# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述 | Project Overview

这是一个基于 Spring Boot 3.4.8 的 AI 代理项目，专注于构建恋爱心理咨询应用。项目集成了多种 AI 框架和模型，包括 Spring AI、LangChain4j、阿里云 DashScope 和 Ollama。项目现已集成完整的 RAG (Retrieval-Augmented Generation) 技术栈，提供智能文档检索和知识增强功能。

This is a Spring Boot 3.4.8-based AI agent project focused on building a love psychology consultation application. The project integrates multiple AI frameworks and models including Spring AI, LangChain4j, Alibaba Cloud DashScope, and Ollama. The project now includes a complete RAG (Retrieval-Augmented Generation) technology stack, providing intelligent document retrieval and knowledge enhancement capabilities.

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
- `application.yml`: 主配置文件，包含服务器端口(8123)、Ollama 配置、API 文档配置
- `application-local.yml`: 本地开发配置，包含 DashScope API 密钥
- 默认激活 local 配置文件 | Default active profile: local

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
│   └── FileBasedChatMemory.java  # 基于文件的对话记忆 | File-based chat memory
├── constant/             # 常量定义 | Constants
│   └── FileConstant.java         # 文件路径常量 | File path constants
├── controller/           # REST 控制器 | REST controllers
│   └── HealthController.java     # 健康检查控制器 | Health check controller
├── tools/                # 工具调用功能 | Tool Function Calling (新增)
│   ├── FileOperationTool.java    # 文件操作工具 | File operation tool
│   ├── ResourceDownloadTool.java # 资源下载工具 | Resource download tool
│   ├── PDFGenerationTool.java    # PDF生成工具 | PDF generation tool
│   └── ToolRegistration.java     # 工具注册配置 | Tool registration config
├── rag/                  # RAG 技术栈 | RAG Technology Stack
│   ├── LoveAppDocumentLoader.java              # 文档加载器 | Document loader
│   ├── LoveAppVectorStoreConfig.java           # 向量存储配置 | Vector store config
│   ├── LoveAppRagCloudAdvisorConfig.java       # 云端 RAG 增强器 | Cloud RAG advisor
│   ├── LoveAppRagCustomAdvisorFactory.java     # 自定义 RAG 工厂 | Custom RAG factory
│   ├── LoveAppContextualQueryAugmenterFactory.java # 上下文查询增强 | Contextual query augmenter
│   ├── MyKeywordEnricher.java                  # 关键词增强器 | Keyword enricher
│   ├── PgVectorVectorStoreConfig.java          # PostgreSQL 向量存储 | PgVector config
│   └── QueryRewriter.java                     # 查询重写器 | Query rewriter
└── demo/                 # 示例和测试 | Demos and examples
    ├── invoke/           # AI 调用示例 | AI invocation examples
    │   ├── HttpAiInvoke.java         # HTTP 方式调用 | HTTP invocation
    │   ├── LangChainAiInvoke.java    # LangChain4j 调用 | LangChain4j invocation
    │   ├── OllamaAIInvoke.java       # Ollama 调用 | Ollama invocation
    │   ├── SdkAiInvoke.java          # SDK 调用 | SDK invocation
    │   ├── SpringAIInvoke.java       # Spring AI 调用 | Spring AI invocation
    │   └── TestApiKey.java           # API 密钥测试 | API key testing
    └── rag/              # RAG 示例 | RAG examples
        └── MultiQueryExpanderDemo.java        # 多查询扩展示例 | Multi-query expander demo
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

**RAG 技术栈 | RAG Technology Stack:**
- **文档处理 | Document Processing**: Spring AI MarkdownDocumentReader
- **向量存储 | Vector Storage**: SimpleVectorStore, PgVector 支持
- **嵌入模型 | Embedding Model**: DashScope Embedding
- **文档增强 | Document Enhancement**: KeywordMetadataEnricher 关键词自动提取
- **查询处理 | Query Processing**: 查询重写、多查询扩展、上下文增强
- **检索增强 | Retrieval Augmentation**: RetrievalAugmentationAdvisor 集成
- **云端知识库 | Cloud Knowledge Base**: DashScope 文档检索器

**数据持久化 | Data Persistence:**
- Kryo 5.6.2: 对话记忆序列化 | Chat memory serialization
- 文件系统存储在 `tmp/chat-memory/` | File system storage
- 文档存储在 `src/main/resources/document/` | Document storage

**工具调用集成 | Tool Function Calling Integration:** (新增)
- **Spring AI Tools**: 基于注解的工具函数定义和调用
- **文件操作**: 读写文件到指定目录 (`tmp/file/`)
- **资源下载**: HTTP资源下载到本地文件系统 (`tmp/download/`)
- **PDF生成**: iText PDF生成库，支持中文字体 (`tmp/pdf/`)
- **工具注册**: 统一工具注册和管理机制

**开发工具 | Development Tools:**
- Lombok: 代码简化 | Code simplification
- Knife4j: API 文档增强 | Enhanced API documentation
- Hutool: 工具类库 | Utility library
- iText PDF: PDF 文档生成库 | PDF document generation library

### 关键架构概念 | Key Architectural Concepts

**1. ChatClient 架构 | ChatClient Architecture**
- 基于 Builder 模式构建 ChatClient
- 支持系统提示词 (System Prompt) 配置
- 可插拔的 Advisor 机制用于功能增强

**2. Advisor 模式 | Advisor Pattern**
- `MessageChatMemoryAdvisor`: 对话记忆管理
- `MyLoggerAdvisor`: 请求/响应日志记录
- `ReReadingAdvisor`: 重读机制 (可选)
- `RetrievalAugmentationAdvisor`: RAG 检索增强 (新增)
- 支持链式调用和参数传递

**3. 对话记忆管理 | Chat Memory Management**
- 抽象接口: `ChatMemory`
- 文件实现: `FileBasedChatMemory` (基于 Kryo 序列化)
- 内存实现: `InMemoryChatMemory` (可选)
- 会话 ID 隔离和消息数量限制

**4. 多模型支持 | Multi-Model Support**
- 配置化切换不同 AI 模型
- 统一的 ChatModel 接口
- 环境隔离的配置管理

**5. RAG 架构 | RAG Architecture**

**ETL 数据管线 | ETL Data Pipeline:**
- **文档收集 | Document Collection**: 自动扫描 `classpath:document/*.md`
- **文档切分 | Document Chunking**: MarkdownDocumentReader 基于水平线切分
- **元数据提取 | Metadata Extraction**: 文件名、状态标签自动提取
- **关键词增强 | Keyword Enhancement**: KeywordMetadataEnricher 自动生成关键词

**向量存储与转换 | Vector Storage & Transformation:**
- **嵌入模型 | Embedding Model**: DashScope Embedding 统一接口
- **向量存储 | Vector Store**: SimpleVectorStore (内存) + PgVector (数据库)
- **文档索引 | Document Indexing**: 自动向量化和存储管理

**文档过滤和检索 | Document Filtering & Retrieval:**
- **多查询扩展 | Multi-Query Expansion**: MultiQueryExpander 生成多个相似查询
- **查询重写 | Query Rewriting**: RewriteQueryTransformer 优化查询表达
- **检索配置 | Retrieval Configuration**: 可配置的相似度阈值和返回数量
- **云端检索 | Cloud Retrieval**: DashScope 文档检索器支持

**查询增强和关联 | Query Augmentation & Association:**
- **上下文增强 | Contextual Augmentation**: 基于检索结果增强用户查询
- **错误处理 | Error Handling**: 检索失败时的优雅降级机制
- **结果融合 | Result Fusion**: 本地向量存储 + 云端知识库结合

**6. 工具调用架构 | Tool Function Calling Architecture** (新增)

**工具定义与注册 | Tool Definition & Registration:**
- **@Tool 注解**: Spring AI 提供的工具函数标注，定义工具描述
- **@ToolParam 注解**: 参数描述和验证，提供类型安全的参数处理
- **ToolCallback 机制**: 统一的工具调用回调接口
- **ToolRegistration**: 集中式工具注册和管理配置

**工具执行与集成 | Tool Execution & Integration:**
- **ChatClient.tools()**: 在对话客户端中集成工具调用能力
- **自动工具选择**: AI 模型根据用户意图自动选择合适的工具
- **参数提取**: 从自然语言中提取工具调用所需的参数
- **结果反馈**: 工具执行结果自动融入对话上下文

**文件系统管理 | File System Management:**
- **分类存储**: 不同工具的文件按类型分目录存储 (file/, download/, pdf/)
- **路径统一**: 通过 FileConstant 统一管理文件路径 (`tmp/` 根目录)
- **目录自动创建**: 工具执行时自动创建必要的目录结构
- **异常处理**: 完整的文件操作异常处理和错误信息返回

## 开发指南 | Development Guidelines

### 添加新的 AI 调用方式 | Adding New AI Invocation Methods
1. 在 `demo/invoke/` 包下创建新的调用类
2. 实现 `CommandLineRunner` 接口 (可选)
3. 使用 `@Component` 注解但默认注释以避免自动启动
4. 参考现有的 `SpringAIInvoke.java` 或 `LangChainAiInvoke.java`

### 工具调用功能扩展 | Extending Tool Function Calling (新增)

**新增工具类型 | Adding New Tool Types:**
1. **创建工具类**: 在 `tools` 包下创建新的工具类
2. **使用注解标注**: 使用 `@Tool` 标注方法，描述工具功能
3. **参数描述**: 使用 `@ToolParam` 标注参数，提供清晰的参数说明
4. **异常处理**: 实现完整的异常处理和错误信息返回
5. **注册工具**: 在 `ToolRegistration.java` 中注册新工具

**工具开发最佳实践 | Tool Development Best Practices:**
1. **功能单一**: 每个工具应专注于单一功能，避免过于复杂
2. **参数验证**: 对输入参数进行适当的验证和清理
3. **路径安全**: 使用统一的文件路径管理，避免路径遍历攻击
4. **资源管理**: 正确管理文件句柄和网络连接等资源
5. **错误信息**: 返回清晰、有用的错误信息帮助用户理解问题

**工具测试策略 | Tool Testing Strategy:**
1. **单元测试**: 为每个工具类编写单元测试，覆盖正常和异常情况
2. **集成测试**: 在 `LoveAppTest.doChatWithTools()` 中测试工具调用流程
3. **文件系统测试**: 验证文件创建、读写和目录管理功能
4. **边界条件**: 测试大文件、特殊字符、无效URL等边界情况

**当前已实现工具 | Currently Implemented Tools:**
- **FileOperationTool**: 文件读写操作，支持UTF-8编码
- **ResourceDownloadTool**: HTTP资源下载，基于Hutool HTTP客户端
- **PDFGenerationTool**: PDF文档生成，支持中文字体显示

### 扩展 Advisor 功能 | Extending Advisor Functionality
1. 实现 `CallAroundAdvisor` 或 `StreamAroundAdvisor` 接口
2. 重写 `getName()`, `getOrder()`, `aroundCall()` 方法
3. 在 LoveApp 构建 ChatClient 时添加到 `defaultAdvisors()`

### 对话记忆存储扩展 | Extending Chat Memory Storage
1. 实现 `ChatMemory` 接口
2. 重写 `add()`, `get()`, `clear()` 方法
3. 在 LoveApp 构造器中替换 ChatMemory 实现

### RAG 功能扩展 | Extending RAG Capabilities

**文档处理扩展 | Document Processing Extension:**
1. **新增文档类型**: 扩展 LoveAppDocumentLoader 支持 PDF、TXT 等格式
2. **自定义元数据**: 修改 MarkdownDocumentReaderConfig 添加业务相关元数据
3. **文档预处理**: 在 MyKeywordEnricher 中添加文档清洗和标准化逻辑

**向量存储扩展 | Vector Store Extension:**
1. **数据库向量存储**: 使用 PgVectorVectorStoreConfig 配置 PostgreSQL + pgvector
2. **向量存储切换**: 在 LoveAppVectorStoreConfig 中配置不同存储后端
3. **向量索引优化**: 调整嵌入维度、相似度算法等参数

**查询处理扩展 | Query Processing Extension:**
1. **查询重写策略**: 在 QueryRewriter 中自定义重写规则和提示词
2. **多查询扩展**: 调整 MultiQueryExpanderDemo 的查询生成策略
3. **上下文增强**: 实现 LoveAppContextualQueryAugmenterFactory 的业务逻辑

**检索策略扩展 | Retrieval Strategy Extension:**
1. **混合检索**: 结合关键词检索和向量检索
2. **检索后排序**: 基于业务规则对检索结果重新排序
3. **检索缓存**: 实现查询结果缓存机制提升性能

### 配置管理 | Configuration Management
- 敏感信息 (如 API 密钥) 配置在 `application-local.yml`
- 公共配置放在 `application.yml`
- 使用 Spring Profiles 进行环境隔离
- RAG 相关配置: 向量维度、检索数量、相似度阈值等

### 测试策略 | Testing Strategy
- 单元测试位于 `src/test/java/` 对应包结构下
- 集成测试可以使用 `@SpringBootTest` 注解
- AI 调用测试建议使用 Mock 或测试专用 API 密钥
- **RAG 测试**: 使用 PgVectorVectorStoreConfigTest 和 MultiQueryExpanderDemoTest 作为参考
- **工具调用测试**: 使用 FileOperationToolTest, ResourceDownloadToolTest, PDFGenerationToolTest 作为参考

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
5. **RAG 文档管理**: 
   - 知识库文档位于 `src/main/resources/document/`，支持 Markdown 格式
   - 文档命名规范: 文件名倒数第3和第2个字符作为状态标签 (如 "恋爱篇.md" 中的 "恋爱")
   - 向量存储初始化时会自动加载和索引所有文档
6. **向量存储**: 
   - SimpleVectorStore 为内存存储，重启后数据丢失
   - 生产环境建议使用 PgVector 持久化存储
7. **DashScope 配置**: 
   - 需要有效的 API 密钥用于嵌入模型和云端知识库
   - 云端知识库需要预先在 DashScope 控制台创建 "恋爱大师" 索引
8. **性能优化**: 
   - 关键词增强会调用 LLM，建议合理设置关键词数量 (默认5个)
   - 向量存储加载可能耗时，建议实施懒加载或缓存策略
9. **工具调用安全**: (新增)
   - 文件操作仅限于 `tmp/` 目录下，避免访问系统敏感文件
   - 资源下载需要验证URL安全性，避免内网地址访问
   - PDF生成使用内置字体，避免字体文件路径遍历
   - 工具执行结果可能包含敏感信息，注意日志记录的安全性
10. **文件系统管理**: (新增)
   - 工具生成的文件保存在 `tmp/` 目录下，按工具类型分目录
   - 生产环境应定期清理临时文件，防止磁盘空间不足
   - 建议设置文件大小和数量限制，防止恶意用户滥用