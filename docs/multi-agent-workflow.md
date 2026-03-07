# Multi-Agent 开发流程

## 概述

基于 dev-team skill 的最小多 Agent 开发架构，支持从需求到交付的完整软件开发生命周期。

---

## Agent 架构

### 完整团队

| Agent | 角色 | 工作区 | 启动方式 |
|-------|------|--------|----------|
| planner-agent | 需求分析 & 任务规划 | ~/.openclaw/workspace/planner | sessions_spawn (subagent) |
| design-agent | UI/UX 设计 | ~/.openclaw/workspace/design | sessions_spawn (subagent) |
| dev-agent | 开发实现 | ~/.openclaw/workspace/dev | sessions_spawn (subagent) |
| test-agent | 测试验证 | ~/.openclaw/workspace/test | sessions_spawn (subagent) |
| review-agent | 代码审查 | ~/.openclaw/workspace/review | sessions_spawn (subagent) |
| browser-agent | 浏览器/查资料 | - | skill agent-browser |

### 核心原则

- **统一使用 subagent** - 所有开发 agent 使用 subagent runtime
- **最小架构** - 只保留必要的 agent
- **复用已有** - dev-agent、browser-agent 复用现有
- **并行优先** - 独立任务并行执行
- **质量第一** - 必须经过测试和审查
- **稳定执行** - dev 阶段完成后自动提交代码，工作流自动结束

---

## 完整开发流程

### 标准流程（完整功能）

```
┌─────────────────────────────────────────────────────────────┐
│ 1. 用户提出需求                                              │
│    示例："我想做一个用户管理系统"                            │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. planner-agent                                            │
│ 任务：分析需求，拆解任务，生成开发计划                        │
│ 输出：docs/projects/plan/plan-xxx.md                         │
│                                                             │
│ 包含内容：                                                   │
│ - 需求分析                                                   │
│ - 功能拆解                                                   │
│ - 技术栈选择                                                 │
│ - 开发步骤                                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. design-agent                                             │
│ 任务：生成 UI/UX 方案，Vue 页面结构                          │
│ 输出：docs/projects/design/design-xxx.md                     │
│                                                             │
│ 包含内容：                                                   │
│ - UI 设计图（描述/结构）                                     │
│ - 页面组件拆分                                               │
│ - 样式建议                                                   │
│ - 交互流程                                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. dev-agent（并行执行）                                     │
│ 任务：实现代码（前端 + 后端）                                 │
│ 输出：dev/xxx/frontend/ 和 dev/xxx/backend/                 │
│                                                             │
│ 要求：                                                       │
│ - 遵循 docs/projects/development-standards.md               │
│ - 代码清晰可读                                               │
│ - 包含必要注释                                               │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. test-agent                                               │
│ 任务：生成测试用例，验证功能                                   │
│ 输出：docs/projects/test/test-xxx.md                         │
│                                                             │
│ 包含内容：                                                   │
│ - 测试用例                                                   │
│ - 测试结果                                                   │
│ - Bug 列表                                                   │
│ - 性能数据                                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. review-agent                                             │
│ 任务：代码审查，质量检查                                      │
│ 输出：docs/projects/review/review-xxx.md                     │
│                                                             │
│ 包含内容：                                                   │
│ - 代码规范检查                                               │
│ - 安全性检查                                                 │
│ - 性能分析                                                   │
│ - 优化建议                                                   │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. 整合结果 → 交付给用户                                      │
│ 输出：完整交付物列表 + 下一步建议                               │
└─────────────────────────────────────────────────────────────┘
```

### 快速流程（小功能）

```
┌─────────────────────────────────────────────────────────────┐
│ 1. 用户提出简单需求                                          │
│    示例："把按钮改成绿色"                                     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. dev-agent（直接开发）                                      │
│ 任务：快速实现，简单测试                                      │
│ 输出：修改的具体文件和代码                                    │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. 整合结果 → 交付给用户                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 进度汇报

### 阶段性汇报

每个阶段完成后向用户汇报：

```
✅ 阶段 X 已完成
- 交付物：[路径]
- 耗时：X 分钟
- 下一步：[即将执行的任务]
```

示例：
```
✅ 需求分析完成
- 交付物：docs/projects/plan/user-management-20260306.md
- 耗时：5 分钟
- 下一步：启动 UI 设计
```

---

## 任务识别规则

### 标准流程（完整开发）

当需求满足以下条件之一时：
- 需求描述超过 50 字
- 包含复杂功能描述（多个功能点）
- 对 UI/UX 有明确要求
- 用户说"做一个完整的项目"或"系统"

**示例：**
- "我想做一个用户管理系统，支持增删改查、权限管理、数据导出"
- "优化知识库系统的界面，让它更好看"

### 快速流程（小功能）

当需求满足以下条件之一时：
- 简单的 bug 修复
- 小功能增删改
- 文档更新
- 配置调整

**示例：**
- "这个按钮改个颜色"
- "修复登出功能 bug"

---

## 并行处理策略

### 独立任务并行

```
1. 前端开发（dev-agent-frontend）
2. 后端开发（dev-agent-backend）

两个 agent 同时启动，并行执行
```

### 前置依赖等待

```
1. planner-agent 必须先完成
2. design-agent 等待 planner 完成
3. dev-agent 等待 design 完成
4. test-agent 等待 dev 完成
5. review-agent 等待 test 完成
```

---

## 文档结构

### 项目文档

```
docs/projects/
├── plan/              # 开发计划
│   └── plan-xxx.md
├── design/            # UI 设计
│   └── design-xxx.md
├── test/              # 测试报告
│   └── test-xxx.md
└── review/            # 审查报告
    └── review-xxx.md
```

### 代码目录

```
dev/
├── frontend/          # 前端代码
│   ├── src/
│   ├── components/
│   └── pages/
└── backend/           # 后端代码
    ├── controller/
    ├── service/
    └── repository/
```

---

## 开发规范

遵循 `docs/projects/development-standards.md`：
- Vue 3 + TypeScript 前端规范
- Java + Spring Boot 后端规范
- Git 提交规范
- 注释规范

---

## 示例：完整开发流程

### 用户需求

"我想做一个用户管理系统，支持增删改查、权限管理、数据导出"

### 执行流程

```bash
# 1. planner-agent
sessions_spawn --session planner-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "分析需求：做一个用户管理系统，包括增删改查、权限管理、数据导出"

# 2. design-agent
sessions_spawn --session design-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "根据开发计划 docs/projects/plan/user-management-xxx.md 设计 UI"

# 3. dev-agent（前端 + 后端并行）
sessions_spawn --session dev-agent-frontend-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "实现前端界面，完成后输出 DEV COMPLETE"

sessions_spawn --session dev-agent-backend-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "实现后端 API，完成后输出 DEV COMPLETE"

# 4. 自动提交代码（dev 完成后自动执行）
git add .
git commit -m "feat: 按开发计划完成用户管理系统"
git push

# 5. 整合结果并交付
```

---

## 关键特性

1. **完全自主** - 无需用户干预
2. **并行执行** - 前后端并发开发
3. **质量保证** - 测试 + 审查双重保障
4. **文档齐全** - 每个阶段都有完整文档
5. **无需重启** - 所有配置无需重启服务

---

**版本：** v1.0
**更新：** 2026-03-06
