<template>
  <div class="note-editor" v-if="note">
    <!-- 编辑器工具栏 -->
    <div class="editor-toolbar">
      <!-- 左侧：视图切换 -->
      <el-button-group>
        <el-button
          :type="editorMode === 'edit' ? 'primary' : ''"
          :icon="Edit"
          @click="editorMode = 'edit'"
          size="small"
        >
          编辑
        </el-button>
        <el-button
          :type="editorMode === 'split' ? 'primary' : ''"
          :icon="View"
          @click="editorMode = 'split'"
          size="small"
        >
          分栏
        </el-button>
        <el-button
          :type="editorMode === 'preview' ? 'primary' : ''"
          :icon="Reading"
          @click="editorMode = 'preview'"
          size="small"
        >
          预览
        </el-button>
      </el-button-group>

      <!-- 中间：分类和标签 -->
      <div class="toolbar-center">
        <el-select
          v-model="note.categoryId"
          placeholder="选择分类"
          size="small"
          clearable
          class="category-select"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>

        <el-select
          v-model="selectedTags"
          multiple
          placeholder="添加标签"
          size="small"
          collapse-tags
          class="tag-select"
        >
          <el-option
            v-for="tag in tags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          />
        </el-select>
      </div>

      <!-- 右侧：操作按钮 -->
      <el-button-group>
        <el-tooltip content="星标" placement="bottom">
          <el-button
            :icon="note.isStarred ? StarFilled : Star"
            @click="handleStar"
            size="small"
            :class="{ 'starred': note.isStarred }"
          />
        </el-tooltip>

        <el-tooltip content="导入 MD" placement="bottom">
          <el-button :icon="Upload" @click="showUpload = true" size="small" />
        </el-tooltip>

        <el-tooltip content="导出" placement="bottom">
          <el-button :icon="Download" @click="handleExport" size="small" />
        </el-tooltip>

        <el-tooltip content="删���" placement="bottom">
          <el-button :icon="Delete" @click="handleDelete" size="small" />
        </el-tooltip>

        <el-tooltip content="全屏" placement="bottom">
          <el-button :icon="isFullscreen ? FullScreen : FullScreen" @click="toggleFullscreen" size="small" />
        </el-tooltip>

        <el-button
          type="primary"
          :icon="Check"
          @click="handleSave"
          size="small"
          :loading="saving"
        >
          保存
        </el-button>
      </el-button-group>
    </div>

    <!-- 编辑器内容 -->
    <div class="editor-content" :class="{ fullscreen: isFullscreen }">
      <mavon-editor
        v-model="noteContent"
        :toolbars="toolbars"
        :subfield="editorMode === 'split'"
        :preview="editorMode === 'preview'"
        :editable="editorMode === 'edit'"
        :toolbars-flag="editorMode !== 'preview'"
        :boxShadow="false"
        :ishljs="true"
        @imgAdd="handleImageAdd"
        @save="handleAutoSave"
        ref="editorRef"
        class="markdown-editor"
      />
    </div>

    <!-- 编辑器底部 -->
    <div class="editor-footer">
      <div class="footer-left">
        <span class="footer-item">
          <el-icon><Document /></el-icon>
          字数：{{ wordCount }}
        </span>
        <span class="footer-item">
          <el-icon><Reading /></el-icon>
          预计阅读：{{ readingTime }} 分钟
        </span>
      </div>

      <div class="footer-center">
        <el-tag
          v-if="saveStatus === 'saved'"
          type="success"
          size="small"
          effect="plain"
        >
          已保存
        </el-tag>
        <el-tag
          v-else-if="saveStatus === 'saving'"
          type="warning"
          size="small"
          effect="plain"
        >
          保存中...
        </el-tag>
        <el-tag
          v-else-if="saveStatus === 'unsaved'"
          type="info"
          size="small"
          effect="plain"
        >
          未保存
        </el-tag>
        <el-tag
          v-else-if="saveStatus === 'error'"
          type="danger"
          size="small"
          effect="plain"
        >
          保存失败
        </el-tag>
      </div>

      <div class="footer-right">
        <span class="footer-item">
          <el-icon><Clock /></el-icon>
          {{ formatTime(note.updatedAt) }}
        </span>
      </div>
    </div>

    <!-- 文件上传对话框 -->
    <UploadDialog
      v-model="showUpload"
      @upload="handleFileUpload"
    />
  </div>

  <!-- 空状态 -->
  <el-empty
    v-else
    description="选择或创建笔记开始编辑"
    :image-size="120"
    class="editor-empty"
  >
    <el-button type="primary" :icon="Plus" @click="$emit('newNote')">
      创建新笔记
    </el-button>
  </el-empty>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import {
  Edit, View, Reading, Star, StarFilled, Upload, Download,
  Delete, FullScreen, Check, Document, Clock, Plus
} from '@element-plus/icons-vue'
import UploadDialog from './UploadDialog.vue'

// Props
const props = defineProps({
  note: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  },
  tags: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits([
  'save',
  'delete',
  'star',
  'newNote',
  'close'
])

// 响应式数据
const editorRef = ref(null)
const noteContent = ref('')
const editorMode = ref('split') // edit, split, preview
const isFullscreen = ref(false)
const saving = ref(false)
const saveStatus = ref('saved') // saved, saving, unsaved, error
const selectedTags = ref([])
const showUpload = ref(false)
let autoSaveTimer = null

// mavon-editor 工具栏配置
const toolbars = {
  bold: true,
  italic: true,
  header: true,
  underline: true,
  strikethrough: true,
  mark: true,
  superscript: true,
  subscript: true,
  quote: true,
  ol: true,
  ul: true,
  link: true,
  imagelink: true,
  code: true,
  table: true,
  htmlcode: true,
  help: true,
  undo: true,
  redo: true,
  trash: true,
  save: true,
  navigation: true,
  alignleft: true,
  aligncenter: true,
  alignright: true,
  subfield: true,
  preview: true
}

// 字数统计
const wordCount = computed(() => {
  if (!noteContent.value) return 0
  return noteContent.value.replace(/\s/g, '').length
})

// 阅读时间（按 400 字/分钟计算）
const readingTime = computed(() => {
  return Math.ceil(wordCount.value / 400)
})

// 监听笔记内容变化
watch(() => props.note, (newNote) => {
  if (newNote) {
    noteContent.value = newNote.content || ''
    selectedTags.value = newNote.tags?.map(t => t.id) || []
  } else {
    noteContent.value = ''
    selectedTags.value = []
  }
}, { immediate: true, deep: true })

// 监听内容变化触发自动保存
watch(noteContent, () => {
  saveStatus.value = 'unsaved'
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(() => {
    handleAutoSave()
  }, 5000)
})

// 处理保存
const handleSave = async () => {
  saving.value = true
  saveStatus.value = 'saving'

  try {
    await emit('save', {
      ...props.note,
      content: noteContent.value,
      tags: selectedTags.value
    })
    saveStatus.value = 'saved'
    setTimeout(() => saveStatus.value = '', 2000)
  } catch (error) {
    saveStatus.value = 'error'
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 自动保存
const handleAutoSave = () => {
  handleSave()
}

// 处理星标
const handleStar = () => {
  emit('star', props.note.id, !props.note.isStarred)
}

// 处理删除
const handleDelete = () => {
  emit('delete', props.note.id)
}

// 处理导出
const handleExport = () => {
  const blob = new Blob([noteContent.value], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${props.note.title || 'note'}.md`
  link.click()
  URL.revokeObjectURL(url)
}

// 处理图片上传
const handleImageAdd = async (pos, file) => {
  // TODO: 实现图片上传到服务器
  console.log('Image upload:', pos, file)
}

// 处理文件上传
const handleFileUpload = (file) => {
  noteContent.value = file.content
  emit('save', {
    ...props.note,
    title: file.title || props.note.title,
    content: file.content
  })
}

// 切换全屏
const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 键盘快捷键
const handleKeydown = (e) => {
  // Ctrl + S 保存
  if (e.ctrlKey && e.key === 's') {
    e.preventDefault()
    handleSave()
  }
  // Ctrl + W 关闭
  if (e.ctrlKey && e.key === 'w') {
    e.preventDefault()
    emit('close')
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  if (autoSaveTimer) clearTimeout(autoSaveTimer)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.note-editor {
  height: calc(100vh - 56px);
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

/* 编辑器工具栏 */
.editor-toolbar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
}

.toolbar-center {
  display: flex;
  align-items: center;
  gap: 12px;
}

:deep(.el-select__wrapper) {
  background: var(--bg-tertiary);
  box-shadow: none !important;
}

.category-select {
  width: 150px;
}

.tag-select {
  width: 200px;
}

.starred :deep(.el-button__icon) {
  color: #fbbf24;
}

/* 编辑器内容 */
.editor-content {
  flex: 1;
  overflow: hidden;
}

.editor-content.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 10000;
  background: var(--bg-primary);
}

.markdown-editor {
  height: 100%;
  width: 100%;
}

/* mavon-editor 主题适配 */
:deep(.v-note-wrapper) {
  background: var(--bg-primary);
  border: none;
}

:deep(.v-note-show) {
  background: var(--bg-primary);
}

:deep(.v-note-edit) {
  background: var(--bg-primary);
}

:deep(.v-note-op) {
  background: var(--bg-secondary) !important;
  border-bottom: 1px solid var(--border-color) !important;
}

:deep(.v-note-op .op-icon) {
  color: var(--text-secondary);
}

:deep(.v-note-op .op-icon:hover) {
  color: var(--primary-color);
}

:deep(.v-note-edit .content-input) {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

:deep(.v-note-show .markdown-body) {
  color: var(--text-primary);
}

:deep(.v-note-show .markdown-body h1) {
  color: var(--text-primary);
  border-bottom-color: var(--border-color);
}

:deep(.v-note-show .markdown-body pre) {
  background: var(--bg-secondary);
}

:deep(.v-note-show .markdown-body code) {
  background: var(--bg-tertiary);
  color: var(--primary-color);
}

/* 滚动条样式 */
:deep(.v-note-wrapper .v-note-op),
:deep(.v-note-wrapper .v-note-edit),
:deep(.v-note-wrapper .v-note-show) {
  scrollbar-width: thin;
  scrollbar-color: var(--border-color) var(--bg-secondary);
}

:deep(.v-note-wrapper ::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.v-note-wrapper ::-webkit-scrollbar-track) {
  background: var(--bg-secondary);
}

:deep(.v-note-wrapper ::-webkit-scrollbar-thumb) {
  background: var(--border-color);
  border-radius: 4px;
}

/* 编辑器底部 */
.editor-footer {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  font-size: 12px;
  color: var(--text-tertiary);
}

.footer-left,
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 空状态 */
.editor-empty {
  height: calc(100vh - 56px);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .editor-toolbar {
    flex-direction: column;
    height: auto;
    padding: 10px;
    gap: 10px;
  }

  .toolbar-center {
    width: 100%;
  }

  .category-select,
  .tag-select {
    flex: 1;
  }

  :deep(.el-button-group) {
    justify-content: center;
  }

  .editor-footer {
    padding: 0 10px;
  }

  .footer-left,
  .footer-right {
    display: none;
  }
}
</style>
