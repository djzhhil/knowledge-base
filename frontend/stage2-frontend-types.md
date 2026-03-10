# Stage 2: 前端数据类型检查

## 概述

本文档记录了前端项目中所有组件、API调用及数据绑定的类型信息。

---

## 1. 组件使用的字段和数据类型

### 1.1 App.vue

**响应式数据类型：**

| 变量名 | JavaScript 类型 | 说明 |
|--------|-----------------|------|
| `userName` | String | 用户名，默认值 'User' |
| `isDarkMode` | Boolean | 是否暗色模式，从 localStorage 读取 |
| `sidebarCollapsed` | Boolean | 侧边栏是否折叠，默认 false |
| `notes` | Array | 笔记列表，元素对象结构如下 |
| `categories` | Array | 分类列表，元素对象结构如下 |
| `tags` | Array | 标签列表，元素对象结构如下 |
| `recentNotes` | Array | 最近访问的笔记 |
| `selectedNoteId` | Number \| null | 当前选中的笔记ID |
| `currentNote` | Object \| null | 当前编辑的笔记对象 |
| `viewMode` | String | 视图模式：'list' | 'grid' |
| `sortBy` | String | 排序方式：'time' | 'title' | 'heat' |
| `loading` | Boolean | 加载状态 |
| `searchKeyword` | String | 搜索关键词 |
| `selectedCategory` | Number \| null | 选中的分类ID |
| `selectedTag` | Number \| null | 选中的标签ID |
| `saving` | Boolean | 保存状态 |

**Note 对象结构（笔记数据模型）：**

| 字段名 | JavaScript 类型 | 可空 | 备注 |
|--------|-----------------|------|------|
| `id` | Number \| null | 是 | 新建时为 null |
| `title` | String | 否 | 笔记标题 |
| `content` | String | 是 | Markdown 内容 |
| `categoryId` | Number \| null | 是 | 分类ID |
| `tags` | Array | 否 | 标签数组，元素可能是对象或 ID |
| `isStarred` | Boolean | 否 | 是否星标 |
| `viewCount` | Number | 是 | 浏览次数 |
| `createdAt` | Date \| String | 是 | 创建时间 |
| `updatedAt` | Date \| String | 是 | 更新时间 |
| `summary` | String | 是 | 摘要（仅用于显示） |

**Category 对象结构（分类数据模型）：**

| 字段名 | JavaScript 类型 | 可空 | 备注 |
|--------|-----------------|------|------|
| `id` | Number | 否 | 分类ID |
| `label` \| `name` | String | 否 | 分类名称，后端返回 name，前端映射为 label |
| `children` | Array | 是 | 子分类数组（目前为空） |

**Tag 对象结构（标签数据模型）：**

| 字段名 | JavaScript 类型 | 可空 | 备注 |
|--------|-----------------|------|------|
| `id` | Number | 否 | 标签ID |
| `name` | String | 否 | 标签名称 |

---

### 1.2 NoteEditor.vue

**Props 类型：**

| 属性名 | JavaScript 类型 | 默认值 | 说明 |
|--------|-----------------|--------|------|
| `note` | Object \| null | null | 笔记对象 |
| `categories` | Array | [] | 分类列表 |
| `tags` | Array | [] | 标签列表 |

**本地响应式数据：**

| 变量名 | JavaScript 类型 | 说明 |
|--------|-----------------|------|
| `noteContent` | String | 编辑器内容 |
| `editorMode` | String | 编辑模式：'edit' | 'split' | 'preview' |
| `isFullscreen` | Boolean | 是否全屏 |
| `saving` | Boolean | 保存状态 |
| `saveStatus` | String | 保存状态：'saved' | 'saving' | 'unsaved' | 'error' |
| `selectedTags` | Array | 选中的标签ID数组 |

**Emits 事件参数：**

| 事件名 | 参数类型 | 说明 |
|--------|----------|------|
| `save` | Object | 包含笔记数据的对象 |
| `delete` | Number | 笔记ID |
| `star` | Number, Boolean | 笔记ID，是否星标 |
| `newNote` | 无 | 创建新笔记 |
| `close` | 无 | 关闭编辑器 |

**Computed 属性：**

| 属性名 | 返回类型 | 说明 |
|--------|----------|------|
| `wordCount` | Number | 字数统计（去除空白字符） |
| `readingTime` | Number | 预计阅读时间（400字/分钟） |

---

### 1.3 NoteList.vue

**Props 类型：**

| 属性名 | JavaScript 类型 | 默认值 | 约束 |
|--------|-----------------|--------|------|
| `notes` | Array | [] | - |
| `selectedNoteId` | Number | null | - |
| `viewMode` | String | 'list' | 'list' | 'grid' |
| `sortBy` | String | 'time' | 'time' | 'title' | 'heat' |
| `loading` | Boolean | false | - |
| `filter` | Object | {} | 包含 category, tag, keyword |

**Filter 对象结构：**

| 字段名 | JavaScript 类型 | 说明 |
|--------|-----------------|------|
| `category` | Number \| String | 分类筛选 |
| `tag` | Number \| String | 标签筛选 |
| `keyword` | String | 搜索关键词 |

**本地响应式数据：**

| 变量名 | JavaScript 类型 | 说明 |
|--------|-----------------|------|
| `selectedNotes` | Array | 批量选中的笔记ID数组 |

**Emits 事件参数：**

| 事件名 | 参数类型 | 说明 |
|--------|----------|------|
| `selectNote` | Number | 笔记ID |
| `starNote` | Number, Boolean | 笔记ID，是否星标 |
| `deleteNote` | Number | 笔记ID |
| `moveNote` | Number | 笔记ID |
| `exportNote` | Number | 笔记ID |
| `newNote` | 无 | 创建新笔记 |
| `viewModeChange` | String | 视图模式 |
| `sortByChange` | String | 排序方式 |
| `batchDelete` | Array | 笔记ID数组 |
| `batchMove` | Array | 笔记ID数组 |

---

### 1.4 NoteCard.vue

**Props 类型：**

| 属性名 | JavaScript 类型 | 默认值 | 约束 |
|--------|-----------------|--------|------|
| `note` | Object | (required) | 必填 |
| `selected` | Boolean | false | - |
| `viewMode` | String | 'list' | 'list' | 'grid' |

**Computed 属性：**

| 属性名 | 返回类型 | 说明 |
|--------|----------|------|
| `displayTags` | Array | 最多3个标签 |
| `hasMoreTags` | Boolean | 是否有更多标签 |
| `coverStyle` | Object | 封面样式对象 |

**Emits 事件参数：**

| 事件名 | 参数类型 | 说明 |
|--------|----------|------|
| `select` | Number | 笔记ID |
| `star` | Number, Boolean | 笔记ID，是否星标 |
| `delete` | Number | 笔记ID |
| `move` | Number | 笔记ID |
| `export` | Number | 笔记ID |

---

## 2. API 调用的请求/响应字段

### 2.1 API 拦截器类型处理

**响应拦截器逻辑：**
```javascript
api.interceptors.response.use(
  response => {
    // 如果有 content 字段（分页结构），提取 content
    // 否则直接使用 data
    if (response.data && response.data.content !== undefined) {
      response.data = response.data.content
    }
    return response
  },
  error => { ... }
)
```

**类型说明：**
- **分页响应结构**：`{ data: { content: [...], ... }, status, ... }`
- **非分页响应结构**：`{ data: {...}, status, ... }`
- **拦截器解包后**：`{ data: [...], status, ... }` 或 `{ data: {...}, status, ... }`

---

### 2.2 Notes API

| 方法 | 端点 | 请求参数 (Request Body) | 响应数据类型 (Response Data) |
|------|------|------------------------|------------------------------|
| `getAll()` | `GET /notes` | Query: `page`, `size`, etc. | `Note[]` (内容数组) |
| `getById(id)` | `GET /notes/:id` | - | `Note` (单个对象) |
| `create(note)` | `POST /notes` | `Note` (不含 id) | `Note` (创建后的完整对象) |
| `update(id, note)` | `PUT /notes/:id` | `Note` | `Note` (更新后对象) |
| `delete(id)` | `DELETE /notes/:id` | - | - |
| `search(keyword)` | `GET /notes/search` | Query: `keyword` | `Note[]` |
| `getByCategory(categoryId)` | `GET /notes/category/:categoryId` | - | `Note[]` |

**创建/更新请求对象结构：**

```javascript
{
  title: String,              // 必需
  content: String,            // 必需
  categoryId: Number,         // 可选
  tags: Array<Number>,        // 可选，标签ID数组
  isStarred: Boolean          // 可选
}
```

**响应对象结构（完整对象）：**

```javascript
{
  id: Number,
  title: String,
  content: String,
  summary: String|null,
  categoryId: Number|null,
  category: Object|null,      // 可选，关联的分类对象
  tags: Array<Object>,        // 标签对象数组
  isStarred: Boolean,
  viewCount: Number,
  createdAt: String (ISO 8601),
  updatedAt: String (ISO 8601)
}
```

---

### 2.3 Categories API

| 方法 | 端点 | 请求参数 | 响应数据类型 |
|------|------|----------|--------------|
| `getAll()` | `GET /categories` | - | `Category[]` |
| `create(category)` | `POST /categories` | `Category` | `Category` |
| `delete(id)` | `DELETE /categories/:id` | - | - |

**分类对象结构：**

```javascript
{
  id: Number,
  name: String
}
```

---

### 2.4 Files API

| 方法 | 端点 | 请求参数 | 响应数据类型 |
|------|------|----------|--------------|
| `upload(formData)` | `POST /notes/upload` | `FormData` (包含 file) | `Note` 或上传结果 |
| `import(file)` | `POST /notes/import` | `FormData` (包含 file) | 导入结果 |
| `batchImport(files)` | `POST /notes/import` | `FormData` (多个 files) | 批量导入结果 |

---

## 3. 数据绑定和类型断言

### 3.1 API 响应处理

#### loadData 函数（App.vue）

```javascript
const [notesRes, catsRes, tagsRes] = await Promise.all([
  api.notes.getAll(),
  api.categories.getAll(),
  Promise.resolve({ data: { content: [] } })  // 标签占位
])

// 安全提取笔记列表
notes.value = Array.isArray(notesRes.data?.data?.content)
  ? notesRes.data.data.content
  : []

// 安全提取分类列表
const categoryList = catsRes.data?.data?.content || []
categories.value = categoryList.map(c => ({
  id: c.id,
  label: c.name,      // name -> label 映射
  children: []
}))

// 安全提取标签列表
tags.value = tagsRes.data?.data?.content || []
```

**类型断言：**
- `notesRes.data?.data?.content` - 使用可选链和空值合并
- `Array.isArray()` - 验证是否为数组
- `|| []` - 空值回退

---

### 3.2 表单提交数据

#### handleSaveNote 函数（App.vue）

**提交对象：**

```javascript
{
  id: noteData.id || null,
  title: '无标题笔记',        // 默认值
  content: noteData.content,  // 必需
  categoryId: noteData.categoryId || null,
  tags: noteData.tags         // 可能是 Number[] 或 Object[]
}
```

**NoteEditor 保存事件数据：**

```javascript
{
  ...props.note,              // 注入原始笔记对象
  content: noteContent.value, // 更新后的内容
  tags: selectedTags.value    // 选中的标签ID数组
}
```

**类型转换示例：**
```javascript
// NoteEditor 中的标签处理
selectedTags.value = Array.isArray(newNote.tags)
  ? newNote.tags.map(t => t.id ?? t)  // 处理对象或ID
  : []
```

---

### 3.3 数据绑定示例

#### 分类选择器绑定（NoteEditor.vue）

```html
<el-select v-model="note.categoryId" placeholder="选择分类">
  <el-option
    v-for="cat in categories"
    :key="cat.id"
    :label="cat.name"
    :value="cat.id"
  />
</el-select>
```

- `note.categoryId` - 绑定到 Number 类型
- `categories` - `Array<{ id: Number, name: String }>`

#### 标签多选绑定（NoteEditor.vue）

```html
<el-select v-model="selectedTags" multiple placeholder="添加标签">
  <el-option
    v-for="tag in tags"
    :key="tag.id"
    :label="tag.name"
    :value="tag.id"
  />
</el-select>
```

- `selectedTags` - 绑定到 `Array<Number>` 类型
- `tags` - `Array<{ id: Number, name: String }>`

---

### 3.4 时间格式处理

#### 格式化函数（NoteEditor.vue）

```javascript
const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
```

**时间格式：**
- 输入：`String` (ISO 8601) 或 `Date` 对象
- 输出：`String` (本地化时间字符串)

#### 相对时间函数（NoteCard.vue）

```javascript
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
```

---

## 4. 发现的类型问题

### 4.1 ID 类型不一致

**问题描述：**
- 后端使用 `Long` 类型（Java），可能返回大整数
- 前端 JavaScript 的 `Number.MAX_SAFE_INTEGER` 是 `2^53 - 1`
- 超出范围的 ID 可能会被截断

**影响组件：**
- 所有涉及 ID 的组件

**建议：**
- 评估是否需要将 ID 改为 `String` 类型
- 或使用 BigInt 处理大数字

---

### 4.2 Tags 类型不一致

**问题描述：**
- API 响应中的 `tags` 是对象数组：`[{ id: 1, name: '标签1' }, ...]`
- 提交时期望的是 ID 数组：`[1, 2, 3]`
- NoteEditor 中有兼容处理逻辑，但不明确

**代码示例（NoteEditor.vue）：**

```javascript
selectedTags.value = Array.isArray(newNote.tags)
  ? newNote.tags.map(t => t.id ?? t)  // 对象提取id，或直接使用
  : []
```

**提交时的数据：**

```javascript
emit('save', {
  ...props.note,
  content: noteContent.value,
  tags: selectedTags.value  // 直接使用ID数组
})
```

**建议：**
- 统一前后端的 tags 字段类型
- 如果后端只接受 ID 数组，保持前端提交格式一致
- 添加类型注释说明 tags 在不同场景下的格式

---

### 4.3 时间格式不一致

**问题描述：**
- 部分地方使用 `Date` 对象，部分地方使用字符串
- 接口返回的是 ISO 8601 字符串
- 前端处理时混合使用

**影响位置：**

```javascript
// 排序时
switch (props.sortBy) {
  case 'time':
    return notes.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
    //               ^^^^^^^^^^^^^          ^^^^^^^^^^^^^
    //               需要转换              需要转换
}
```

**建议：**
- 统一使用 ISO 8601 字符串
- 避免频繁创建 Date 对象
- 或者在获取数据时统一转换为 Date 对象

---

### 4.4 API 响应拦截器的隐患

**问题描述：**
- 拦截器检查 `response.data.content !== undefined`
- 如果后端某条记录的 content 字段为空字符串 `""`，会被误判为非分页响应

**代码：**

```javascript
if (response.data && response.data.content !== undefined) {
  response.data = response.data.content
}
```

**潜在问题：**
- 如果 API 返回 `{ data: { content: "" } }`（空内容），不会被解包
- 如果 API 返回 `{ data: { content: [] } }`（空数组），会被正确解包

**建议：**
- 使用更明确的判断逻辑
- 或添加分页标识字段（如 `total`, `page`）来判断是否分页响应

---

### 4.5 Category 字段名不一致

**问题描述：**
- 后端返回 `name` 字段
- App.vue 中映射为 `label` 字段
- NoteEditor.vue 中使用 `cat.name`
- NoteCard.vue 中使用 `tag.name`

**代码（App.vue）：**

```javascript
categories.value = categoryList.map(c => ({
  id: c.id,
  label: c.name,      // name -> label
  children: []
}))
```

**代码（NoteEditor.vue）：**

```html
<el-option v-for="cat in categories" :label="cat.name" />
<!--                                           ^^^ -->
```

**影响：**
- 如果 Sidebar 使用 `label`，NoteEditor 使用 `name`，可能导致不一致

**建议：**
- 统一字段命名（建议使用 `name`）
- 或统一转换为 `label`

---

### 4.6 响应数据结构不一致

**问题描述：**
- `getAll()` 可能返回数组（被拦截器解包后）
- `getById()` 返回单个对象
- App.vue 中使用解包逻辑，但访问路径不统一

**代码（App.vue）：**

```javascript
// 安全提取笔记列表
notes.value = Array.isArray(notesRes.data?.data?.content)
  ? notesRes.data.data.content
  : []

// 但这里假设拦截器已经解包了？
const savedNote = res.data  // 这里使用的是解包后的 data
```

**问题：**
- 拦截器会修改 `response.data`
- 但代码中混合使用 `data.data.content` 和 `data`
- 可能导致数据路径错误

**建议：**
- 明确拦截器行为
- 统一使用解包后的数据访问方式
- 添加类型检查和错误处理

---

### 4.7 批量操作的类型处理

**问题描述：**
- NoteList 中的批量操作（delete, move）接收标签ID数组
- 目前未实现，类型定义不明确

**代码（NoteList.vue）：**

```javascript
const selectedNotes = ref([])  // 是 ID 数组还是笔记对象数组？

const handleBatchDelete = () => {
  emit('batchDelete', selectedNotes.value)
}
```

**建议：**
- 明确定义 `selectedNotes` 的类型
- 添加类型注释说明
- 实现时验证参数类型

---

## 5. 类型安全建议

### 5.1 添加 JSDoc 类型注释

```javascript
/**
 * @typedef {Object} Note
 * @property {number} id
 * @property {string} title
 * @property {string} [content]
 * @property {number|null} categoryId
 * @property {(Array<number>|Array<Tag>)} tags
 * @property {boolean} isStarred
 * @property {number} [viewCount]
 * @property {string} createdAt
 * @property {string} updatedAt
 */

/**
 * @type {Note[]}
 */
const notes = ref([])
```

### 5.2 使用 PropTypes 或 TypeScript

**Vue 3 + TypeScript 示例：**

```typescript
interface Note {
  id: number | null;
  title: string;
  content: string;
  categoryId: number | null;
  tags: number[];
  isStarred: boolean;
  viewCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

defineProps<{
  note: Note | null;
  categories: Category[];
  tags: Tag[];
}>()
```

### 5.3 添加运行时类型验证

```javascript
function validateNote(note) {
  return {
    isValid: typeof note.id === 'number' || note.id === null,
    errors: [
      typeof note.title !== 'string' ? 'title must be string' : null,
     typeof note.content !== 'string' ? 'content must be string' : null
    ].filter(Boolean)
  }
}
```

---

## 6. 总结

### 6.1 类型分布

| 类型类别 | 数量 | 说明 |
|----------|------|------|
| 基础数据模型 | 3 | Note, Category, Tag |
| 组件 | 4 | App, NoteEditor, NoteList, NoteCard |
| API 端点组 | 3 | Notes, Categories, Files |
| 发现的类型问题 | 7 | 详见第 4 节 |

### 6.2 关键发现

1. **Tags 处理**：对象/ID 混合使用，需要统一
2. **ID 范围**：大整数可能被截断
3. **时间格式**：混合使用 Date 和字符串
4. **API 拦截器**：逻辑可能导致误判
5. **字段命名不一致**：category 的 name/label

### 6.3 优先级建议

| 优先级 | 问题 | 建议 |
|--------|------|------|
| 高 | Tags 类型不一致 | 统一为对象数组或 ID 数组 |
| 高 | API 拦截器逻辑 | 改进分页判断逻辑 |
| 中 | ID 范围问题 | 评估是否需要转 String |
| 中 | 时间格式 | 统一使用 ISO 8601 |
| 低 | 字段命名 | 统一 name/label |

---

**文档生成时间：** 2026-03-10
**项目路径：** /root/.openclaw/workspace/knowledge-base/frontend
