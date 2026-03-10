# Stage 3: 数据库表结构检查报告

## 项目信息
- 项目路径: `/root/.openclaw/workspace/knowledge-base`
- 检查时间: 2026-03-10
- 数据库名: `knowledge_db`
- 字符集: `utf8mb4`

---

## 1. 数据库表结构

### 1.1 Category（分类表）

#### 表定义语句
```sql
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';
```

#### 字段类型定义表

| 字段名 | 数据类型 | 长度 | NULL | 默认值 | 约束 | 说明 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | PRIMARY KEY | 分类主键 |
| name | VARCHAR | 100 | NO | - | - | 分类名称 |
| description | VARCHAR | 255 | YES | NULL | - | 分类描述 |
| created_at | TIMESTAMP | - | NO | CURRENT_TIMESTAMP | - | 创建时间 |

#### 索引
- PRIMARY KEY: `id`
- INDEX: `idx_name(name)` - 分类名称索引

---

### 1.2 Note（笔记表）

#### 表定义语句
```sql
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容（Markdown格式）',
    category_id BIGINT COMMENT '分类ID',
    tags VARCHAR(255) COMMENT '标签（逗号分隔）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category (category_id),
    KEY idx_title (title),
    FULLTEXT INDEX idx_content (title, content),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记表';
```

#### 字段类型定义表

| 字段名 | 数据类型 | 长度 | NULL | 默认值 | 约束 | 说明 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | PRIMARY KEY | 笔记主键 |
| title | VARCHAR | 200 | NO | - | - | 笔记标题 |
| content | TEXT | - | NO | - | - | 笔记内容（Markdown格式） |
| category_id | BIGINT | - | YES | NULL | FOREIGN KEY | 分类ID |
| tags | VARCHAR | 255 | YES | NULL | - | 标签（逗号分隔） |
| created_at | TIMESTAMP | - | NO | CURRENT_TIMESTAMP | - | 创建时间 |
| updated_at | TIMESTAMP | - | NO | CURRENT_TIMESTAMP | ON UPDATE | 更新时间 |

#### 索引
- PRIMARY KEY: `id`
- INDEX: `idx_category(category_id)` - 分类ID索引
- INDEX: `idx_title(title)` - 标题索引
- FULLTEXT INDEX: `idx_content(title, content)` - 全文搜索索引

#### 外键约束
- `category_id` → `category(id)` ON DELETE SET NULL
  - 当分类被删除时，该分类下的笔记的 category_id 会被设置为 NULL

---

## 2. 约束条件说明

### 2.1 主键约束
- `category.id`: BIGINT AUTO_INCREMENT，自增长主键
- `note.id`: BIGINT AUTO_INCREMENT，自增长主键

### 2.2 外键约束
- `note.category_id` 引用 `category.id`
  - 约束: ON DELETE SET NULL
  - 含义: 当删除分类时，关联的笔记不会被删除，其 category_id 字段会被置为 NULL

### 2.3 非空约束
- `category.name`: 必须提供分类名称
- `note.title`: 必须提供笔记标题
- `note.content`: 必须提供笔记内容

### 2.4 索引
#### 普通索引
- `category.idx_name`: 用于快速按名称查找分类
- `note.idx_category`: 用于快速按分类筛选笔记
- `note.idx_title`: 用于快速按标题查找笔记

#### 全文索引
- `note.idx_content`: 支持对标题和内容进行全文搜索（MySQL 的 FULLTEXT 索引特性）

---

## 3. 数据类型特征

### 3.1 字符串类型
| 类型 | 用途 | 特点 |
|------|------|------|
| VARCHAR(100) | 分类名称 | 变长字符串，最大100字符 |
| VARCHAR(200) | 笔记标题 | 变长字符串，最大200字符 |
| VARCHAR(255) | 分类描述、标签 | 变长字符串，最大255字符 |
| TEXT | 笔记内容 | 大文本，不支持默认值，存储较大内容 |

### 3.2 数值类型
| 类型 | 用途 | 范围 |
|------|------|------|
| BIGINT | 主键ID | -9,223,372,036,854,775,808 ~ 9,223,372,036,854,775,807 |

### 3.3 日期时间类型
| 类型 | 用途 | 特点 |
|------|------|------|
| TIMESTAMP | 创建时间、更新时间 | 自动记录时间戳，支持自动更新 |

---

## 4. 与后端 Entity 的映射关系

### 4.1 Category 实体映射

| 数据库字段 | Entity 属性 | Java 类型 | 注解 |
|-----------|------------|-----------|------|
| id | id | Long | @Id, @GeneratedValue(IDENTITY) |
| name | name | String | @Column(nullable=false, length=100) |
| description | description | String | @Column |
| created_at | createdAt | LocalDateTime | @Column(name="created_at") |

#### Entity 特殊属性
- `noteCount` (Integer, @Transient): 不持久化到数据库，用于统计该分类下的笔记数量

#### 验证注解
- `name`: @NotBlank, @Size(max=100)

---

### 4.2 Note 实体映射

| 数据库字段 | Entity 属性 | Java 类型 | 注解 |
|-----------|------------|-----------|------|
| id | id | Long | @Id, @GeneratedValue(IDENTITY) |
| title | title | String | @Column(nullable=false) |
| content | content | String | @Column(columnDefinition="TEXT") |
| category_id | category, categoryId | Category, Long | @ManyToOne, @JoinColumn(name="category_id") |
| tags | tags | String | @Column(columnDefinition="VARCHAR(500)") |
| content_hash | contentHash | String | @Column(nullable=false) |
| created_at | createdAt | LocalDateTime | @Column(name="created_at") |
| updated_at | updatedAt | LocalDateTime | @Column(name="updated_at") |

#### Entity 特殊属性和方法

**categoryId 转换机制:**
```java
@JsonProperty("categoryId")
public Long getCategoryId() {
    return category != null ? category.getId() : null;
}

@JsonProperty
public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
}
```
- 前端发送 categoryId 时自动转换为 Category 对象
- 支持直接通过 categoryId 设置关联的分类

**tags 转换机制:**
```java
@JsonProperty("tags")
public List<String> getTagsList() {
    return tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>();
}

@JsonProperty("tags")
public void setTagsList(List<String> tagsList) {
    this.tags = tagsList != null ? String.join(",", tagsList) : "";
}
```
- 数据库存储为逗号分隔的字符串
- 对外暴露为 String 列表，自动转换

**自动更新时间:**
```java
@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```
- 更新实体时自动设置 updatedAt 为当前时间

#### 验证注解
- `title`: @NotBlank, @Size(max=200)
- `content`: @Size(max=10000)
- `tags`: @Size(max=500)

---

## 5. 发现的差异说明

### 5.1 数据库 vs Entity 字段差异

**Note 表差异:**
1. **content_hash 字段**
   - 数据库: ❌ 未定义
   - Entity: ✅ 存在 (`@Column(name="content_hash", nullable=false)`)
   - 说明: 后端 Entity 定义了内容哈希字段，但数据库表结构中未包含。需要同步更新数据库结构或在 init.sql 中添加该字段定义

2. **tags 字段长度**
   - 数据库: VARCHAR(255)
   - Entity: @Size(max=500) - 验证允许500字符，但列定义为 VARCHAR(500)
   - 说明: Entity 注解长度为500，但数据库定义为255，存在不一致隐患

### 5.2 建议
1. **同步数据库结构**: 需要在 init.sql 的 note 表中添加 `content_hash VARCHAR(64) NOT NULL COMMENT '内容哈希值'` 字段定义
2. **统一 tags 字段长度**: 建议将数据库的 tags 字段从 VARCHAR(255) 修改为 VARCHAR(500)，与 Entity 验证规则保持一致

---

## 6. 默认数据

### 6.1 初始分类数据

系统初始化时默认添加以下分类：

| ID | 名称 | 描述 |
|----|------|------|
| 1 | 计算机专业课 | 大学计算机专业课相关笔记 |
| 2 | 编程语言 | Java, Python, JavaScript 等编程语言 |
| 3 | 框架技术 | SpringBoot, Vue, Uniapp 等框架 |
| 4 | 算法与数据结构 | 算法、数据结构相关笔记 |
| 5 | 网络安全 | Web渗透测试、安全相关笔记 |
| 6 | 其他 | 其他分类 |

---

## 7. 总结

### 7.1 数据库设计特点
- ✅ 使用 utf8mb4 字符集，支持完整的 Unicode 字符（包括 emoji）
- ✅ 合理的索引设计：普通索引加速查询，全文索引支持内容搜索
- ✅ 外键级联删除设置为 SET NULL，保护数据完整性
- ✅ TIMESTAMP 自动管理创建和更新时间

### 7.2 后端映射特点
- ✅ 使用 JPA 注解进行 ORM 映射
- ✅ 类型安全的实体关系（@ManyToOne）
- ✅ 自定义 JSON 序列化逻辑（categoryId 和 tags 的数组转换）
- ✅ 数据验证注解（@NotBlank, @Size）

### 7.3 需要关注的问题
- ⚠️ content_hash 字段在 Entity 中定义但数据库表中缺失
- ⚠️ tags 字段长度限制在数据库和 Entity 中不一致（255 vs 500）
- ⚠️ 建议创建数据库迁移脚本来同步这些差异

---

**检查完成**
