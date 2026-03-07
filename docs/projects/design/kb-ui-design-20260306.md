# Knowledge Base UI 设计方案

**项目名称：** 知识库系统 UI 设计
**创建时间：** 2026-03-06
**设计者：** 小克🐕💎
**文档版本：** v1.0
**参考系统：** Obsidian、Notion

---

## 📐 1. 整体布局设计

### 1.1 布局结构

采用经典的三栏布局，灵感来自 Obsidian 的编辑界面和 Notion 的卡片展示。

```
┌─────────────────────────────────────────────────────────────────┐
│  顶部导航栏 (Header Bar)                                         │
│  Logo | 搜索框 | 新建笔记 | 主题切换 | 用户头像                    │
├──────────┬─────────────────────────┬──────────────────────────────┤
│          │                          │                              │
│  左侧栏  │      中间栏              │      右侧栏                  │
│  Sidebar │    Note List            │    Note Editor/Preview       │
│          │                          │                              │
│  ┌────┐  │  ┌───────────────────┐  │  ┌────────────────────────┐  │
│  │分类│  │  │  笔记卡片 1       │  │  │   编辑区 / 预览区       │  │
│  │树  │  │  ├───────────────────┤  │  ├────────────────────────┤  │
│  ├────┤  │  │  笔记卡片 2       │  │  │                        │  │
│  │标签│  │  ├───────────────────┤  │  │   Toolbar              │  │
│  │云  │  │  │  笔记卡片 3       │  │  │   [切换视图] [保存]... │  │
│  ├────┤  │  ├───────────────────┤  │  │                        │  │
│  │最近│  │  │  笔记卡片 4       │  │  │                        │  │
│  │访问│  │  ├───────────────────┤  │  │                        │  │
│  └────┘  │  │  ...              │  │  │                        │  │
│          │  └───────────────────┘  │  └────────────────────────┘  │
│          │                          │                              │
│          │  [列表/网格切换] [排序]  │                              │
│          │                          │                              │
└──────────┴─────────────────────────┴──────────────────────────────┘
```

### 1.2 布局详细说明

#### 顶部导航栏 (Top Bar)
- **高度：** 56px
- **元素：**
  - 左侧：Logo（可点击返回首页）
  - 中间：搜索框（el-input, 占比 40%）
  - 右侧：功能按钮（新建笔记、主题切换、用户头像）

#### 左侧栏 (Sidebar)
- **宽度：** 240px（可调整，支持折叠到 64px）
- **位置：** 顶部导航栏下方左侧
- **内容：**
  - 分类树（el-tree）
  - 标签云（标签云展示）
  - 最近访问（el-timeline 或列表）
  - 回收站（可选）
- **特性：**
  - 支持折叠/展开
  - 当前选中项高亮
  - 右键菜单（新增、编辑、删除分类/标签）

#### 中间栏 (Note List)
- **宽度：** 320px（可调整，支持收起）
- **位置：** 顶部导航栏下方中间
- **内容：**
  - 笔记列表/网格（NoteCard 组件）
  - 视图切换按钮（列表/网格）
  - 排序下拉菜单（按时间、标题、热度）
  - 分页或无限滚动
- **特性：**
  - 搜索结果动态过滤
  - 当前选中笔记高亮
  - 悬停效果
  - 拖拽排序（可选）

#### 右侧栏 (Note Editor/Preview)
- **宽度：** flex-grow（占满剩余空间）
- **位置：** 顶部导航栏下方右侧
- **内容：**
  - 顶部工具栏（操作按钮、视图切换）
  - Markdown 编辑器/预览区（mavon-editor）
  - 底部状态栏（字数统计、保存状态、最后修改时间）
- **特性：**
  - 支持编辑/预览/分栏三种模式
  - 实时保存
  - 全屏模式
  - 快捷键支持

### 1.3 组件树结构

```
App.vue
├── TopBar.vue (顶部导航栏)
│   ├── Logo
│   ├── SearchBar (搜索框)
│   ├── ActionButtons (新建笔记、主题切换)
│   └── UserAvatar (用户头像)
├── MainLayout.vue (主布局)
│   ├── Sidebar.vue (左侧栏)
│   │   ├── CategoryTree.vue (分类树)
│   │   ├── TagCloud.vue (标签云)
│   │   ├── RecentNotes.vue (最近访问)
│   │   └── SidebarFooter.vue (底部折叠按钮)
│   ├── NoteListPanel.vue (中间栏)
│   │   ├── ViewToggle.vue (视图切换)
│   │   ├── SortDropdown.vue (排序)
│   │   └── NoteList.vue / NoteGrid.vue (笔记列表/网格)
│   │       └── NoteCard.vue (笔记卡片) [重复 N 次]
│   └── NoteEditorPanel.vue (右侧栏)
│       ├── EditorToolbar.vue (编辑器工具栏)
│       ├── MarkdownEditor.vue (Markdown 编辑器)
│       └── EditorFooter.vue (底部状态栏)
└── GlobalComponents
    ├── ThemeProvider.vue (主题提供者)
    ├── Toast.vue (全局通知)
    └── ConfirmDialog.vue (确认对话框)
```

---

## 🎨 2. 颜色方案

### 2.1 暗色主题（Dark Mode）- 参考 Obsidian

参考：Obsidian 深色主题，专业护眼，适合长时间使用。

```css
/* 主色调 */
--primary-color: #7c3aed;      /* 主色：紫色 */
--primary-light: #a78bfa;      /* 浅紫色 */
--primary-dark: #5b21b6;       /* 深紫色 */

/* 背景色 */
--bg-primary: #1e1e2e;         /* 主背景：深色背景 */
--bg-secondary: #2d2d3a;       /* 次级背景：卡片背景 */
--bg-tertiary: #3a3a4a;        /* 三级背景：悬浮态 */
--bg-elevated: #252532;        /* 浮升背景：弹窗、下拉菜单 */

/* 文字色 */
--text-primary: #f2f2f7;       /* 主文字：白色 */
--text-secondary: #b8b8c8;     /* 次级文字：灰色 */
--text-tertiary: #8e8e9e;      /* 三级文字：浅灰 */
--text-disabled: #5a5a6a;      /* 禁用文字 */

/* 边框色 */
--border-color: #3d3d4d;       /* 边框 */
--border-light: #4a4a5a;       /* 亮边框 */
--border-focus: #7c3aed;       /* 聚焦边框 */

/* 功能色 */
--success-color: #4ade80;      /* 成功：绿色 */
--warning-color: #fbbf24;      /* 警告：黄色 */
--error-color: #f87171;        /* 错误：红色 */
--info-color: #60a5fa;         /* 信息：蓝色 */

/* 阴影 */
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
--shadow-md: 0 4px 6px rgba(0, 0, 0, 0.4);
--shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.5);
```

**配色说明：**
- 使用紫色系作为主色，既专业又现代
- 背景采用深灰色调，不是纯黑，减少视觉疲劳
- 文字对比度足够，确保可读性
- 功能色保持高对比度，确保状态信息清晰

### 2.2 亮色主题（Light Mode）- 参考 Notion

参考：Notion 亮色主题，清爽简洁，适合白天使用。

```css
/* 主色调 */
--primary-color: #7c3aed;      /* 主色：紫色 */
--primary-light: #a78bfa;      /* 浅紫色 */
--primary-dark: #5b21b6;       /* 深紫色 */

/* 背景色 */
--bg-primary: #ffffff;         /* 主背景：白色 */
--bg-secondary: #f9fafb;       /* 次级背景：浅灰 */
--bg-tertiary: #f3f4f6;        /* 三级背景：浅灰悬浮 */
--bg-elevated: #ffffff;        /* 浮升背景：白色带阴影 */

/* 文字色 */
--text-primary: #111827;       /* 主文字：深灰 */
--text-secondary: #4b5563;     /* 次级文字：中灰 */
--text-tertiary: #9ca3af;      /* 三级文字：浅灰 */
--text-disabled: #d1d5db;      /* 禁用文字 */

/* 边框色 */
--border-color: #e5e7eb;       /* 边框 */
--border-light: #d1d5db;       /* 亮边框 */
--border-focus: #7c3aed;       /* 聚焦边框 */

/* 功能色 */
--success-color: #10b981;      /* 成功：绿色 */
--warning-color: #f59e0b;      /* 警告：橙色 */
--error-color: #ef4444;        /* 错误：红色 */
--info-color: #3b82f6;         /* 信息：蓝色 */

/* 阴影 */
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
--shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
--shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.15);
```

**配色说明：**
- 保持紫色主色，确保品牌一致性
- 背景使用浅灰色调，与白色形成层次
- 文字使用深灰色而非纯黑，增加可读性
- 边框和阴影更轻柔，减少视觉干扰

### 2.3 主题切换实现

```javascript
// store/theme.js (Pinia)
import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    theme: localStorage.getItem('theme') || 'dark'
  }),

  getters: {
    isDark: (state) => state.theme === 'dark'
  },

  actions: {
    toggleTheme() {
      this.theme = this.theme === 'dark' ? 'light' : 'dark'
      localStorage.setItem('theme', this.theme)
      this.applyTheme()
    },

    applyTheme() {
      const html = document.documentElement
      if (this.theme === 'dark') {
        html.classList.add('dark')
        html.classList.remove('light')
      } else {
        html.classList.add('light')
        html.classList.remove('dark')
      }
    }
  }
})
```

---

## 🧩 3. 组件拆分和职责

### 3.1 App.vue

**职责：**
- 应用根组件
- 全局状态初始化
- 路由管理
- 主题初始化

**Props:** 无

**Emits:** 无

**关键代码结构：**
```vue
<template>
  <div id="app" :class="{ dark: themeStore.isDark }">
    <router-view />
  </div>
</template>

<script setup>
import { useThemeStore } from '@/stores/theme'
import { onMounted } from 'vue'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.applyTheme()
})
</script>
```

---

### 3.2 TopBar.vue（顶部导航栏）

**职责：**
- 显示 Logo
- 搜索功能
- 全局操作按钮（新建笔记、主题切换）
- 用户信息展示

**Props:**
```typescript
interface TopBarProps {
  logo?: string        // Logo 图片路径
  userName?: string     // 用户名
  userAvatar?: string   // 用户头像
}
```

**Emits:**
```typescript
interface TopBarEmits {
  (e: 'search', query: string): void      // 搜索事件
  (e: 'newNote'): void                    // 新建笔记事件
  (e: 'toggleTheme'): void               // 切换主题事件
}
```

**Element Plus 组件：**
- `el-input` (搜索框)
- `el-button` (新建笔记按钮)
- `el-switch` (主题切换)
- `el-dropdown` (用户菜单)
- `el-avatar` (用户头像)

**示例代码：**
```vue
<template>
  <div class="top-bar">
    <div class="logo" @click="$router.push('/')">
      <img :src="logo" alt="Logo" />
      <span>Knowledge Base</span>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索笔记..."
        :prefix-icon="Search"
        @input="handleSearch"
        clearable
      />
    </div>

    <div class="actions">
      <el-button type="primary" :icon="Plus" @click="$emit('newNote')">
        新建笔记
      </el-button>

      <el-switch
        v-model="isDark"
        inline-prompt
        :active-icon="Moon"
        :inactive-icon="Sunny"
        @change="$emit('toggleTheme')"
      />

      <el-dropdown>
        <el-avatar :src="userAvatar">{{ userName?.[0] }}</el-avatar>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人资料</el-dropdown-item>
            <el-dropdown-item>设置</el-dropdown-item>
            <el-dropdown-item divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>
```

---

### 3.3 Sidebar.vue（侧边栏）

**职责：**
- 分类树展示
- 标签云展示
- 最近访问展示
- 侧边栏折叠/展开

**Props:**
```typescript
interface SidebarProps {
  collapsed?: boolean           // 是否折叠
  categories?: Category[]       // 分类列表
  tags?: Tag[]                  // 标签列表
  recentNotes?: Note[]          // 最近访问笔记
}
```

**Emits:**
```typescript
interface SidebarEmits {
  (e: 'selectCategory', id: number | null): void       // 选择分类
  (e: 'selectTag', id: number | null): void            // 选择标签
  (e: 'selectRecent', id: number): void                // 选择最近笔记
  (e: 'toggleCollapse'): void                          // 切换折叠
}
```

**Element Plus 组件：**
- `el-menu` (左侧菜单容器)
- `el-menu-item` (菜单项)
- `el-tree` (分类树)
- `el-tag` (标签)
- `el-scrollbar` (滚动容器)

**分类树数据结构：**
```typescript
interface Category {
  id: number
  name: string
  icon?: string
  children?: Category[]
  noteCount?: number
}
```

**标签云数据结构：**
```typescript
interface Tag {
  id: number
  name: string
  color?: string   // 标签颜色（可选）
  noteCount: number
}
```

**主要功能：**
1. **分类树：** el-tree 组件，支持拖拽、右键菜单
2. **标签云：** 标签按照使用次数排序，热门标签加大
3. **最近访问：** 显示最近 10 条访问记录
4. **折叠功能：** 收起时只显示图标，展开显示完整信息

---

### 3.4 NoteList.vue（笔记列表）

**职责：**
- 笔记列表展示（卡片模式）
- 笔记列表展示（列表模式）
- 视图切换（列表/网格）
- 排序功能
- 分页/无限滚动

**Props:**
```typescript
interface NoteListProps {
  notes: Note[]                       // 笔记列表
  viewMode: 'list' | 'grid'           // 视图模式
  sortBy: 'time' | 'title' | 'heat'   // 排序方式
  selectedNoteId?: number             // 当前选中笔记 ID
  loading?: boolean                   // 加载状态
}
```

**Emits:**
```typescript
interface NoteListEmits {
  (e: 'selectNote', id: number): void         // 选择笔记
  (e: 'deleteNote', id: number): void         // 删除笔记
  (e: 'viewModeChange', mode: 'list' | 'grid'): void   // 视图切换
  (e: 'sortByChange', sortBy: string): void   // 排序变更
  (e: 'loadMore'): void                       // 加载更多
}
```

**Element Plus 组件：**
- `el-scrollbar` (滚动容器)
- `el-row` / `el-col` (网格布局)
- `el-select` (排序选择)
- `el-button-group` (视图切换按钮)
- `el-pagination` (分页)

**笔记数据结构：**
```typescript
interface Note {
  id: number
  title: string
  content: string
  summary?: string              // 摘要（自动生成）
  categoryId: number
  tags: Tag[]
  createdAt: string
  updatedAt: string
  coverImage?: string           // 封面图（随机颜色或图片）
  wordCount: number             // 字数统计
  viewCount: number             // 访问次数
  isStarred?: boolean           // 是否星标
}
```

**视图切换：**
1. **列表模式：** 紧凑型，显示标题、摘要、标签、时间
2. **网格模式：** 卡片型，显示封面、标题、摘要、标签

**排序选项：**
- 按时间倒序（默认）
- 按时间正序
- 按标题 A-Z
- 按标题 Z-A
- 按热度（访问次数）

---

### 3.5 NoteCard.vue（笔记卡片）

**职责：**
- 单个笔记的卡片展示
- 点击选择
- 悬停效果
- 右键菜单（删除、移动、复制等）

**Props:**
```typescript
interface NoteCardProps {
  note: Note                       // 笔记数据
  selected?: boolean               // 是否选中
  viewMode: 'list' | 'grid'        // 视图模式
}
```

**Emits:**
```typescript
interface NoteCardEmits {
  (e: 'select', id: number): void                 // 选择笔记
  (e: 'delete', id: number): void                 // 删除笔记
  (e: 'star', id: number, isStarred: boolean): void  // 星标/取消星标
}
```

**Element Plus 组件：**
- `el-card` (卡片容器)
- `el-image` (封面图)
- `el-tag` (标签)
- `el-dropdown` (右键菜单)

**卡片样式：**
```vue
<template>
  <el-card
    class="note-card"
    :class="{ selected: selected, 'list-mode': viewMode === 'list' }"
    @click="$emit('select', note.id)"
    :body-style="{ padding: '16px' }"
  >
    <!-- 封面图（仅网格模式） -->
    <div v-if="viewMode === 'grid' && note.coverImage" class="card-cover">
      <el-image :src="note.coverImage" fit="cover" />
    </div>

    <!-- 笔记标题 -->
    <h3 class="card-title">{{ note.title || '无标题' }}</h3>

    <!-- 笔记摘要 -->
    <p class="card-summary">{{ note.summary || note.content.slice(0, 100) }}...</p>

    <!-- 标签 -->
    <div class="card-tags">
      <el-tag
        v-for="tag in note.tags.slice(0, 3)"
        :key="tag.id"
        size="small"
        :color="tag.color"
      >
        {{ tag.name }}
      </el-tag>
    </div>

    <!-- 底部信息 -->
    <div class="card-footer">
      <span class="card-time">{{ formatDate(note.updatedAt) }}</span>
      <el-dropdown @command="handleCommand">
        <el-button link :icon="MoreFilled" />
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :command="{ action: 'star', id: note.id }">
              {{ note.isStarred ? '取消星标' : '星标' }}
            </el-dropdown-item>
            <el-dropdown-item :command="{ action: 'move', id: note.id }">移动</el-dropdown-item>
            <el-dropdown-item :command="{ action: 'delete', id: note.id }" divided>删除</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-card>
</template>
```

**卡片样式特性：**
1. **悬停效果：** 卡片略微上浮，阴影加深
2. **选中状态：** 边框高亮（使用主题色）
3. **封面图：** 可配置随机颜色或图片
4. **标签：** 显示前 3 个，多余的显示 +N

---

### 3.6 NoteEditor.vue（笔记编辑器）

**职责：**
- Markdown 编辑
- 实时预览
- 分栏切换（编辑/预览/分栏）
- 工具栏操作
- 自动保存
- 快捷键支持

**Props:**
```typescript
interface NoteEditorProps {
  note?: Note                      // 当前编辑的笔记
  readonly?: boolean               // 是否只读
}
```

**Emits:**
```typescript
interface NoteEditorEmits {
  (e: 'save', note: Partial<Note>): void    // 保存笔记
  (e: 'close'): void                        // 关闭编辑器
  (e: 'delete', id: number): void          // 删除笔记
}
```

**Element Plus 组件：**
- `mavon-editor` (Markdown 编辑器)
- `el-button-group` (工具栏按钮)
- `el-button` (操作按钮)
- `el-select` (视图模式选择)

**编辑器视图模式：**
1. **编辑模式：** 纯 Markdown 编辑
2. **预览模式：** 纯 HTML 预览
3. **分栏模式：** 左侧编辑，右侧预览（默认）

**编辑器工具栏：**
```vue
<template>
  <div class="note-editor">
    <div class="editor-toolbar">
      <el-button-group>
        <el-button :type="editorMode === 'edit' ? 'primary' : ''" @click="editorMode = 'edit'">
          编辑
        </el-button>
        <el-button :type="editorMode === 'preview' ? 'primary' : ''" @click="editorMode = 'preview'">
          预览
        </el-button>
        <el-button :type="editorMode === 'fullscreen' ? 'primary' : ''" @click="toggleFullscreen">
          全屏
        </el-button>
      </el-button-group>

      <div class="toolbar-center">
        <el-select v-model="note.categoryId" placeholder="选择分类">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>

        <el-select v-model="selectedTags" multiple placeholder="添加标签">
          <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
      </div>

      <el-button-group>
        <el-button :icon="Star" @click="toggleStar">{{ note.isStarred ? '已星标' : '星标' }}</el-button>
        <el-button :icon="Upload" @click="showUploadDialog">导入 MD</el-button>
        <el-button :icon="Download" @click="exportMarkdown">导出</el-button>
        <el-button type="primary" :icon="Check" @click="handleSave">保存</el-button>
      </el-button-group>
    </div>

    <div class="editor-content" :class="{ fullscreen: isFullscreen }">
      <mavon-editor
        v-model="noteContent"
        :toolbars="toolbars"
        :subfield="editorMode === 'split'"
        :preview="editorMode !== 'edit'"
        :editable="!readonly"
        :toolbars-flag="editorMode !== 'preview'"
        @imgAdd="handleImageAdd"
        @save="handleAutoSave"
      />
    </div>

    <div class="editor-footer">
      <span class="word-count">字数：{{ wordCount }}</span>
      <span class="save-status">{{ saveStatus }}</span>
      <span class="last-modified">最后修改：{{ formatDate(note?.updatedAt) }}</span>
    </div>
  </div>
</template>
```

**mavon-editor 配置：**
```javascript
const toolbars = {
  bold: true,                 // 粗体
  italic: true,               // 斜体
  header: true,               // 标题
  underline: true,            // 下划线
  strikethrough: true,        // 中划线
  mark: true,                 // 标记
  superscript: true,          // 上角标
  subscript: true,           // 下角标
  quote: true,               // 引用
  ol: true,                  // 有序列表
  ul: true,                  // 无序列表
  link: true,                // 链接
  imagelink: true,           // 图片链接
  code: true,                // 代码
  table: true,               // 表格
  fullscreen: false,         // 全屏编辑
  readmodel: true,           // 沉浸式阅读
  htmlcode: true,            // 展示 html 源码
  help: true,                // 帮助
  undo: true,                // 上一步
  redo: true,                // 下一步
  trash: true,               // 清空
  save: true,                // 保存（触发 events 中的 save 事件）
}
```

**自动保存逻辑：**
```javascript
// 防抖自动保存，每 5 秒保存一次
let saveTimer = null

const handleAutoSave = (content, html) => {
  clearTimeout(saveTimer)
  saveResult.value = '保存中...'
  saveTimer = setTimeout(() => {
    $emit('save', { content, html })
    saveResult.value = '已保存'
    setTimeout(() => saveResult.value = '', 2000)
  }, 2000)
}
```

---

## 🎭 4. 交互动画建议

### 4.1 页面切换动画

使用 Vue 的 `<transition>` 组件配合 CSS 过渡。

**进场动画：**
```css
/* 淡入 + 上浮 */
.fade-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
```

**侧边栏切换动画：**
```css
/* 侧边栏展开/收起 */
.sidebar-enter-active,
.sidebar-leave-active {
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-enter-from,
.sidebar-leave-to {
  width: 64px;
}
```

### 4.2 卡片交互动画

**点击选择动画：**
```css
.note-card {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.note-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.note-card.selected {
  border: 2px solid var(--primary-color);
  background: var(--bg-primary);
}
```

**加载动画：**
```vue
<template>
  <div class="skeleton-card">
    <div class="skeleton-cover"></div>
    <div class="skeleton-title"></div>
    <div class="skeleton-summary"></div>
    <div class="skeleton-tags"></div>
  </div>
</template>

<style>
.skeleton-card {
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 8px;
  overflow: hidden;
}

.skeleton-cover,
.skeleton-title,
.skeleton-summary,
.skeleton-tags {
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-cover { height: 150px; margin-bottom: 12px; }
.skeleton-title { height: 24px; margin-bottom: 8px; width: 80%; }
.skeleton-summary { height: 16px; margin-bottom: 12px; width: 100%; }
.skeleton-tags { height: 20px; width: 120px; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
```

### 4.3 按钮交互动画

**按钮涟漪效果：**
```css
.el-button {
  position: relative;
  overflow: hidden;
}

.el-button::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  top: 50%;
  left: 50%;
  pointer-events: none;
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.3) 10%, transparent 10.01%);
  background-repeat: no-repeat;
  background-position: 50%;
  transform: translate(-50%, -50%) scale(10);
  opacity: 0;
  transition: transform 0.5s, opacity 1s;
}

.el-button:active::after {
  transform: translate(-50%, -50%) scale(0);
  opacity: 1;
  transition: 0s;
}
```

### 4.4 滚动条美化

```css
/* 自定义滚动条 */
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
```

### 4.5 加载动画

**全局加载动画：**
```vue
<template>
  <transition name="fade">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
    </div>
  </transition>
</template>

<style>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--bg-secondary);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
```

### 4.6 主题切换动画

```css
/* 主题切换过渡 */
html {
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* 所有元素也跟随过渡 */
body, .el-card, .el-button, .el-input {
  transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease;
}
```

---

## 📱 5. 响应式设计

### 5.1 断点规划

```css
/* 移动端：<= 768px */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    z-index: 1000;
    margin-left: -240px;
    transition: margin-left 0.3s ease;
  }

  .sidebar.open {
    margin-left: 0;
  }

  .note-list {
    width: 100%;
  }

  .note-editor {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 2000;
  }
}

/* 平板：769px - 1024px */
@media (min-width: 769px) and (max-width: 1024px) {
  .sidebar {
    width: 200px;
  }

  .note-list {
    width: 280px;
  }
}

/* 桌面端：>= 1025px */
@media (min-width: 1025px) {
  .sidebar {
    width: 240px;
  }

  .note-list {
    width: 320px;
  }
}
```

### 5.2 移动端适配方案

1. **汉堡菜单：** 左上角按钮，点击滑出侧边栏
2. **全屏编辑器：** 点击笔记进入全屏编辑模式
3. **底部导航栏：** 替代顶部导航（可选）
4. **手势操作：** 左滑返回列表，右滑进入编辑

---

## 📦 6. 文件结构

```
src/
├── components/
│   ├── TopBar.vue              # 顶部导航栏
│   ├── MainLayout.vue          # 主布局容器
│   ├── sidebar/
│   │   ├── Sidebar.vue         # 侧边栏容器
│   │   ├── CategoryTree.vue    # 分类树
│   │   ├── TagCloud.vue        # 标签云
│   │   └── RecentNotes.vue     # 最近访问
│   ├── NoteListPanel.vue       # 笔记列表面板
│   ├── NoteList.vue            # 笔记列表（列表模式）
│   ├── NoteGrid.vue            # 笔记列表（网格模式）
│   ├── NoteCard.vue            # 笔记卡片
│   ├── NoteEditorPanel.vue     # 编辑器面板
│   ├── EditorToolbar.vue       # 编辑器工具栏
│   ├── MarkdownEditor.vue      # Markdown 编辑器封装
│   ├── EditorFooter.vue        # 编辑器底部状态栏
│   ├── UploadDialog.vue        # 文件上传对话框
│   ├── ConfirmDialog.vue       # 确认对话框
│   └── Toast.vue               # 全局通知组件
├── stores/
│   ├── theme.js                # 主题 Store
│   ├── note.js                 # 笔记 Store
│   ├── category.js             # 分类 Store
│   └── tag.js                  # 标签 Store
├── views/
│   ├── Home.vue                # 首页（三栏布局）
│   ├── Login.vue               # 登录页
│   └── Settings.vue            # 设置页
├── router/
│   └── index.js                # 路由配置
├── api/
│   ├── note.js                 # 笔记 API
│   ├── category.js             # 分类 API
│   ├── tag.js                  # 标签 API
│   └── upload.js               # 文件上传 API
├── utils/
│   ├── format.js               # 格式化工具
│   ├── markdown.js             # Markdown 工具
│   └── storage.js              # 本地存储工具
├── styles/
│   ├── variables.scss          # CSS 变量
│   ├── dark.scss               # 暗色主题
│   ├── light.scss              # 亮色主题
│   ├── transitions.scss        # 过渡动画
│   └── global.scss             # 全局样式
├── App.vue                     # 根组件
└── main.js                     # 入口文件
```

---

## 🎯 7. 开发优先级

### 第一阶段（必须实现）
1. ✅ 三栏基础布局（Sidebar、NoteList、NoteEditor）
2. ✅ 暗色主题（默认）
3. ✅ 基础组件（TopBar、NoteCard、NoteEditor）
4. ✅ Markdown 编辑器集成（mavon-editor）
5. ✅ 笔记增删改查

### 第二阶段（重要功能）
1. 🔲 亮色主题切换
2. 🔲 分类树和标签云
3. 🔲 最近访问记录
4. 🔲 笔记搜索功能
5. 🔲 视图切换（列表/网格）

### 第三阶段（增强功能）
1. 🔲 MD 文件上传
2. 🔲 批量导入
3. 🔲 笔记星标
4. 🔲 拖拽排序
5. 🔲 快捷键支持

### 第四阶段（优化体验）
1. 🔲 响应式移动端适配
2. 🔲 加载动画优化
3. 🔲 性能优化（虚拟滚动）
4. 🔲 图片懒加载
5. 🔲 版本历史

---

## 🛠️ 8. 技术约束和依赖

### 8.1 核心依赖

```json
{
  "dependencies": {
    "vue": "^3.3.0",
    "vue-router": "^4.2.0",
    "pinia": "^2.1.0",
    "element-plus": "^2.4.0",
    "@element-plus/icons-vue": "^2.1.0",
    "mavon-editor": "^2.10.0",
    "axios": "^1.6.0",
    "dayjs": "^1.11.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.5.0",
    "vite": "^5.0.0",
    "sass": "^1.69.0"
  }
}
```

### 8.2 Element Plus 组件使用清单

| 组件 | 用途 | 使用频率 |
|------|------|----------|
| `el-button` | 按钮 | ⭐⭐⭐⭐⭐ |
| `el-input` | 输入框 | ⭐⭐⭐⭐⭐ |
| `el-card` | 卡片容器 | ⭐⭐⭐⭐⭐ |
| `el-tree` | 分类树 | ⭐⭐⭐⭐ |
| `el-tag` | 标签 | ⭐⭐⭐⭐⭐ |
| `el-dropdown` | 下拉菜单 | ⭐⭐⭐⭐ |
| `el-avatar` | 用户头像 | ⭐⭐⭐ |
| `el-switch` | 主题切换 | ⭐⭐⭐ |
| `el-scrollbar` | 滚动容器 | ⭐⭐⭐⭐ |
| `el-select` | 选择器 | ⭐⭐⭐⭐ |
| `el-upload` | 文件上传 | ⭐⭐⭐ |
| `el-pagination` | 分页 | ⭐⭐⭐ |
| `el-dialog` | 对话框 | ⭐⭐⭐ |
| `el-message` | 消息提示 | ⭐⭐⭐⭐⭐ |

---

## 📊 9. 数据流设计

### 9.1 全局状态（Pinia）

```javascript
// stores/note.js
export const useNoteStore = defineStore('note', {
  state: () => ({
    notes: [],              // 所有笔记
    currentNote: null,      // 当前编辑的笔记
    selectedNoteId: null,   // 当前选中的笔记 ID
    loading: false,         // 加载状态
  }),

  getters: {
    getNoteById: (state) => (id) => state.notes.find(n => n.id === id),
    filteredNotes: (state) => (filters) => { /* 过滤逻辑 */ },
  },

  actions: {
    async fetchNotes() { /* 获取笔记列表 */ },
    async createNote(note) { /* 创建笔记 */ },
    async updateNote(id, data) { /* 更新笔记 */ },
    async deleteNote(id) { /* 删除笔记 */ },
    setCurrentNote(note) { /* 设置当前笔记 */ },
  }
})
```

### 9.2 组件通信

```
TopBar (搜索) → NoteList (过滤显示)
          ↓
     NoteStore (状态管理)
          ↓
NoteList (选择笔记) → NoteEditor (加载笔记)
          ↓
     NoteStore (currentNote 更新)
```

---

## 🔍 10. 可维护性设计

### 10.1 样式规范

```scss
// 命名规范：BEM (Block Element Modifier)
.note-card {                    // Block
  &__title {                    // Element
    font-size: 16px;
  }

  &--selected {                 // Modifier
    border: 2px solid var(--primary-color);
  }

  &--grid {                     // Modifier
    width: 250px;
  }
}
```

### 10.2 组件 Props 验证

```javascript
// 使用 TypeScript 接口验证
interface NoteCardProps {
  note: Note
  selected?: boolean
  viewMode: 'list' | 'grid'
}

defineProps<NoteCardProps>()
```

### 10.3 错误处理

```javascript
// 统一错误处理
try {
  await api.createNote(data)
  ElMessage.success('创建成功')
} catch (error) {
  console.error('创建失败:', error)
  ElMessage.error('创建失败，请重试')
}
```

---

## 🐕 11. 附录

### 11.1 图标清单

| 图标 | 组件 | 使用场景 |
|------|------|----------|
| `Search` | Search | 搜索框 |
| `Plus` | Plus | 新建笔记 |
| `Star` | StarF | 星标笔记 |
| `Edit` | Edit | 编辑笔记 |
| `Delete` | Delete | 删除笔记 |
| `MoreFilled` | MoreFilled | 右键菜单 |
| `Moon` | Moon | 暗色主题 |
| `Sunny` | Sunny | 亮色主题 |
| `Download` | Download | 导出笔记 |
| `Upload` | Upload | 上传文件 |
| `Check` | Check | 保存操作 |
| `Close` | Close | 关闭弹窗 |

### 11.2 快捷键建议

| 快捷键 | 功能 | 优先级 |
|--------|------|--------|
| `Ctrl + N` | 新建笔记 | ⭐⭐⭐⭐⭐ |
| `Ctrl + S` | 保存笔记 | ⭐⭐⭐⭐⭐ |
| `Ctrl + F` | 搜索笔记 | ⭐⭐⭐⭐ |
| `Ctrl + /` | 切换侧边栏 | ⭐⭐⭐⭐ |
| `Ctrl + E` | 切换编辑/预览 | ⭐⭐⭐⭐ |
| `Ctrl + Shift + T` | 切换主题 | ⭐⭐⭐ |
| `Ctrl + W` | 关闭当前笔记 | ⭐⭐⭐ |
| `Esc` | 退出全屏 | ⭐⭐⭐ |

### 11.3 性能优化建议

1. **虚拟滚动：** 笔记列表超过 100 条时使用虚拟滚动
2. **图片懒加载：** 列表中的封面图懒加载
3. **防抖搜索：** 搜索输入防抖 300ms
4. **代码分割：** 路由级别懒加载
5. **缓存策略：** 使用 localStorage 缓存最近访问的笔记

---

## 📝 12. 设计总结

### 12.1 设计亮点

✅ **三栏布局：** 清晰的信息层级，提高浏览效率
✅ **暗色主题：** 参考 Obsidian，专业护眼
✅ **组件化：** 高度模块化，易于维护和扩展
✅ **响应式：** 支持移动端、平板、桌面端
✅ **交互动画：** 流畅的过渡效果，提升用户体验
✅ **性能优化：** 虚拟滚动、懒加载、防抖节流

### 12.2 技术栈一致性

- ✅ 使用 Vue 3 + Element Plus，与现有系统一致
- ✅ 使用 Pinia 状态管理，轻量简洁
- ✅ 使用 mavon-editor，功能完善的 Markdown 编辑器
- ✅ CSS 变量实现主题切换，代码简洁

### 12.3 可扩展性

- ✅ 组件化设计，便于增加新功能
- ✅ 主题系统可轻松添加新主题
- ✅ 插件化设计，支持第三方插件（后续版本）
- ✅ API 接口标准化，便于移动端开发

---

**文档完成时间：** 2026-03-06
**设计者：** 小克🐕💎
**下一步：** 进入开发实现阶段