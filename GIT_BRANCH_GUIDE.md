# Git 分支管理指南

## 项目分支结构

### 主要分支

- **`master`** - 主线开发分支，专注核心恋爱咨询功能
- **`feature/mysql-persistence`** - MySQL持久化实验分支 (提交ID: `3e1d03e`)

## 常用操作命令

### 🔍 查看分支状态

```bash
# 查看所有分支和最新提交
git branch -v

# 查看当前分支状态
git status

# 查看分支提交历史
git log --oneline --graph --all
```

### 🔄 分支切换操作

#### 切换到主线开发
```bash
# 切换到主分支继续核心功能开发
git checkout master

# 确认工作区状态
git status
```

#### 切换到MySQL功能
```bash
# 切换到MySQL持久化分支
git checkout feature/mysql-persistence

# 验证MySQL功能完整性
mvn test -Dtest=MySQLConnectionTest
```

#### 基于MySQL功能创建新分支
```bash
# 基于MySQL分支创建增强版本
git checkout -b feature/mysql-enhanced feature/mysql-persistence

# 或者创建其他实验分支
git checkout -b feature/mysql-optimization feature/mysql-persistence
```

### 📊 分支对比和合并

#### 查看分支差异
```bash
# 查看主分支和MySQL分支的差异
git diff master feature/mysql-persistence

# 只查看文件名差异
git diff --name-only master feature/mysql-persistence

# 查看特定文件的差异
git diff master feature/mysql-persistence -- pom.xml
```

#### 合并操作（谨慎使用）
```bash
# 如果要将MySQL功能合并到主分支
git checkout master
git merge feature/mysql-persistence

# 或者使用rebase（保持历史整洁）
git checkout feature/mysql-persistence
git rebase master
```

### 🔒 安全操作

#### 保存当前工作进度
```bash
# 如果有未提交的更改，先暂存
git stash push -m "临时保存工作进度"

# 切换分支后恢复
git stash pop
```

#### 撤销错误操作
```bash
# 撤销未提交的更改
git restore <file-name>

# 重置到最近提交
git reset --hard HEAD

# 查看操作历史（可恢复误删的分支）
git reflog
```

## 分支功能说明

### Master 分支特性
- **目标**: 恋爱心理咨询核心功能
- **聊天记忆**: 基于文件的持久化（Kryo序列化）
- **存储位置**: `tmp/chat-memory/`
- **AI模型**: DashScope + Ollama双模型支持
- **架构**: Spring AI + ChatClient + Advisor模式

### MySQL分支特性
- **目标**: 数据库持久化实验
- **聊天记忆**: MySQL数据库存储
- **新增组件**:
  - `MySQLChatMemory.java` - MySQL实现
  - `ChatMessageEntity.java` - JPA实体
  - `ChatMessageRepository.java` - 数据访问层
  - `MySQLConnectionTest.java` - 连接测试
- **配置**: Spring Data JPA + MySQL连接
- **数据库**: yu_ai_agent (需要手动创建)

## 项目配置差异

### Master分支配置
```yaml
# application.yml - 主要配置
app:
  chat:
    memory:
      type: file  # 文件存储

# application-local.yml - 敏感信息
spring:
  ai:
    dashscope:
      api-key: sk-xxx
```

### MySQL分支配置
```yaml
# application.yml - 主要配置  
app:
  chat:
    memory:
      type: mysql  # MySQL存储

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# application-local.yml - 敏感信息
spring:
  datasource:
    url: jdbc:mysql://192.168.2.102:3306/yu_ai_agent
    username: root
    password: 123456
```

## 开发工作流建议

### 1. 日常开发流程
```bash
# 1. 确认在主分支
git checkout master
git status

# 2. 拉取最新代码（如果有远程仓库）
git pull origin master

# 3. 开始开发...
# 4. 提交更改
git add .
git commit -m "feat: 新功能描述"
```

### 2. 实验功能开发
```bash
# 1. 基于MySQL分支创建实验分支
git checkout -b feature/new-experiment feature/mysql-persistence

# 2. 进行实验开发...
# 3. 提交实验结果
git add .
git commit -m "experiment: 实验功能描述"

# 4. 回到主分支继续主线开发
git checkout master
```

### 3. 紧急修复流程
```bash
# 1. 保存当前工作
git stash push -m "临时保存"

# 2. 切换到需要修复的分支
git checkout master

# 3. 创建修复分支
git checkout -b hotfix/urgent-fix

# 4. 修复问题并测试
# 5. 提交修复
git add .
git commit -m "fix: 紧急修复描述"

# 6. 合并回主分支
git checkout master
git merge hotfix/urgent-fix

# 7. 恢复之前的工作
git stash pop
```

## 注意事项 ⚠️

1. **配置文件**: 切换分支时注意配置文件差异，特别是数据库配置
2. **依赖差异**: MySQL分支有额外的JPA依赖，可能需要重新编译
3. **数据库**: MySQL分支需要数据库支持，确保数据库服务正常
4. **测试**: 切换分支后建议运行相应测试确保功能正常
5. **备份**: 重要实验结果及时提交到分支避免丢失

## 快速参考

```bash
# 查看当前所在分支
git branch

# 主线开发
git checkout master

# MySQL实验
git checkout feature/mysql-persistence

# 查看分支差异
git diff master feature/mysql-persistence --name-only

# 暂存工作进度
git stash push -m "临时保存"

# 恢复工作进度
git stash pop
```

---
📝 **提示**: 如有疑问或需要添加新的分支管理策略，可以随时更新此文档。