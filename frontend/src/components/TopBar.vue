<template>
  <div class="top-bar">
    <!-- Logo 区域 -->
    <div class="logo" @click="$emit('home')">
      <span class="logo-icon">🐕</span>
      <span class="logo-text">Knowledge Base</span>
    </div>

    <!-- 搜索框 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索笔记..."
        :prefix-icon="Search"
        @input="handleSearch"
        @keyup.enter="handleSearch"
        clearable
        class="search-input"
      />
    </div>

    <!-- 操作按钮 -->
    <div class="actions">
      <el-button
        type="primary"
        :icon="Plus"
        @click="$emit('newNote')"
        class="action-btn"
      >
        新建笔记
      </el-button>

      <el-switch
        v-model="isDarkMode"
        inline-prompt
        :active-icon="Moon"
        :inactive-icon="Sunny"
        @change="handleThemeToggle"
        class="theme-switch"
      />

      <el-dropdown @command="handleUserCommand">
        <el-avatar :size="36" class="user-avatar">
          {{ userName?.[0] || 'U' }}
        </el-avatar>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人资料
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              设置
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Search, Plus, Moon, Sunny, User, Setting, SwitchButton
} from '@element-plus/icons-vue'

// Props
defineProps({
  userName: {
    type: String,
    default: 'User'
  }
})

// Emits
const emit = defineEmits(['search', 'newNote', 'toggleTheme', 'home'])

// 响应式数据
const searchQuery = ref('')
const isDarkMode = ref(false)

// 处理搜索
const handleSearch = () => {
  emit('search', searchQuery.value)
}

// 处理主题切换
const handleThemeToggle = (val) => {
  emit('toggleTheme', val ? 'dark' : 'light')
}

// 处理用户菜单命令
const handleUserCommand = (command) => {
  console.log('User command:', command)
  // TODO: 实现用户菜单功能
}

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  isDarkMode.value = savedTheme === 'dark'
})
</script>

<style scoped>
.top-bar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 搜索框 */
.search-bar {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.search-input {
  --el-input-bg-color: var(--bg-secondary);
  --el-input-border-color: var(--border-color);
  --el-input-text-color: var(--text-primary);
  --el-input-placeholder-color: var(--text-tertiary);
}

/* 操作按钮区域 */
.actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-btn {
  --el-button-bg-color: var(--primary-color);
  --el-button-border-color: var(--primary-color);
  --el-button-text-color: #fff;
}

/* 主题切换开关 */
.theme-switch {
  --el-switch-on-color: var(--primary-color);
  --el-switch-off-color: var(--border-color);
}

.theme-switch :deep(.el-switch__action) {
  color: var(--text-primary);
}

/* 用户头像 */
.user-avatar {
  cursor: pointer;
  background: var(--primary-color);
  color: #fff;
  transition: transform 0.2s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar {
    margin: 0 20px;
  }

  .logo-text {
    display: none;
  }

  .search-input {
    width: 150px;
  }
}
</style>
