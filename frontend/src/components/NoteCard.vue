<template>
  <el-card
    class="note-card"
    :class="{ selected, [viewMode]: true }"
    @click="$emit('select', note.id)"
    :body-style="{ padding: '16px' }"
    shadow="hover"
  >
    <!-- 卡片封面（仅网格模式） -->
    <div v-if="viewMode === 'grid'" class="card-cover" :style="coverStyle">
      <div class="cover-content">
        <el-icon :size="32"><Document /></el-icon>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="card-body">
      <!-- 标题 -->
      <h3 class="card-title">{{ note.title || '无标题' }}</h3>

      <!-- 摘要 -->
      <p v-if="viewMode === 'grid'" class="card-summary">
        {{ note.summary || (note.content?.slice(0, 120) + '...') }}
      </p>

      <!-- 标签 -->
      <div class="card-tags">
        <el-tag
          v-for="tag in displayTags"
          :key="tag.id"
          size="small"
          :type="getTagType(tag.name)"
          effect="plain"
          class="card-tag"
        >
          {{ tag.name }}
        </el-tag>
        <el-tag v-if="hasMoreTags" size="small" class="card-tag-more">
          +{{ note.tags.length - 3 }}
        </el-tag>
      </div>

      <!-- 底部信息 -->
      <div class="card-footer">
        <span class="card-time">
          <el-icon><Clock /></el-icon>
          {{ formatTime(note.updatedAt) }}
        </span>

        <div class="card-actions">
          <el-button
            link
            :icon="note.isStarred ? StarFilled : Star"
            @click.stop="handleStar"
            :class="{ 'starred': note.isStarred }"
          />
          <el-dropdown @command="handleCommand">
            <el-button link :icon="MoreFilled" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="{ action: 'star' }">
                  <el-icon>{{ note.isStarred ? StarFilled : Star }}</el-icon>
                  {{ note.isStarred ? '取消星标' : '星标' }}
                </el-dropdown-item>
                <el-dropdown-item :command="{ action: 'move' }">
                  <el-icon><FolderOpened /></el-icon>
                  移动
                </el-dropdown-item>
                <el-dropdown-item :command="{ action: 'export' }">
                  <el-icon><Download /></el-icon>
                  导出
                </el-dropdown-item>
                <el-dropdown-item :command="{ action: 'delete' }" divided>
                  <el-icon><Delete /></el-icon>
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import {
  Document, Clock, Star, StarFilled, MoreFilled,
  FolderOpened, Download, Delete
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  note: {
    type: Object,
    required: true
  },
  selected: {
    type: Boolean,
    default: false
  },
  viewMode: {
    type: String,
    default: 'list', // 'list' | 'grid'
    validator: (value) => ['list', 'grid'].includes(value)
  }
})

// Emits
const emit = defineEmits(['select', 'star', 'delete', 'move', 'export'])

// 展示的标签（最多3个）
const displayTags = computed(() => {
  const tags = props.note.tags || []
  return tags.slice(0, 3)
})

// 是否有更多标签
const hasMoreTags = computed(() => {
  return (props.note.tags?.length || 0) > 3
})

// 封面样式（根据标题生成随机颜色）
const coverStyle = computed(() => {
  const colors = [
    '#7c3aed', '#4f46e5', '#3b82f6', '#06b6d4',
    '#10b981', '#f59e0b', '#ef4444', '#ec4899'
  ]
  const index = (props.note.title?.charCodeAt(0) || 0) % colors.length
  return {
    background: colors[index],
    height: '120px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  }
})

// 处理星标
const handleStar = () => {
  emit('star', props.note.id, !props.note.isStarred)
}

// 处理右键菜单命令
const handleCommand = ({ action }) => {
  switch (action) {
    case 'star':
      emit('star', props.note.id, !props.note.isStarred)
      break
    case 'delete':
      emit('delete', props.note.id)
      break
    case 'move':
      emit('move', props.note.id)
      break
    case 'export':
      emit('export', props.note.id)
      break
  }
}

// 获取标签类型
const getTagType = (name) => {
  const types = ['primary', 'success', 'warning', 'info', 'danger']
  const index = name?.charCodeAt(0) % types.length
  return types[index] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes < 1 ? '刚刚' : `${minutes}分钟前`
    }
    return `${hours}小时前`
  }
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.note-card {
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  margin-bottom: 0;
}

.note-card:hover {
  transform: translateY(-2px);
}

.note-card.selected {
  border-color: var(--primary-color);
  background: var(--bg-elevated);
}

/* 列表模式 */
.note-card.list-mode {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

.note-card.list-mode .card-cover {
  display: none;
}

.note-card.list-mode .card-body {
  flex: 1;
}

.note-card.list-mode .card-title {
  font-size: 16px;
  margin-bottom: 4px;
}

.note-card.list-mode .card-summary {
  display: none;
}

.note-card.list-mode .card-tags {
  margin-bottom: 6px;
}

/* 网格模式 */
.note-card.grid-mode {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-cover {
  border-radius: 8px 8px 0 0;
  color: #fff;
}

.cover-content {
  opacity: 0.9;
}

/* 卡片主体 */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card:hover .card-title {
  color: var(--primary-color);
}

.card-summary {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.card-tag,
.card-tag-more {
  --el-tag-bg-color: var(--bg-tertiary);
  --el-tag-border-color: var(--border-color);
  --el-tag-text-color: var(--text-secondary);
  font-size: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid var(--border-color);
}

.card-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-tertiary);
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-actions :deep(.el-button) {
  --el-button-text-color: var(--text-tertiary);
  padding: 4px;
}

.card-actions :deep(.el-button:hover) {
  --el-button-text-color: var(--text-primary);
}

.starred :deep(.el-button) {
  --el-button-text-color: #fbbf24 !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note-card.grid-mode {
    display: none;
  }

  .note-card.list-mode {
    padding: 10px;
  }

  .card-title {
    font-size: 14px;
  }

  .card-tags {
    display: none;
  }
}
</style>
