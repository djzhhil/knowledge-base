<template>
  <div class="sidebar" :class="{ collapsed }">
    <!-- 侧边栏头部 -->
    <div class="sidebar-header">
      <span v-if="!collapsed" class="sidebar-title">导航</span>
      <el-button
        link
        :icon="collapsed ? Expand : Fold"
        @click="$emit('toggleCollapse')"
        class="collapse-btn"
      />
    </div>

    <el-scrollbar class="sidebar-content">
      <!-- 分类树 -->
      <div class="sidebar-section">
        <div class="section-title" v-if="!collapsed">
          <el-icon><Folder /></el-icon>
          分类
        </div>
        <el-tree
          :data="categories"
          :props="treeProps"
          :highlight-current="true"
          :default-expand-all="true"
          :expand-on-click-node="false"
          node-key="id"
          @node-click="handleCategoryClick"
          :class="{ 'tree-collapsed': collapsed }"
        >
          <template #default="{ node, data }">
            <span class="tree-node">
              <el-icon v-if="!collapsed"><Folder /></el-icon>
              <span v-if="!collapsed" class="node-label">{{ node.label }}</span>
              <el-badge
                v-if="!collapsed"
                :value="data.noteCount || 0"
                class="tree-badge"
                type="info"
              />
            </span>
          </template>
        </el-tree>
      </div>

      <!-- 标签云 -->
      <div class="sidebar-section" v-if="!collapsed">
        <div class="section-title">
          <el-icon><PriceTag /></el-icon>
          标签
        </div>
        <div class="tag-cloud">
          <el-tag
            v-for="tag in tags.slice(0, 10)"
            :key="tag.id"
            :type="getTagType(tag.noteCount)"
            :effect="selectedTagId === tag.id ? 'dark' : 'plain'"
            @click="handleTagClick(tag.id)"
            class="tag-cloud-tag"
          >
            {{ tag.name }} ({{ tag.noteCount }})
          </el-tag>
        </div>
      </div>

      <!-- 最近访问 -->
      <div class="sidebar-section" v-if="!collapsed">
        <div class="section-title">
          <el-icon><Clock /></el-icon>
          最近访问
        </div>
        <el-timeline>
          <el-timeline-item
            v-for="note in recentNotes.slice(0, 5)"
            :key="note.id"
            :timestamp="formatTime(note.updatedAt)"
            placement="top"
            class="recent-note"
          >
            <div @click="handleRecentNoteClick(note.id)" class="recent-note-title">
              {{ note.title }}
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-scrollbar>

    <!-- 侧边栏底部 -->
    <div class="sidebar-footer">
      <div v-if="!collapsed" class="stats">
        <el-statistic title="笔记总数" :value="noteCount" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  Folder, Fold, Expand, PriceTag, Clock
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  },
  categories: {
    type: Array,
    default: () => []
  },
  tags: {
    type: Array,
    default: () => []
  },
  recentNotes: {
    type: Array,
    default: () => []
  },
  noteCount: {
    type: Number,
    default: 0
  }
})

// Emits
const emit = defineEmits(['selectCategory', 'selectTag', 'selectRecent', 'toggleCollapse'])

// 响应式数据
const selectedTagId = ref(null)
const treeProps = {
  children: 'children',
  label: 'label'
}

// 处理分类点击
const handleCategoryClick = (data) => {
  emit('selectCategory', data.id)
}

// 处理标签点击
const handleTagClick = (tagId) => {
  selectedTagId.value = tagId
  emit('selectTag', tagId)
}

// 处理最近笔记点击
const handleRecentNoteClick = (noteId) => {
  emit('selectRecent', noteId)
}

// 获取标签类型（根据笔记数量）
const getTagType = (count) => {
  const noteCount = count || 0
  if (noteCount >= 20) return 'danger'
  if (noteCount >= 10) return 'warning'
  if (noteCount >= 5) return 'success'
  return 'info'
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.sidebar {
  width: 240px;
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 64px;
}

/* 侧边栏头部 */
.sidebar-header {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.collapse-btn {
  --el-button-text-color: var(--text-secondary);
}

/* 侧边栏内容 */
.sidebar-content {
  flex: 1;
  padding: 16px 0;
}

/* 侧边栏区块 */
.sidebar-section {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 16px;
  margin-bottom: 12px;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 分类树 */
:deep(.el-tree) {
  background: transparent;
  color: var(--text-primary);
}

:deep(.el-tree-node__content) {
  height: 36px;
  color: var(--text-primary);
}

:deep(.el-tree-node__content:hover) {
  background: var(--bg-tertiary);
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: var(--primary-color);
  color: #fff;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.node-label {
  flex: 1;
}

.tree-badge :deep(.el-badge__content) {
  background: var(--primary-color);
}

.collapsed .tree-collapsed :deep(.el-tree-node__content) {
  justify-content: center;
  padding: 0;
}

/* 标签云 */
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 0 16px;
}

.tag-cloud-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.tag-cloud-tag:hover {
  transform: scale(1.05);
}

/* 最近访问 */
:deep(.el-timeline) {
  padding-left: 16px;
}

.recent-note {
  cursor: pointer;
}

.recent-note-title {
  font-size: 14px;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.recent-note-title:hover {
  color: var(--primary-color);
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--border-color);
}

.stats {
  --el-statistic-content-color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 56px;
    z-index: 1000;
    transform: translateX(-100%);
  }

  .sidebar.open {
    transform: translateX(0);
  }
}
</style>
