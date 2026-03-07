<template>
  <div class="note-list-panel">
    <!-- 面板头部 -->
    <div class="panel-header">
      <h3 class="panel-title">{{ title }}</h3>
      <div class="header-actions">
        <!-- 视图切换 -->
        <el-button-group>
          <el-button
            :type="viewMode === 'list' ? 'primary' : ''"
            :icon="List"
            @click="handleViewModeChange('list')"
          />
          <el-button
            :type="viewMode === 'grid' ? 'primary' : ''"
            :icon="Grid"
            @click="handleViewModeChange('grid')"
          />
        </el-button-group>

        <!-- 排序 -->
        <el-select
          :model="sortBy"
          placeholder="排序"
          @change="handleSortChange"
          class="sort-select"
        >
          <el-option label="按时间" value="time" />
          <el-option label="按标题" value="title" />
          <el-option label="按热度" value="heat" />
        </el-select>
      </div>
    </div>

    <!-- 笔记列表 -->
    <el-scrollbar class="list-content">
      <div v-if="!loading && sortedNotes.length > 0" class="notes-container" :class="viewMode">
        <NoteCard
          v-for="note in sortedNotes"
          :key="note.id"
          :note="note"
          :selected="selectedNoteId === note.id"
          :view-mode="viewMode"
          @select="handleSelectNote"
          @star="handleStarNote"
          @delete="handleDeleteNote"
          @move="handleMoveNote"
          @export="handleExportNote"
        />
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else-if="!loading"
        description="暂无笔记"
        :image-size="100"
        class="empty-state"
      >
        <el-button type="primary" :icon="Plus" @click="$emit('newNote')">
          创建第一条笔记
        </el-button>
      </el-empty>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
    </el-scrollbar>

    <!-- 批量操作栏 -->
    <div v-if="selectedNotes.length > 0" class="batch-actions">
      <span class="selected-count">已选择 {{ selectedNotes.length }} 项</span>
      <el-button-group>
        <el-button :icon="Delete" @click="handleBatchDelete">删除</el-button>
        <el-button :icon="FolderOpened" @click="handleBatchMove">移动</el-button>
      </el-button-group>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  List, Grid, Plus, Delete, FolderOpened
} from '@element-plus/icons-vue'
import NoteCard from './NoteCard.vue'

// Props
const props = defineProps({
  notes: {
    type: Array,
    default: () => []
  },
  selectedNoteId: {
    type: Number,
    default: null
  },
  viewMode: {
    type: String,
    default: 'list',
    validator: (value) => ['list', 'grid'].includes(value)
  },
  sortBy: {
    type: String,
    default: 'time',
    validator: (value) => ['time', 'title', 'heat'].includes(value)
  },
  loading: {
    type: Boolean,
    default: false
  },
  filter: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits([
  'selectNote',
  'starNote',
  'deleteNote',
  'moveNote',
  'exportNote',
  'newNote',
  'viewModeChange',
  'sortByChange',
  'batchDelete',
  'batchMove'
])

// 响应式数据
const selectedNotes = ref([])

// 面板标题
const title = computed(() => {
  const { category, tag, keyword } = props.filter
  if (category) return '分类笔记'
  if (tag) return '标签笔记'
  if (keyword) return '搜索结果'
  return '所有笔记'
})

// 排序后的笔记列表
const sortedNotes = computed(() => {
  const notes = [...props.notes]

  switch (props.sortBy) {
    case 'time':
      return notes.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
    case 'title':
      return notes.sort((a, b) => a.title.localeCompare(b.title))
    case 'heat':
      return notes.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    default:
      return notes
  }
})

// 处理选择笔记
const handleSelectNote = (id) => {
  emit('selectNote', id)
}

// 处理星标笔记
const handleStarNote = (id, isStarred) => {
  emit('starNote', id, isStarred)
}

// 处理删除笔记
const handleDeleteNote = (id) => {
  emit('deleteNote', id)
}

// 处理移动笔记
const handleMoveNote = (id) => {
  emit('moveNote', id)
}

// 处理导出笔记
const handleExportNote = (id) => {
  emit('exportNote', id)
}

// 处理视图切换
const handleViewModeChange = (mode) => {
  emit('viewModeChange', mode)
}

// 处理排序变更
const handleSortChange = (value) => {
  emit('sortByChange', value)
}

// 处理批量删除
const handleBatchDelete = () => {
  emit('batchDelete', selectedNotes.value)
  selectedNotes.value = []
}

// 处理批量移动
const handleBatchMove = () => {
  emit('batchMove', selectedNotes.value)
  selectedNotes.value = []
}
</script>

<style scoped>
.note-list-panel {
  width: 320px;
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
  border-right: 1px solid var(--border-color);
}

/* 面板头部 */
.panel-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-color);
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-select {
  width: 100px;
}

/* 笔记列表内容 */
.list-content {
  flex: 1;
  padding: 12px;
}

.notes-container.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.notes-container.list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

/* 加载状态 */
.loading-state {
  padding: 20px;
}

/* 批量操作栏 */
.batch-actions {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: var(--bg-elevated);
  border-top: 1px solid var(--border-color);
}

.selected-count {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .note-list-panel {
    width: 100%;
  }

  .header-actions {
    gap: 8px;
  }

  .sort-select {
    width: 80px;
  }

  .notes-container.grid {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .note-list-panel {
    width: 280px;
  }
}
</style>
