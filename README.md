# 🐕 知识库系统

一个简单实用的个人知识管理系统，用于记录课程笔记和学习知识。

## 📋 功能特性

- ✅ 笔记增删改查
- ✅ Markdown 格式支持
- ✅ 分类管理
- ✅ 标签系统
- ✅ 搜索功能
- ✅ 按分类筛选

## 🛠️ 技术栈

### 前端
- Vue 3
- Axios
- 原生 CSS

### 后端
- Spring Boot 3.1.5
- Spring Data JPA
- MySQL 8.0

### 开发工具
- 前端：HBuilderX
- 后端：IntelliJ IDEA

## 🚀 快速开始

### 1. 数据库准备

```bash
# 创建数据库
mysql -u root -p < init.sql
```

### 2. 后端启动

```bash
cd backend

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 修改数据库密码

# 运行项目
mvn spring-boot:run
# 或在 IDEA 中直接运行
```

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 在 HBuilderX 中打开 frontend 目录
# 或使用浏览器访问 http://localhost:8081
```

## 📁 项目结构

```
knowledge-base/
├── backend/                    # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/knowledge/
│   │       │   ├── controller/  # 控制层
│   │       │   ├── service/     # 服务层
│   │       │   ├── entity/      # 实体类
│   │       │   └── mapper/      # 数据访问层
│   │       └── resources/
│   │           └── application.yml
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API 接口
│   │   ├── App.vue             # 主组件
│   │   └── main.js             # 入口文件
│   └── package.json
├── init.sql                    # 数据库初始化脚本
└── README.md
```

## 🎯 使用说明

### 创建笔记
1. 点击右上角 "+ 新建笔记" 按钮
2. 填写标题、选择分类、添加标签
3. 编写内容（支持 Markdown）
4. 点击保存

### 搜索笔记
- 在搜索框输入关键词
- 按回车或点击搜索按钮
- 支持标题和内容搜索

### 分类筛选
- 选择不同的分类查看对应笔记

## 🔄 定期同步到 GitHub

### 首次提交

```bash
# 初始化 Git
git init

# 添加所有文件
git add .

# 首次提交
git commit -m "Initial commit: 知识库系统基础版本"

# 添加远程仓库（替换为你的 GitHub 仓库地址）
git remote add origin https://github.com/your-username/knowledge-base.git

# 推送到 GitHub
git push -u origin main
```

### 定期更新

```bash
# 查看修改状态
git status

# 添加修改的文件
git add .

# 提交
git commit -m "Update: 描述你的改动"

# 推送
git push
```

### 在其他设备同步

```bash
# 克隆仓库
git clone https://github.com/your-username/knowledge-base.git

# 更新代码
git pull
```

## 🔧 配置说明

### 后端配置文件

`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/knowledge_db
    username: root
    password: your_password  # 修改为你的密码
```

### 前端 API 地址

`frontend/src/api/index.js`

```javascript
const BASE_URL = 'http://localhost:8080/api'
```

## 📝 待优化功能

- [ ] Markdown 实时预览
- [ ] 导出 PDF/Markdown
- [ ] 版本历史
- [ ] 协作功能
- [ ] 知识图谱可视化
- [ ] 数据备份与恢复

## 🐕 系统信息

- 开发者：耀
- 版本：1.0.0
- 创建时间：2024
- 协作：小克（Multi-Agent 协作）

## 📞 问题反馈

如果遇到问题，请提交 Issue 或直接联系。

---

**Made with ❤️ by 耀 & 小克 🐕💎**
