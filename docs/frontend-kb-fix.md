# Frontend-KB 项目修复指南

## 当前问题

1. dev-agent 尝试启动 ACP runtime agent
2. ACP runtime 不可用，导致 fallback 到 subagent
3. dev 阶段可能长时间卡住，无持续输出
4. workflow 结束逻辑不清晰

---

## 修复方案

### 一、Workflow 执行逻辑

#### 修复后的流程

```
planner（已完成）→ design（已完成）→ dev（修复中）→ git commit → git push → workflow end
```

#### 不执行阶段

- ❌ test-agent（本阶段不执行）
- ❌ review-agent（本阶段不执行）

#### Workflow 自动结束触发条件

1. 前端 dev-agent 输出 `DEV COMPLETE`
2. 后端 dev-agent（如果有）输出 `DEV COMPLETE`
3. 代码自动提交和推送

---

### 二、Dev-Agent 修复

#### 1. 统一使用 subagent

```bash
# ❌ 修复前（尝试 ACP runtime）
sessions_spawn \
  --runtime acp \
  --agentId codex \
  --model claude-sonnet-4.1 \
  --mode run \
  --task "实现前端界面"

# ✅ 修复后（使用 subagent）
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "实现前端界面，完成后输出 DEV COMPLETE"
```

#### 2. 持续输出进度

任务描述中必须包含输出要求：

```bash
--task "根据开发计划实现知识库前端：

1. UI 组件库引入（Element Plus）
2. Markdown 编辑器集成
3. 布局重构
4. MD 文件上传功能
5. 视图优化

输出要求：
- 每 30 秒报告一次进度
- 每生成一个文件输出文件路径
- 完成后输出 DEV COMPLETE
- 5 分钟无输出则报告当前状态

限制：
- 不允许使用 ls -R 扫描仓库
- 只读取必要文件（package.json, src/ 相关）
- 遇到错误跳过，继续下一任务"
```

#### 3. 防止长时间卡住

在任务描述中添加超时检查：

```bash
"⚠️ 5分钟监控要求：
如果 5 分钟内没有生成代码或日志输出，
必须输出当前执行状态：

⏱️ 执行状态：
- 当前步骤：[具体名称]
- 已完成：[已生成的代码]
- 待完成：[未完成的任务]
- 文件列表：[已创建的文件]"
```

---

### 三、Dev-Agent-Frontend-KB 执行示例

```bash
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --cwd /root/.openclaw/workspace/knowledge-base/frontend \
  --timeoutSeconds 3600 \
  --task "开发知识库前端，参考设计文档 /root/.openclaw/workspace/docs/projects/design/kb-ui-design-20260306.md

## 开发任务

### 阶段一：UI 组件库引入
1. 安装依赖：element-plus, @element-plus/icons-vue
2. 在 main.js 中全局引入 Element Plus
3. 配置主题色（ Obsidian 深色主题）
4. 替换 button, input, select 为 Element Plus 组件

### 阶段二：Markdown 编辑器集成
1. 安装依赖：mavon-editor
2. 在笔记编辑表单中集成 mavon-editor
3. 配置工具栏（加粗、斜体、代码块）
4. 实现保存功能

### 阶段三：布局重构
1. 重构 App.vue 布局结构
2. 添加顶部导航栏
3. 添加侧边栏（分类树）
4. 优化笔记卡片样式

### 阶段四：MD 文件上传功能
1. 添加文件上传组件（drag & drop）
2. 前端文件读取和解析（.md 文件）
3. 批量文件支持
4. 上传进度显示

### 阶段五：视图优化
1. 优化笔记卡片样式
2. 实现列表/网格切换
3. 搜索界面优化
4. 暗色/亮色主题切换

## 输出要求

1. 每 30 秒报告一次进度，格式：
   [时间] 阶段 X：[任务名称]
   [时间] - [具体步骤]

2. 每生成一个文件，输出：
   ✅ 已创建：[文件路径]

3. 遇到错误，输出：
   ❌ 错误：[错误描述]
   ⏭️ 继续下一任务

4. 完成后，输出：
   DEV COMPLETE

## 限制

1. 不允许使用 ls -R 扫描仓库
2. 只读取必要文件：
   - package.json
   - src/App.vue
   - src/main.js
   - docs/projects/design/kb-ui-design-20260306.md
3. 遇到错误跳过，不要中断
4. 每个文件生成后立即写入

## 5分钟监控

如果 5 分钟内没有生成代码或日志，输出：
⏱️ 执行状态：正在 [当前步骤]
已生成文件：[文件列表]
下一步：[即将处理的任务]"
```

---

### 四、Dev 完成后的自动提交

Workflow 监控所有 dev-agent 的输出，当都输出 `DEV COMPLETE` 时：

```bash
# 自动执行
git add .
git commit -m "kb redesign: ui redesign + md upload"
git push

# 输出
✅ 开发完成，已提交代码
- 提交信息：kb redesign: ui redesign + md upload
- 推送状态：success

🎉 Workflow 完成
```

---

## 调试命令

### 检查 dev-agent 状态

```bash
# 列出所有子 agent
subagents --action list

# 查看详细日志
sessions_history --sessionKey dev-agent-frontend-kb --limit 100

# 实时查看当前执行的任务
sessions_list --limit 10
```

### 手动终止卡住的任务

```bash
subagents --action kill --target dev-agent-frontend-kb
```

---

## 验证修复

### 1. 检查是否使用 subagent

查看日志，确认没有 ACP 相关输出：

```bash
grep -i "acp\|codex" /tmp/openclaw/openclaw-*.log
# 应该没有输出
```

### 2. 检查持续输出

```bash
tail -f /tmp/openclaw/openclaw-*.log | grep "dev-agent"
# 应该看到持续的进度输出
```

### 3. 检查 DEV COMPLETE

```bash
grep "DEV COMPLETE" /tmp/openclaw/openclaw-*.log
# 应该找到输出
```

---

## 回滚到 planner/design？不需要！

**不要重新执行 planner 和 design**

- planner 和 design 已经完成
- 文档已生成
- 开发计划已确认

**只修复 dev 阶段**

---

## 修改清单

| 项目 | 状态 |
|------|------|
| ✅ workflow 文档 | 已修复 `docs/multi-agent-workflow.md` |
| ✅ dev-agent 执行指南 | 已创建 `docs/dev-agent-execution-guide.md` |
| ✅ 修复指南 | 已创建本文件 |
| 📋 dev-agent-frontend-kb | 待执行 |

---

## 执行 dev-agent

```bash
# 确保 plannner 和 design 已完成
ls -l docs/projects/plan/kb-redesign-20260306.md
ls -l docs/projects/design/kb-ui-design-20260306.md

# 执行 dev-agent（使用本指南的任务描述）
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --cwd /root/.openclaw/workspace/knowledge-base/frontend \
  --timeoutSeconds 3600 \
  --task "参考 /root/.openclaw/workspace/docs/frontend-kb-fix.md 的开发任务执行"
```

---

**版本：** v1.0
**创建：** 2026-03-07
**维护：** 小克 🐕💎
