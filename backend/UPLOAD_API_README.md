# 文件上传 API 使用说明

## 概述

本项目实现了 Markdown 文件上传和批量导入功能，支持：
- 单文件上传（.md 或 .markdown 格式）
- 批量文件导入（并行处理）
- 自动解析 frontmatter 元数据
- 自动提取标题、标签、分类
- 文件大小限制（最大 5MB）

---

## API 接口

### 1. 单文件上传

**接口地址：** `POST /api/notes/upload`

**请求类型：** `multipart/form-data`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file   | File | 是   | 上传的 Markdown 文件 |

**请求示例：**

```bash
curl -X POST http://localhost:8080/api/notes/upload \
  -F "file=@example.md"
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "noteId": 1,
    "title": "示例文档",
    "message": "文件上传成功",
    "success": true
  }
}
```

**错误响应示例：**

```json
{
  "code": 400,
  "message": "不支持的文件类型，仅支持 .md 或 .markdown 文件",
  "data": null
}
```

---

### 2. 批量导入

**接口地址：** `POST /api/notes/import`

**请求类型：** `multipart/form-data`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files  | File[] | 是   | 上传的文件列表 |

**请求示例：**

```bash
curl -X POST http://localhost:8080/api/notes/import \
  -F "files=@example1.md" \
  -F "files=@example2.md" \
  -F "files=@example3.md"
```

**响应示例：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 3,
    "success": 2,
    "failed": 1,
    "results": [
      {
        "noteId": 1,
        "title": "示例文档 1",
        "message": "文件上传成功",
        "success": true
      },
      {
        "noteId": 2,
        "title": "示例文档 2",
        "message": "文件上传成功",
        "success": true
      },
      {
        "noteId": null,
        "title": null,
        "message": "example3.md: 不支持的文件类型",
        "success": false
      }
    ]
  }
}
```

---

## Frontmatter 元数据支持

### YAML 格式

```markdown
---
title: 笔记标题
tags: tag1, tag2, tag3
category: 技术文档
author: 作者名
---

# 正文内容

这里是笔记的主要内容...
```

### JSON 格式

```markdown
```json
{
  "title": "笔记标题",
  "tags": "tag1, tag2, tag3",
  "category": "技术文档"
}
````

# 正文内容
...
```
```

### 无 Frontmatter

如果没有 frontmatter，系统将：
1. 自动提取第一个一级标题作为标题
2. 如果没有标题，使用文件名（去除扩展名）作为标题
3. 自动扫描 `#tag` 格式的标签

---

## 文件校验规则

### 文件扩展名
- 支持 `.md` 或 `.markdown` 格式

### 文件大小
- 最大限制：5MB

### 字符编码
- 建议使用 UTF-8 编码

---

## 标题提取优先级

1. **frontmatter 中的 title 字段**（最高优先级）
2. **文件中的第一个一级标题**（# 标题）
3. **文件名**（去除扩展名，转换为标题格式）

---

## 使用示例

### 示例 1：简单文档

**文件名：** `hello-world.md`

**内容：**

```markdown
# Hello World

这是我的第一篇笔记。
```

**结果：**
- 标题：Hello World
- 内容：# Hello World\n\n这是我的第一篇笔记。
- 标签：空
- 分类：空

---

### 示例 2：带 Frontmatter 的文档

**文件名：** `java-spring-boot-guide.md`

**内容：**

```markdown
---
title: Spring Boot 入门指南
tags: Java, Spring Boot, 后端开发
category: 编程教程
created_at: 2026-03-07
---

# Spring Boot 快速开始

Spring Boot 是简化 Spring 应用开发的框架...
```

**结果：**
- 标题：Spring Boot 入门指南
- 标签：Java, Spring Boot, 后端开发
- 分类：编程教程
- 内容：# Spring Boot 快速开始...

---

### 示例 3：带内联标签的文档

**文件名：** `daily-note.md`

**内容：**

```markdown
# 今日学习

今天学习了 #Vue3 和 #TypeScript 的组合使用。

## 前端开发

...
```

**结果：**
- 标题：今日学习
- 标签：Vue3, TypeScript
- 分类：空

---

## 前端集成示例

### Axios 请求封装

```javascript
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080'

// 单文件上传
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  return axios.post(`${API_BASE_URL}/api/notes/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 批量导入
export function importFiles(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })

  return axios.post(`${API_BASE_URL}/api/notes/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
```

### Element Plus 上传组件

```vue
<template>
  <div>
    <!-- 单文件上传 -->
    <el-upload
      action="/api/notes/upload"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
    >
      <el-button type="primary">上传文件</el-button>
    </el-upload>

    <!-- 批量上传 -->
    <el-upload
      action="/api/notes/import"
      multiple
      drag
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        拖拽文件到此处或 <em>点击上传</em>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
const beforeUpload = (file) => {
  // 验证文件类型
  const isMarkdown = file.name.endsWith('.md') || file.name.endsWith('.markdown')
  if (!isMarkdown) {
    ElMessage.error('仅支持 .md 或 .markdown 文件')
    return false
  }

  // 验证文件大小（5MB）
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }

  return true
}

const handleSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    // 刷新笔记列表
  } else {
    ElMessage.error(response.message)
  }
}

const handleError = (error) => {
  ElMessage.error('上传失败')
}
</script>
```

---

## 异常处理

系统已内置全局异常处理器，会统一返回以下错误格式：

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 400    | 参数错误、文件格式错误、文件大小超出限制 |
| 500    | 系统错误、数据库错误 |

---

## 性能优化

- 批量导入使用线程池并行处理（默认 10 个线程）
- 支持同时上传多个文件
- 完善的日志记录，方便问题排查

---

## 开发注意事项

1. **文件编码：** 建议统一使用 UTF-8 编码
2. **分类关联：** 当前分类关联逻辑需根据实际需求扩展
3. **线程池配置：** 可根据服务器性能调整线程池大小
4. **文件存储：** 本实现直接解析文件内容，不进行物理存储

---

**文档版本：** v1.0
**更新时间：** 2026-03-07
