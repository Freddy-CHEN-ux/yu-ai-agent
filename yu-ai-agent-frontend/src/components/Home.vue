<template>
  <div class="home">
    <div class="container">
      <header class="header">
        <h1 class="title">AI智能助手平台</h1>
        <p class="subtitle">选择你需要的AI应用，开启智能对话体验</p>
      </header>
      
      <main class="main">
        <div class="apps-grid">
          <div class="app-card card" @click="goToLoveChat">
            <div class="app-icon">
              💝
            </div>
            <h3 class="app-title">AI恋爱大师</h3>
            <p class="app-description">
              专业的恋爱咨询助手，帮助你解决情感问题，提供个性化的恋爱建议
            </p>
            <button class="btn btn-primary">开始聊天</button>
          </div>
          
          <!-- 预留其他应用位置 -->
          <div class="app-card card coming-soon">
            <div class="app-icon">
              🚀
            </div>
            <h3 class="app-title">更多应用</h3>
            <p class="app-description">
              更多精彩的AI应用正在开发中，敬请期待...
            </p>
            <button class="btn btn-secondary" disabled>即将推出</button>
          </div>
        </div>
      </main>
      
      <footer class="footer">
        <p>&copy; 2024 AI智能助手平台. 让AI让生活更美好</p>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToLoveChat() {
      this.$router.push('/love-chat')
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.home::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><radialGradient id="g" cx="50%" cy="50%" r="50%"><stop offset="0%" stop-color="white" stop-opacity="0.1"/><stop offset="100%" stop-color="white" stop-opacity="0"/></radialGradient></defs><circle cx="20" cy="20" r="2" fill="white" opacity="0.3"/><circle cx="80" cy="30" r="1" fill="white" opacity="0.4"/><circle cx="40" cy="70" r="1" fill="white" opacity="0.3"/><circle cx="90" cy="80" r="2" fill="white" opacity="0.2"/><circle cx="10" cy="90" r="1" fill="white" opacity="0.4"/></svg>') repeat;
  animation: float 20s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0) translateY(0); }
  100% { transform: translateX(-100px) translateY(-100px); }
}

.container {
  position: relative;
  z-index: 1;
  padding: 40px 20px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  text-align: center;
  margin-bottom: 60px;
  color: white;
}

.title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: -1px;
}

.subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  font-weight: 400;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
  max-width: 800px;
  width: 100%;
}

.app-card {
  padding: 40px 30px;
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 20px;
}

.app-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 1);
}

.coming-soon {
  cursor: default;
  opacity: 0.7;
}

.coming-soon:hover {
  transform: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07);
}

.app-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  display: block;
}

.app-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 15px;
  color: #2d3748;
}

.app-description {
  color: #718096;
  margin-bottom: 30px;
  line-height: 1.6;
  font-size: 1rem;
}

.btn-secondary {
  background: #e2e8f0;
  color: #718096;
  cursor: not-allowed;
}

.footer {
  text-align: center;
  margin-top: 60px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .title {
    font-size: 2.5rem;
  }
  
  .subtitle {
    font-size: 1.1rem;
  }
  
  .apps-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .app-card {
    padding: 30px 20px;
  }
  
  .app-icon {
    font-size: 3rem;
  }
}
</style>