# Dev-Agent 执行指南

## 核心原则

**dev-agent 始终使用 subagent runtime**，不使用 ACP runtime。

---

## 执行模式

### Standard Mode（推荐）

使用 subagent 执行开发任务：

```bash
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "执行开发任务" \
  --timeoutSeconds 3600
```

### Important Parameters

| 参数 | 说明 | 推荐值 |
|------|------|--------|
| `--runtime` | 必须是 `subagent` | subagent |
| `--model` | 模型选择 | nvidia/z-ai/glm4.7 |
| `--mode` | 运行模式 | run（一次性执行） |
| `--timeoutSeconds` | 超时时间 | 3600（1小时） |
| `--cleanup` | 完成后处理 | keep（保留会话） |

---

## 任务输出要求

### 持续进度日志

开发过程中必须持续输出进度：

```
[10:00:00] 开始前端开发
[10:00:10] 创建项目结构
[10:00:30] 生成组件代码
[10:01:00] 实现 API 调用
[10:01:30] 写入代码文件
[10:02:00] 前端开发完成
```

### 完成标志

所有开发任务完成后，必须输出：

```
DEV COMPLETE
```

**此标志用于触发自动提交流程。**

---

## 防止卡住措施

### 5分钟超时检查

如果 5 分钟内没有生成代码或日志输出，必须输出当前状态：

```
⏱️ 执行状态更新：
- 当前步骤：[具体步骤]
- 已完成：[已完成的内容]
- 待完成：[待完成的任务]
- 问题：[遇到的问题，如果有]
```

### 输出频率要求

- 每 30 秒至少输出一次状态更新
- 每生成一个文件必须输出文件路径

---

## 文件读取限制

### 禁止操作

```bash
❌ ls -R（递归扫描仓库）
❌ find /root/.openclaw/workspace -type f
❌ 全仓库扫描
```

### 允许操作

```bash
✅ read dev/frontend/src/App.vue
✅ read src/components/NoteCard.vue
✅ grep -r "import " dev/frontend/src/
```

### 只读取必要文件

- 项目配置文件（package.json, pom.xml）
- 当前任务相关的源代码文件
- 开发文档（design.md, plan.md）

---

## dev-agent-frontend-kb 专用配置

### 知识库前端项目

```bash
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --cwd /root/.openclaw/workspace/knowledge-base/frontend \
  --task "根据开发计划实现知识库前端：
1. UI 组件库引入（Element Plus）
2. Markdown 编辑器集成
3. 布局重构
4. MD 文件上传功能
5. 视图优化

输出要求：
- 每 30 秒报告一次进度
- 每生成一个文件输出路径
- 完成后输出 DEV COMPLETE
- 5 分钟无输出则报告状态"
```

### 状态报告模板

```
✅ 任务 1.1：引入 UI 组件库
- package.json 已更新
- main.js 已配置
- 耗时：2 分钟

⏳ 任务 1.2：引入 Markdown 编辑器
- 正在配置 mavon-editor
...
```

---

## 错误处理

### 常见问题

| 问题 | 解决方案 |
|------|----------|
| 文件读取超时 | 只读取必要文件，避免全仓库扫描 |
| 长时间无输出 | 触发 5 分钟状态报告，输出当前步骤 |
| 内存不足 | 分批处理大文件，一次性只处理一个文件 |
| 网络超时 | 使用 web_fetch 而不是浏览器工具 |

### 错误输出

遇到错误时，必须输出：

```
❌ 错误：[错误描述]
- 文件：[文件路径]
- 行号：[行号]
- 原因：[错误原因]

⏭️ 继续处理下一任务
```

---

## Dev 完成后的处理

### 自动触发提交

当所有 dev-agent 输出 `DEV COMPLETE` 后，主 workflow 自动执行：

```bash
git add .
git commit -m "kb redesign: ui redesign + md upload"
git push
```

### Workflow 结束

提交完成后，workflow 自动结束，无需等待用户输入。

---

## 最佳实践

1. **分阶段执行** - 按开发计划逐步执行，不要一次性处理所有任务
2. **持续输出** - 每 30 秒至少报告一次进度
3. **明确完成** - 最后必须输出 `DEV COMPLETE`
4. **限制范围** - 只处理当前任务需要的文件
5. **错误容忍** - 遇到错误跳过，继续下一任务
6. **超时保护** - 设置合理的超时时间，防止无限等待

---

## 调试技巧

### 查看子任务状态

```bash
sessions_list --kinds subagent
```

### 查看子任务日志

```bash
sessions_history --sessionKey dev-agent-frontend-kb --limit 50
```

### 终止卡住的任务

```bash
# 列出所有子任务
subagents --action list

# 终止卡住的任务
subagents --action kill --target dev-agent-frontend-kb
```

---

**版本：** v1.0
**更新：** 2026-03-07
**维护：** 小克 🐕💎
