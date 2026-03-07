# 示例项目：用户管理系统

## 用户需求

"我想做一个用户管理系统，支持增删改查、权限管理、数据导出"

---

## 标准流程执行

### 📋 阶段 1：需求分析 (PM Agent)
**启动：**
```bash
sessions_spawn --session pm-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "分析需求：做一个用户管理系统，包括用户增删改查、权限管理、数据导出"
```

**PM Agent 执行：**
- 分析需求
- 撰写 PRD
- 拆解功能点
- 定义验收标准

**输出：** `docs/projects/prd/user-management-20260306.md`

**内容示例：**
```markdown
# 用户管理系统 PRD

## 功能需求
1. 用户列表（分页、搜索、筛选）
2. 用户详情（查看、编辑、删除）
3. 用户创建（表单验证、权限分配）
4. 权限管理（角色、权限矩阵）
5. 数据导出（Excel、CSV）

## 非功能性需求
- 响应时间 < 500ms
- 支持 1000+ 用户
- 前端：Vue 3 + Element Plus
- 后端：Spring Boot + MySQL

## 验收标准
- 能够完成用户的增删改查
- 权限控制正常
- 数据导出功能正常
```

**Main 汇报：**
```
✅ 需求分析完成
- PRD 文档：docs/projects/prd/user-management-20260306.md
- 耗时：5 分钟
- 下一步：启动设计阶段
```

---

### 🎨 阶段 2：设计规划 (Design Agent)
**启动：**
```bash
sessions_spawn --session design-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "根据 PRD docs/projects/prd/user-management-20260306.md 设计系统架构、数据库、API、UI"
```

**Design Agent 执行：**
- 设计系统架构
- 设计数据库表结构
- 设计 API 接口
- 设计 UI/UX 原型

**输出：** `docs/projects/design/user-management-20260306.md`

**内容示例：**
```markdown
# 用户管理系统设计文档

## 系统架构
```
Frontend (Vue 3)
  ↓ API
Backend (Spring Boot)
  ↓
Database (MySQL)
```

## 数据库设计
```sql
-- 用户表
CREATE TABLE users (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100),
  role_id BIGINT,
  ...
);

-- 角色表
CREATE TABLE roles (
  id BIGINT PRIMARY KEY,
  name VARCHAR(50),
  ...
);
```

## API 设计
```
GET    /api/users              # 用户列表
GET    /api/users/{id}         # 用户详情
POST   /api/users              # 创建用户
PUT    /api/users/{id}         # 更新用户
DELETE /api/users/{id}         # 删除用户
POST   /api/users/export       # 数据导出
```

## UI 设计
- 用户列表页：表格、搜索、筛选、分页
- 用户详情页：表单、权限选择
- 权限管理页：角色列表、权限矩阵
```

**Main 汇报：**
```
✅ 设计阶段完成
- 设计文档：docs/projects/design/user-management-20260306.md
- 耗时：10 分钟
- 下一步：启动开发阶段（前端和后端并行）
```

---

### 💻 阶段 3：代码实现 (Dev Agent)
**启动前端：**
```bash
sessions_spawn --session dev-agent-frontend-$(date +%s) \
  --runtime acp \
  --agentId codex \
  --model claude-sonnet-4.1 \
  --mode run \
  --task "实现前端界面：参考 docs/projects/design/user-management-20260306.md，使用 Vue 3 + Element Plus"
```

**启动后端（并行）：**
```bash
sessions_spawn --session dev-agent-backend-$(date +%s) \
  --runtime acp \
  --agentId codex \
  --model claude-sonnet-4.1 \
  --mode run \
  --task "实现后端 API：参考 docs/projects/design/user-management-20260306.md，使用 Spring Boot + MySQL"
```

**Dev Agent 执行：**
- 前端：创建 Vue 组件、实现页面逻辑、集成 UI 库
- 后端：创建 Controller、Service、Repository，实现业务逻辑

**并行输出：**
```
前端代码：dev/user-management/frontend/
后端代码：dev/user-management/backend/
```

**Main 汇报：**
```
✅ 开发阶段完成
- 前端代码：dev/user-management/frontend/
- 后端代码：dev/user-management/backend/
- 耗时：20 分钟（并行）
- 下一步：启动测试阶段
```

---

### 🧪 阶段 4：质量保证 (Test Agent)
**启动：**
```bash
sessions_spawn --session test-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "测试功能：dev/user-management/，对照需求文档 docs/projects/prd/user-management-20260306.md"
```

**Test Agent 执行：**
- 编写测试用例
- 执行功能测试
- 执行性能测试
- 输出测试报告

**输出：** `docs/projects/test/user-management-20260306.md`

**内容示例：**
```markdown
# 测试报告

## 功能测试
✅ 用户列表：通过
✅ 用户详情：通过
✅ 用户创建：通过
✅ 用户更新：通过
✅ 用户删除：通过
✅ 权限管理：通过
✅ 数据导出：通过

## 性能测试
✅ 用户列表响应：320ms (< 500ms)
✅ 并发测：100 用户正常

## Bug 列表
无
```

**Main 汇报：**
```
✅ 测试阶段完成
- 测试报告：docs/projects/test/user-management-20260306.md
- 耗时：10 分钟
- 结果：全部通过，无 Bug
- 下一步：启动验收阶段
```

---

### ✅ 阶段 5：验收 (QA Agent)
**启动：**
```bash
sessions_spawn --session qa-agent-$(date +%s) \
  --runtime subagent \
  --model nvidia/z-ai/glm4.7 \
  --mode run \
  --task "验收项目：
需求文档：docs/projects/prd/user-management-20260306.md
代码路径：dev/user-management/
测试报告：docs/projects/test/user-management-20260306.md"
```

**QA Agent 执行：**
- 对照需求验收功能
- 测试实际使用场景
- 决定是否通过验收

**输出：** `docs/projects/qa/user-management-20260306.md`

**内容示例：**
```markdown
# 验收报告

## 功能验收
✅ 所有功能符合 PRD 要求

## 使用体验
✅ 界面美观，操作流畅
✅ 权限控制正常
✅ 响应速度快

## 待优化项
- 导出功能可以增加更多格式
- 用户列表可以增加批量操作

## 最终决定
✅ 通过验收
```

**Main 汇报：**
```
✅ 用户管理系统开发完成！

📊 完整交付物
------------------
✅ 需求文档：docs/projects/prd/user-management-20260306.md
✅ 设计文档：docs/projects/design/user-management-20260306.md
✅ 前端代码：dev/user-management/frontend/
✅ 后端代码：dev/user-management/backend/
✅ 测试报告：docs/projects/test/user-management-20260306.md
✅ 验收报告：docs/projects/qa/user-management-20260306.md

⏱ 总耗时：45 分钟

🚀 下一步建议：
1. 部署到测试环境
2. 收集用户反馈
3. 继续优化

你现在可以：
- 查看代码并本地运行
- 部署到服务器
- 继续提出新需求
```

---

## 关键点

1. **完全自主** - 整个流程不需要用户干预
2. **并行执行** - 前后端开发并行，节省时间
3. **及时汇报** - 每个阶段完成后告知用户
4. **质量保证** - 经过测试和验收才能交付
5. **文档齐全** - 所有产出都有完整文档

---

这就是从想法到产品的完整流程！你现在只需要提出想法，剩下的交给我协调各 agent 完成。
