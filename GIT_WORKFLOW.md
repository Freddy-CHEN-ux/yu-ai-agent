# Git 工作流程说明 - MySQL 持久化分支

## 📋 项目分支结构

### 主要分支
- **master**: 主分支，稳定版本
- **feature/mysql-persistence**: MySQL持久化实验分支（基于master创建）

## 🔄 分支切换和基本操作

### 1. 查看当前分支状态
```bash
# 查看当前所在分支
git branch

# 查看所有分支（包括远程）
git branch -a

# 查看远程分支
git branch -r

# 查看当前状态
git status
```

### 2. 分支切换操作
```bash
# 切换到 master 分支
git checkout master

# 切换到 MySQL 持久化分支
git checkout feature/mysql-persistence

# 创建并切换到新分支（如果分支不存在）
git checkout -b feature/mysql-persistence
```

### 3. 确认当前分支
```bash
# 当前分支会有 * 标记
git branch

# 或者查看详细状态
git status
```

## 📝 在 feature/mysql-persistence 分支上的开发流程

### 1. 开始开发前
```bash
# 确保在正确的分支
git checkout feature/mysql-persistence

# 拉取最新的远程更改（如果有协作者）
git pull origin feature/mysql-persistence

# 查看当前状态
git status
```

### 2. 开发过程中的提交
```bash
# 查看修改的文件
git status

# 添加特定文件到暂存区
git add src/main/java/com/yupi/yuaiagent/chatmemory/MySQLChatMemory.java
git add database/init.sql

# 或添加所有修改的文件
git add .

# 提交更改
git commit -m "feat: 实现MySQL聊天记忆持久化功能

- 新增MySQLChatMemory类实现ChatMemory接口
- 添加ChatMessageEntity实体类和Repository
- 支持消息的增删查操作和事务管理
- 新增数据库初始化脚本

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 3. 推送到远程分支
```bash
# 推送当前分支到远程（已设置上游跟踪）
git push

# 如果是第一次推送新分支（已完成，此处仅供参考）
git push -u origin feature/mysql-persistence
```

## ⚠️ 重要的安全操作提醒

### 1. 防止误操作的检查点
```bash
# 🔍 操作前必做：确认当前分支
echo "当前分支: $(git branch --show-current)"

# 🔍 操作前必做：查看将要提交的内容
git diff --staged

# 🔍 推送前必做：确认推送目标
git remote -v
git branch --show-current
```

### 2. 误操作恢复
```bash
# 如果意外切换到了错误分支，立即切回
git checkout feature/mysql-persistence

# 如果意外提交到了错误分支，撤销最后一次提交（保留文件修改）
git reset --soft HEAD~1

# 如果需要丢弃工作区的修改
git checkout -- .

# 如果需要回到某个特定提交
git log --oneline  # 查看提交历史
git checkout <commit-hash>  # 回到指定提交
```

## 🌟 推荐的安全工作流程

### 完整的开发周期
```bash
# 1. 开始工作前
echo "=== 开始 MySQL 持久化功能开发 ==="
git checkout feature/mysql-persistence
echo "当前分支: $(git branch --show-current)"
git status

# 2. 开发完成后
echo "=== 准备提交代码 ==="
git status
git add .
git diff --staged  # 检查将要提交的内容

# 3. 提交代码
git commit -m "你的提交信息"

# 4. 推送到远程
echo "当前分支: $(git branch --show-current)"
git push

echo "=== 开发周期完成 ==="
```

## 📈 分支合并策略（将来使用）

### 当 MySQL 功能开发完成后
```bash
# 1. 确保 feature 分支是最新的
git checkout feature/mysql-persistence
git pull origin feature/mysql-persistence

# 2. 切换到 master 并更新
git checkout master
git pull origin master

# 3. 合并 feature 分支（建议在 GitHub 上创建 Pull Request）
# 在 GitHub 上操作更安全，有代码审查和自动化测试

# 4. 或者本地合并（不推荐）
git merge feature/mysql-persistence
git push origin master
```

## 🚨 紧急情况处理

### 如果分支搞混了
```bash
# 查看所有分支和当前位置
git branch -a
git log --oneline --graph --all

# 强制切换到正确分支（会丢失未提交的修改）
git checkout -f feature/mysql-persistence

# 或者保存当前修改后切换
git stash  # 保存当前修改
git checkout feature/mysql-persistence
git stash pop  # 恢复修改
```

### 联系人信息
- 如果遇到复杂的 Git 问题，可以寻求技术支持
- 重要提醒：在执行任何 `git reset --hard` 或 `git push -f` 操作前，请三思

## 📝 操作记录模板

建议每次操作时记录：
```
日期: ____
操作: ____
分支: ____
结果: ____
备注: ____
```