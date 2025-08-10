<template>
  <div class="love-chat">
    <div class="chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <button class="back-btn" @click="goBack">
          ← 返回
        </button>
        <div class="chat-info">
          <h2 class="chat-title">💝 AI恋爱大师</h2>
          <p class="chat-subtitle">专业恋爱咨询 • 在线服务</p>
        </div>
        <div class="chat-status">
          <span class="status-indicator" :class="{ 'online': isConnected }"></span>
          {{ isConnected ? '在线' : '离线' }}
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="ai-avatar">💝</div>
          <div class="welcome-content">
            <h3>欢迎来到AI恋爱大师！</h3>
            <p>我是你的专属恋爱顾问，可以帮助你解决各种情感问题。</p>
            <p>请告诉我你遇到的困扰，我会为你提供专业的建议。</p>
          </div>
        </div>

        <!-- 聊天消息列表 -->
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message-item"
          :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
        >
          <div class="message-avatar">
            <span v-if="message.type === 'user'">👤</span>
            <span v-else>💝</span>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
        </div>

        <!-- 正在输入指示器 -->
        <div v-if="isTyping" class="message-item ai-message">
          <div class="message-avatar">
            <span>💝</span>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="input-container">
          <div class="input-group">
            <input
              v-model="currentMessage"
              @keypress.enter="sendMessage"
              @input="handleInput"
              class="form-input"
              type="text"
              placeholder="请输入你的问题..."
              :disabled="isLoading"
              maxlength="500"
            />
            <button
              @click="sendMessage"
              class="btn btn-send"
              :disabled="!currentMessage.trim() || isLoading"
            >
              <span v-if="isLoading">发送中...</span>
              <span v-else>发送</span>
            </button>
          </div>
          <div class="input-footer">
            <span class="char-count">{{ currentMessage.length }}/500</span>
            <span class="chat-id">会话ID: {{ chatId }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoveChat',
  data() {
    return {
      messages: [],
      currentMessage: '',
      chatId: '',
      isLoading: false,
      isTyping: false,
      isConnected: true,
      eventSource: null,
      showRetryOption: false
    }
  },
  mounted() {
    this.initChat()
  },
  beforeUnmount() {
    this.cleanup()
  },
  methods: {
    initChat() {
      // 生成聊天室ID
      this.chatId = this.generateChatId()
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    generateChatId() {
      return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    },

    async sendMessage() {
      if (!this.currentMessage.trim() || this.isLoading) {
        return
      }

      const messageText = this.currentMessage.trim()
      this.currentMessage = ''

      // 添加用户消息
      this.addMessage('user', messageText)

      // 开始加载状态
      this.isLoading = true
      this.isTyping = true

      try {
        await this.sendMessageToAI(messageText)
      } catch (error) {
        console.error('发送消息失败:', error)
        let errorMessage = '抱歉，服务暂时不可用，请稍后再试。'
        
        if (error.message === '连接超时') {
          errorMessage = '响应超时，请检查网络连接或稍后重试。'
        } else if (error.message.includes('连接失败')) {
          errorMessage = '无法连接到服务器，请检查网络连接。'
        }
        
        this.addMessage('ai', errorMessage)
        this.isConnected = false
        
        // 显示重试按钮逻辑可以在这里添加
        this.showRetryOption = true
      } finally {
        this.isLoading = false
        this.isTyping = false
      }
    },

    async sendMessageToAI(message) {
      return new Promise((resolve, reject) => {
        const url = `http://localhost:8123/api/ai/love_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${this.chatId}`
        
        // 关闭之前的连接
        this.cleanup()

        this.eventSource = new EventSource(url)
        
        let aiMessage = ''
        let messageStarted = false
        let resolved = false

        // 设置超时处理
        const timeout = setTimeout(() => {
          if (!resolved) {
            console.warn('SSE connection timeout')
            this.cleanup()
            this.isConnected = false
            this.isTyping = false
            if (!messageStarted) {
              reject(new Error('连接超时'))
            } else {
              resolve()
            }
            resolved = true
          }
        }, 30000) // 30秒超时

        this.eventSource.onopen = (event) => {
          console.log('SSE connection opened:', event)
          console.log('EventSource readyState:', this.eventSource.readyState)
          this.isConnected = true
        }

        this.eventSource.onmessage = (event) => {
          console.log('SSE message received:', event.data)
          console.log('Event type:', event.type)
          console.log('Full event:', event)
          
          if (event.data === '[DONE]') {
            clearTimeout(timeout)
            this.cleanup()
            this.isTyping = false
            if (!resolved) {
              resolved = true
              resolve()
            }
            return
          }

          if (!messageStarted) {
            // 第一次收到消息时添加AI消息占位符
            this.addMessage('ai', '')
            messageStarted = true
            this.isTyping = false
          }

          // 累积消息内容
          aiMessage += event.data
          
          // 更新最后一条AI消息
          const lastMessageIndex = this.messages.length - 1
          if (this.messages[lastMessageIndex] && this.messages[lastMessageIndex].type === 'ai') {
            this.messages[lastMessageIndex].content = aiMessage
          }

          // 滚动到底部
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }

        this.eventSource.onerror = (error) => {
          console.error('SSE connection error:', error)
          console.log('EventSource readyState:', this.eventSource?.readyState)
          console.log('Error event:', error)
          clearTimeout(timeout)
          this.cleanup()
          this.isConnected = false
          this.isTyping = false
          
          if (!resolved) {
            resolved = true
            if (!messageStarted) {
              reject(new Error('连接失败，请检查网络或稍后重试'))
            } else {
              resolve()
            }
          }
        }
      })
    },

    addMessage(type, content) {
      const message = {
        type,
        content,
        timestamp: new Date()
      }
      this.messages.push(message)
      
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    formatMessage(content) {
      // 简单的文本格式化，将换行符转换为<br>
      return content.replace(/\n/g, '<br>')
    },

    formatTime(timestamp) {
      return timestamp.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },

    handleInput() {
      // 可以在这里添加实时输入处理逻辑
    },

    goBack() {
      this.cleanup()
      this.$router.push('/')
    },

    cleanup() {
      if (this.eventSource) {
        try {
          this.eventSource.close()
        } catch (error) {
          console.warn('Error closing EventSource:', error)
        }
        this.eventSource = null
      }
    }
  }
}
</script>

<style scoped>
.love-chat {
  height: 100vh;
  display: flex;
  background: #f0f2f5;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 1000px;
  margin: 0 auto;
  background: white;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
  margin-right: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-info {
  flex: 1;
}

.chat-title {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
}

.chat-subtitle {
  margin: 4px 0 0 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

.chat-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ef4444;
  animation: pulse 2s infinite;
}

.status-indicator.online {
  background: #10b981;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f9fafb;
}

.welcome-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.ai-avatar {
  font-size: 2rem;
}

.welcome-content h3 {
  margin: 0 0 10px 0;
  color: #1f2937;
  font-size: 1.2rem;
}

.welcome-content p {
  margin: 6px 0;
  color: #6b7280;
  line-height: 1.6;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  gap: 12px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #667eea;
}

.ai-message .message-avatar {
  background: #f3f4f6;
}

.message-content {
  max-width: 70%;
  min-width: 120px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
}

.user-message .message-bubble {
  background: #667eea;
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: white;
  color: #1f2937;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 4px;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.7;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #9ca3af;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.chat-input {
  padding: 20px 24px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.input-container {
  max-width: 100%;
}

.input-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.form-input {
  flex: 1;
  padding: 14px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 24px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-send {
  padding: 14px 24px;
  border-radius: 24px;
  white-space: nowrap;
  min-width: 80px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 0.8rem;
  color: #9ca3af;
}

.char-count {
  margin-left: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    margin: 0;
    border-radius: 0;
  }
  
  .chat-header {
    padding: 16px 20px;
  }
  
  .chat-title {
    font-size: 1.2rem;
  }
  
  .chat-messages {
    padding: 16px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .chat-input {
    padding: 16px 20px;
  }
  
  .form-input {
    font-size: 16px; /* 防止iOS缩放 */
  }
  
  .input-footer {
    flex-direction: column;
    gap: 4px;
    align-items: flex-start;
  }
}
</style>