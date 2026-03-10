<template>
  <div id="app" :class="{ dark: isDarkMode }">
    <!-- 顶部导航栏 -->
    <TopBar
      :user-name="userName"
      @search="handleSearch"
      @new-note="handleNewNote"
      @toggle-theme="handleThemeToggle"
      @home="handleHome"
    />

    <!-- 主布局 -->
    <div class="main-layout">
      <!-- 左侧边栏 -->
      <Sidebar
        :collapsed="sidebarCollapsed"
        :categories="categories"
        :tags="tags"
        :recent-notes="recentNotes"
        :note-count="notes.length"
        @select-category="handleSelectCategory"
        @select-tag="handleSelectTag"
        @select-recent="handleSelectRecent"
        @toggle-collapse="handleToggleSidebar"
      />

      <!-- 中间列表面板 -->
      <NoteList
        :notes="filteredNotes"
        :selected-note-id="selectedNoteId"
        :view-mode="viewMode"
        :sort-by="sortBy"
        :loading="loading"
        :filter="currentFilter"
        @select-note="handleSelectNote"
        @star-note="handleStarNote"
        @delete-note="handleDeleteNote"
        @move-note="handleMoveNote"
        @export-note="handleExportNote"
        @new-note="handleNewNote"
        @view-mode-change="handleViewModeChange"
        @sort-by-change="handleSortByChange"
        @batch-delete="handleBatchDelete"
        @batch-move="handleBatchMove"
      />

      <!-- 右侧编辑器 -->
      <NoteEditor
        :note="currentNote"
        :categories="categories"
        :tags="tags"
        @save="handleSaveNote"
        @delete="handleDeleteNote"
        @star="handleStarNote"
        @new-note="handleNewNote"
        @close="handleCloseEditor"
      />
    </div>

    <!-- 全局消息提示 -->
    <el-config-provider :locale="zhCn">
      <slot></slot>
    </el-config-provider>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElConfigProvider, ElMessage, ElMessageBox } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 导入组件
import TopBar from './components/TopBar.vue'
import Sidebar from './components/Sidebar.vue'
import NoteList from './components/NoteList.vue'
import NoteEditor from './components/NoteEditor.vue'

// 导入 API
import api from './api'

// 响应式数据
const userName = ref('User')
const isDarkMode = ref(localStorage.getItem('theme') !== 'light')
const sidebarCollapsed = ref(false)
const notes = ref([])
const categories = ref([])
const tags = ref([])
const recentNotes = ref([])
const selectedNoteId = ref(null)
const currentNote = ref(null)
const viewMode = ref('list')
const sortBy = ref('time')
const loading = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref(null)
const selectedTag = ref(null)
const saving = ref(false)

// 当前过滤器
const currentFilter = computed(() => ({
  category: selectedCategory.value,
  tag: selectedTag.value,
  keyword: searchKeyword.value
}))

// 过滤后的笔记列表
const filteredNotes = computed(() => {
  let result = notes.value

  // 按分类筛选
  if (selectedCategory.value) {
    result = result.filter(n => n.categoryId === selectedCategory.value)
  }

  // 按标签筛选
  if (selectedTag.value) {
    result = result.filter(n =>
      n.tags && n.tags.some(t => t.id === selectedTag.value)
    )
  }

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(n =>
      n.title.toLowerCase().includes(keyword) ||
      n.content.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 初始化加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 并行加载笔记、分类、标签
    const [notesRes, catsRes, tagsRes] = await Promise.all([
      api.notes.getAll(),
      api.categories.getAll(),
      // TODO: 获取标签列表的 API
      Promise.resolve({ data: { content: [] } })
    ])

    // 安全提取笔记列表
    notes.value = Array.isArray(notesRes.data?.data?.content)
      ? notesRes.data.data.content
      : []

    // 安全提取分类列表
    const categoryList = catsRes.data?.data?.content || []
    categories.value = categoryList.map(c => ({
      id: c.id,
      label: c.name,
      children: []
    }))

    // 安全提取标签列表
    tags.value = tagsRes.data?.data?.content || []

    // 设置最近访问笔记
    recentNotes.value = [...notes.value]
      .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
      .slice(0, 10)

    console.log('数据加载完成:', {
      notes: notes.value.length,
      categories: categories.value.length,
      tags: tags.value.length
    })
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = async (keyword) => {
  searchKeyword.value = keyword
  loading.value = true
  try {
    const res = await api.notes.search(keyword)
    notes.value = res.data || []
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 处理主题切换
const handleThemeToggle = (theme) => {
  isDarkMode.value = theme === 'dark'
  localStorage.setItem('theme', theme)

  const html = document.documentElement
  if (theme === 'dark') {
    html.classList.add('dark')
    html.classList.remove('light')
  } else {
    html.classList.add('light')
    html.classList.remove('dark')
  }

  ElMessage.success(`已切换到${theme === 'dark' ? '暗色' : '亮色'}主题`)
}

// 处理返回首页
const handleHome = () => {
  selectedCategory.value = null
  selectedTag.value = null
  searchKeyword.value = ''
  selectedNoteId.value = null
  currentNote.value = null
}

// 处理新建笔记
const handleNewNote = () => {
  const newNote = {
    id: null,
    title: '无标题笔记',
    content: '',
    categoryId: null,
    tags: [],
    isStarred: false
  }

  currentNote.value = newNote
  selectedNoteId.value = null
}

// 处理侧边栏切换
const handleToggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 处理选择分类
const handleSelectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  selectedTag.value = null
  searchKeyword.value = ''
}

// 处理选择标签
const handleSelectTag = (tagId) => {
  selectedTag.value = tagId
  selectedCategory.value = null
  searchKeyword.value = ''
}

// 处理选择最近笔记
const handleSelectRecent = (noteId) => {
  selectedNoteId.value = noteId
  currentNote.value = notes.value.find(n => n.id === noteId)
}

// 处理选择笔记
const handleSelectNote = (noteId) => {
  selectedNoteId.value = noteId
  currentNote.value = notes.value.find(n => n.id === noteId)

  // 更新访问次数
  const note = currentNote.value
  if (note) {
    note.viewCount = (note.viewCount || 0) + 1
  }
}

// 处理保存笔记
const handleSaveNote = async (noteData) => {
  if (!noteData.content?.trim()) return
	
  if (saving.value) return

  saving.value = true

  try {
    let res

    if (noteData.id) {
      res = await api.notes.update(noteData.id, noteData)
      ElMessage.success('更新成功')
    } else {
      res = await api.notes.create(noteData)
      ElMessage.success('创建成功')
    }

    const savedNote = res.data

    // 更新本地 notes
    const index = notes.value.findIndex(n => n.id === savedNote.id)

    if (index !== -1) {
      notes.value[index] = savedNote
    } else {
      notes.value.unshift(savedNote)
    }

    // 更新当前笔记
    currentNote.value = savedNote
    selectedNoteId.value = savedNote.id

  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 处理星标笔记
const handleStarNote = async (noteId, isStarred) => {
  try {
    // TODO: 调用 API 更新星标状态
    console.log('星标笔记:', noteId, isStarred)

    const note = notes.value.find(n => n.id === noteId)
    if (note) {
      note.isStarred = isStarred
    }

    if (currentNote.value && currentNote.value.id === noteId) {
      currentNote.value.isStarred = isStarred
    }

    ElMessage.success(isStarred ? '已添加星标' : '已取消星标')
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 处理删除笔记
const handleDeleteNote = async (noteId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 调用删除接口
    await api.notes.delete(noteId)

    // 从列表中移除
    const index = notes.value.findIndex(n => n.id === noteId)
    if (index !== -1) {
      notes.value.splice(index, 1)
    }

    // 如果删除的是当前选中的笔记
    if (selectedNoteId.value === noteId) {
      selectedNoteId.value = null
      currentNote.value = null
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理移动笔记
const handleMoveNote = (noteId) => {
  // TODO: 实现移动笔记功能
  console.log('移动笔记:', noteId)
  ElMessage.info('移动功能开发中')
}

// 处理导出笔记
const handleExportNote = (noteId) => {
  const note = notes.value.find(n => n.id === noteId)
  if (note) {
    const blob = new Blob([note.content], { type: 'text/markdown' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${note.title || 'note'}.md`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  }
}

// 处理关闭编辑器
const handleCloseEditor = () => {
  selectedNoteId.value = null
  currentNote.value = null
}

// 处理视图切换
const handleViewModeChange = (mode) => {
  viewMode.value = mode
}

// 处理排序变更
const handleSortByChange = (value) => {
  sortBy.value = value
}

// 处理批量删除
const handleBatchDelete = (noteIds) => {
  console.log('批量删除:', noteIds)
  ElMessage.info('批量删除功能开发中')
}

// 处理批量移动
const handleBatchMove = (noteIds) => {
  console.log('批量移动:', noteIds)
  ElMessage.info('批量移动功能开发中')
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style>
/* 全局样式已在 main.js 中定义 */
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 主布局 */
.main-layout {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-layout {
    flex-direction: column;
  }

  #app {
    height: 100vh;
  }
}

/*过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
