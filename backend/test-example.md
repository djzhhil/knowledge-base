---
title: Spring Boot 文件上传功能
tags: Spring Boot, Java, 后端开发
category: 编程教程
author: 耀
created_at: 2026-03-07
---

# Spring Boot 文件上传功能

这是一个用于测试文件上传 API 的示例文档。

## 功能特性

- 单文件上传
- 批量文件导入
- Frontmatter 元数据解析
- 自动标题提取
- 标签和分类提取

## 技术栈

- Spring Boot 3.1.5
- Spring Web
- Apache Commons IO
- Lombok

## 使用示例

### 上传单个文件

```bash
curl -X POST http://localhost:8080/api/notes/upload \
  -F "file=@test-example.md"
```

### 批量导入

```bash
curl -X POST http://localhost:8080/api/notes/import \
  -F "files=@test1.md" \
  -F "files=@test2.md"
```

## 注意事项

请确保：
1. 文件格式为 .md 或 .markdown
2. 文件大小小于 5MB
3. 文件使用 UTF-8 编码

---

**最后更新：** 2026-03-07
