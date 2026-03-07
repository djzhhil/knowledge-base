# Dev-Team Workflow 修复总结

## 修复时间

2026-03-07

---

## 问题描述

1. dev-agent 尝试启动 ACP runtime agent
2. ACP runtime 不可用，导致 fallback 到 subagent
3. dev 阶段可能长时间卡住，无持续输出
4. workflow 结束逻辑不清晰，dev 完成后仍可能继续等待

---

## 修复内容

### 一、已修复的文件

| 文件 | 修复内容 | 状态 |
|------|----------|------|
| `/root/.openclaw/workspace/docs/multi-agent-workflow.md` | 将 dev-agent 从 acp 改为 subagent，添加 git 提交步骤 | ✅ |
| `/root/.openclaw/workspace/docs/dev-agent-execution-guide.md` | 新建 dev-agent 执行指南 | ✅ |
| `/root/.openclaw/workspace/docs/frontend-kb-fix.md` | 新建前端 KB 项目修复指南 | ✅ |
| `/root/.openclaw/skills/dev-team/README.md` | 统一使用 subagent，更新 workflow 执行逻辑 | ✅ |
| `/root/.openclaw/skills/dev-team/SKILL.md` | 添加持续输出要求，明确 workflow 结束逻辑 | ✅ |

---

## 核心修复点

### 1. 统一使用 subagent runtime

**修复前：**
```bash
sessions_spawn --session dev-agent-$(date +%s) \
  --runtime acp \
  --agentId codex \
  --model claude-sonnet-4.1 \
  --mode run \
  --task "$TASK_DEV"
```

**修复后：**
```bash
sessions_spawn --session dev-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "$TASK_DEV"
```

### 2. 添加持续输出要求

dev-agent 必须：
- 每 30 秒报告一次进度
- 每生成一个文件输出文件路径
- 遇到错误必须输出并跳过

### 3. 5 分钟监控机制

如果 5 分钟内没有输出，必须报告：
```
⏱️ 执行状态：[当前步骤]
已生成文件：[文件列表]
下一步：[即将处理的任务]
```

### 4. 禁止全仓库扫描

```bash
❌ ls -R (递归扫描仓库)
❌ find /root/.openclaw/workspace -type f
❌ 全仓库扫描
```

### 5. 明确完成标志

所有开发任务完成后，必须输出：
```
DEV COMPLETE
```

### 6. 修复 workflow 结束逻辑

**修复前：**
```
planner → design → dev → test → review → 交付
```

**修复后（知识库项目）：**
```
planner → design → dev → git commit → git push → workflow end
```

### 7. 不执行 test 和 review

对于当前项目（frontend-kb）：
- ❌ test-agent：本阶段不执行
- ❌ review-agent：本阶段不执行

---

## Workflow 执行流程（修复后）

### Standard Flow

```
1. planner-agent
   - 生成开发计划
   - 输出：docs/projects/plan/xxx.md

2. design-agent
   - 生成 UI 方案
   - 输出：docs/projects/design/xxx.md

3. dev-agent
   - 实现代码（subagent）
   - 持续输出进度
   - 输出：dev/xxx/
   - 完成标志：DEV COMPLETE

4. 自动提交
   - git add .
   - git commit -m "kb redesign: ui redesign + md upload"
   - git push

5. Workflow 自动结束
```

---

## 验证方法

### 检查是否使用 subagent

```bash
grep -i "acp\|codex" /root/.openclaw/skills/dev-team/*
# 应该没有结果
```

### 检查持续输出要求

```bash
grep -A 5 "每 30 秒" /root/.openclaw/skills/dev-team/*
# 应该找到相关配置
```

### 检查 DEV COMPLETE

```bash
grep "DEV COMPLETE" /root/.openclaw/skills/dev-team/*
# 应该找到相关配置
```

---

## 相关文档

- **执行指南：** `docs/dev-agent-execution-guide.md`
- **项目修复：** `docs/frontend-kb-fix.md`
- **Workflow 文档：** `docs/multi-agent-workflow.md`
- **Skill 文档：** `skills/dev-team/SKILL.md`
- **Skill README：** `skills/dev-team/README.md`

---

## 重要限制

1. ❌ 不要重新执行 planner
2. ❌ 不要重新执行 design
3. ❌ 不要重启 OpenClaw
4. ✅ 只修复 workflow 执行逻辑
5. ✅ 只修复 dev-agent-frontend-kb

---

## 下一步

执行 dev-agent-frontend-kb：

```bash
sessions_spawn \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --cwd /root/.openclaw/workspace/knowledge-base/frontend \
  --timeoutSeconds 3600 \
  --task "根据 /root/.openclaw/workspace/docs/frontend-kb-fix.md 执行开发任务"
```

---

**版本：** v1.0
**修复人：** 小克 🐕💎
**日期：** 2026-03-07
