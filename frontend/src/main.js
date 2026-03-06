import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

import App from './App.vue'

// 创建 Vue 应用实例
const app = createApp(App)

// 注册 Element Plus
app.use(ElementPlus)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 主题初始化
function initTheme() {
  // 从 localStorage 读取保存的主题，默认为暗色主题
  const savedTheme = localStorage.getItem('theme') || 'dark'
  const html = document.documentElement

  if (savedTheme === 'dark') {
    html.classList.add('dark')
  } else {
    html.classList.add('light')
  }
}

// 应用主题样式
const style = document.createElement('style')
style.textContent = `
  /* CSS 变量定义 */
  :root {
    /* 暗色主题变量 */
    --primary-color: #7c3aed;
    --primary-light: #a78bfa;
    --primary-dark: #5b21b6;

    --bg-primary: #1e1e2e;
    --bg-secondary: #2d2d3a;
    --bg-tertiary: #3a3a4a;
    --bg-elevated: #252532;

    --text-primary: #f2f2f7;
    --text-secondary: #b8b8c8;
    --text-tertiary: #8e8e9e;
    --text-disabled: #5a5a6a;

    --border-color: #3d3d4d;
    --border-light: #4a4a5a;
    --border-focus: #7c3aed;

    --success-color: #4ade80;
    --warning-color: #fbbf24;
    --error-color: #f87171;
    --info-color: #60a5fa;

    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.4);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.5);
  }

  /* 亮色主题变量 */
  .light {
    --bg-primary: #ffffff;
    --bg-secondary: #f9fafb;
    --bg-tertiary: #f3f4f6;
    --bg-elevated: #ffffff;

    --text-primary: #111827;
    --text-secondary: #4b5563;
    --text-tertiary: #9ca3af;
    --text-disabled: #d1d5db;

    --border-color: #e5e7eb;
    --border-light: #d1d5db;

    --success-color: #10b981;
    --warning-color: #f59e0b;
    --error-color: #ef4444;
    --info-color: #3b82f6;

    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.15);
  }

  /* 全局样式 */
  html, body {
    margin: 0;
    padding: 0;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    background: var(--bg-primary);
    color: var(--text-primary);
    transition: background-color 0.3s ease, color 0.3s ease;
  }

  /* Element Plus 组件样式覆盖 */
  .el-button {
    transition: all 0.2s;
  }

  .el-card {
    background: var(--bg-secondary);
    border-color: var(--border-color);
    transition: all 0.2s;
  }

  .el-card__body {
    color: var(--text-primary);
  }

  .el-input__wrapper {
    background: var(--bg-secondary);
    border-color: var(--border-color);
    box-shadow: none !important;
  }

  .el-input__inner {
    color: var(--text-primary);
  }

  .el-input__inner::placeholder {
    color: var(--text-tertiary);
  }

  .el-textarea__inner {
    background: var(--bg-secondary);
    border-color: var(--border-color);
    color: var(--text-primary);
  }

  .el-textarea__inner::placeholder {
    color: var(--text-tertiary);
  }

  .el-select-dropdown__item {
    color: var(--text-primary);
  }

  .el-select-dropdown__item.hover,
  .el-select-dropdown__item:hover {
    background: var(--bg-tertiary);
  }

  .el-dialog {
    background: var(--bg-elevated);
  }

  .el-dialog__title {
    color: var(--text-primary);
  }

  .el-dialog__body {
    color: var(--text-primary);
  }

  .el-empty__description {
    color: var(--text-secondary);
  }

  .el-tag {
    border-color: var(--border-color);
  }

  .el-message {
    background: var(--bg-elevated);
    border-color: var(--border-color);
    color: var(--text-primary);
    box-shadow: var(--shadow-lg);
  }

  .el-message--success {
    background: var(--bg-elevated);
    border-left: 4px solid var(--success-color);
  }

  .el-message--warning {
    background: var(--bg-elevated);
    border-left: 4px solid var(--warning-color);
  }

  .el-message--error {
    background: var(--bg-elevated);
    border-left: 4px solid var(--error-color);
  }

  .el-notification {
    background: var(--bg-elevated);
    border-color: var(--border-color);
    color: var(--text-primary);
  }

  .el-notification__title {
    color: var(--text-primary);
  }

  .el-notification__content {
    color: var(--text-secondary);
  }

  /* 滚动条样式 */
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  ::-webkit-scrollbar-track {
    background: var(--bg-secondary);
    border-radius: 4px;
  }

  ::-webkit-scrollbar-thumb {
    background: var(--text-tertiary);
    border-radius: 4px;
    transition: background 0.2s;
  }

  ::-webkit-scrollbar-thumb:hover {
    background: var(--text-secondary);
  }
`

document.head.appendChild(style)

// 初始化主题
initTheme()

// 挂载应用
app.mount('#app')
