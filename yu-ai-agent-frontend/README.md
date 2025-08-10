# AI智能助手前端项目

基于 Vue3 + Vite 构建的 AI 智能助手前端应用，目前包含 AI 恋爱大师聊天功能。

## 功能特性

- 🏠 **主页导航**: 美观的应用选择界面
- 💝 **AI恋爱大师**: 专业的恋爱咨询聊天室
- 🔄 **实时对话**: 基于 SSE (Server-Sent Events) 的流式对话
- 📱 **响应式设计**: 支持桌面端和移动端
- ⚡ **实时连接状态**: 显示连接状态和输入指示器

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vue Router** - 官方路由管理器
- **Vite** - 现代化前端构建工具
- **Axios** - HTTP 请求库
- **Server-Sent Events** - 实时数据流

## 项目结构

```
yu-ai-agent-frontend/
├── public/                 # 静态资源
├── src/
│   ├── components/         # Vue 组件
│   │   ├── Home.vue       # 主页组件
│   │   └── LoveChat.vue   # 恋爱聊天组件
│   ├── App.vue            # 根组件
│   ├── main.js            # 入口文件
│   └── style.css          # 全局样式
├── index.html             # HTML 模板
├── package.json           # 项目配置
├── vite.config.js         # Vite 配置
└── README.md              # 项目说明
```

## 快速开始

### 环境要求

- Node.js 16.0 或更高版本
- npm 或 yarn

### 安装依赖

\`\`\`bash
npm install
\`\`\`

### 启动开发服务器

\`\`\`bash
npm run dev
\`\`\`

应用将在 http://localhost:5173 启动

### 构建生产版本

\`\`\`bash
npm run build
\`\`\`

### 预览生产构建

\`\`\`bash
npm run preview
\`\`\`

## 后端服务

前端应用需要配合后端 Spring Boot 服务使用:

- **后端服务地址**: http://localhost:8123
- **API 接口**: \`/api/ai/love_app/chat/sse\`
- **通信方式**: Server-Sent Events (SSE)

确保后端服务已启动并运行在 8123 端口。

## 功能说明

### 主页 (Home.vue)

- 展示可用的 AI 应用
- 支持应用切换
- 现代化卡片布局设计

### AI恋爱大师 (LoveChat.vue)

- **聊天界面**: 仿微信聊天室设计
- **实时对话**: 基于 SSE 的流式响应
- **会话管理**: 自动生成唯一会话 ID
- **状态指示**: 连接状态、输入状态显示
- **响应式**: 适配桌面和移动设备

## 核心特性

### SSE 实时通信

```javascript
// 创建 SSE 连接
const url = \`http://localhost:8123/api/ai/love_app/chat/sse?message=\${encodeURIComponent(message)}&chatId=\${this.chatId}\`
this.eventSource = new EventSource(url)

// 监听消息
this.eventSource.onmessage = (event) => {
  if (event.data === '[DONE]') {
    this.eventSource.close()
    return
  }
  // 处理流式数据
  aiMessage += event.data
}
```

### 自动生成会话ID

```javascript
generateChatId() {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}
```

### 消息格式化

- 支持换行符转换
- 时间戳格式化
- 字符数限制 (500字符)

## 样式特色

- **渐变背景**: 现代化视觉效果
- **卡片设计**: Material Design 风格
- **动画效果**: 平滑的过渡和悬停效果
- **输入指示器**: 打字效果动画
- **响应式布局**: 移动设备友好

## 开发说明

### 添加新的 AI 应用

1. 在 \`components\` 目录创建新组件
2. 在 \`main.js\` 中添加路由
3. 在 \`Home.vue\` 中添加应用卡片

### 自定义样式

- 全局样式在 \`src/style.css\`
- 组件样式使用 scoped CSS
- 支持 CSS 变量和媒体查询

### 错误处理

- SSE 连接错误处理
- 网络异常处理  
- 用户输入验证

## 浏览器支持

- Chrome/Edge 88+
- Firefox 84+
- Safari 14+

## 开发建议

1. **开发调试**: 使用浏览器开发者工具监控 SSE 连接
2. **错误处理**: 实现完善的错误提示和重连机制
3. **性能优化**: 适当的防抖和节流处理
4. **用户体验**: 合理的加载状态和反馈

## 许可证

MIT License