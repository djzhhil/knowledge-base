# 开发规范标准

## 前端开发规范 (Vue 3 + TypeScript)

### 命名规范

#### 文件命名
```
- 组件文件：PascalCase，如 UserList.vue
- 工具文件：kebab-case，如 format-date.ts
- 页面文件：kebab-case，如 user-management.vue
```

#### 变量命名
```
- 变量/函数：camelCase，如 userName, getUserList()
- 常量：UPPER_SNAKE_CASE，如 API_BASE_URL
- 类/接口：PascalCase，如 UserService, IUser
```

### 目录结构
```
src/
├── api/              # API 请求
├── assets/           # 静态资源
├── components/       # 公共组件
├── composables/      # 组合式函数
├── layouts/          # 布局组件
├── pages/            # 页面组件
├── router/           # 路由配置
├── stores/           # 状态管理
├── types/            # TypeScript 类型
└── utils/            # 工具函数
```

### 组件规范

#### 组件 Props
```typescript
// ✅ 推荐：使用 script setup + TypeScript
interface Props {
  title: string
  count?: number
}

const props = withDefaults(defineProps<Props>(), {
  count: 0
})
```

#### 组件 Emits
```typescript
interface Emits {
  (e: 'update', value: string): void
  (e: 'delete', id: number): void
}

const emit = defineEmits<Emits>()
```

### 代码组织

#### 组件顺序
```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// 1. 导入
import { ref } from 'vue'

// 2. Props 和 Emits
interface Props { ... }
const props = defineProps<Props>()
const emit = defineEmits<{...}>()

// 3. 响应式数据
const count = ref(0)

// 4. 计算属性
const doubled = computed(() => count.value * 2)

// 5. 方法
const increment = () => { count.value++ }

// 6. 生命周期
onMounted(() => { ... })

// 7. Watch
watch(count, (newVal) => { ... })
</script>

<style scoped>
/* 样式 */
</style>
```

### API 规范

#### 请求封装
```typescript
// src/api/request.ts
import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000
})

request.interceptors.request.use((config) => {
  // 添加 token
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    // 错误处理
    return Promise.reject(error)
  }
)
```

#### API 定义
```typescript
// src/api/user.ts
import { request } from './request'

export interface User {
  id: number
  name: string
  email: string
}

export const userApi = {
  getList: () => request.get<User[]>('/users'),
  getById: (id: number) => request.get<User>(`/users/${id}`),
  create: (data: Partial<User>) => request.post<User>('/users', data),
  update: (id: number, data: Partial<User>) => request.put<User>(`/users/${id}`, data),
  delete: (id: number) => request.delete(`/users/${id}`)
}
```

### 状态管理规范

#### 使用 Pinia
```typescript
// src/stores/user.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // State
  const user = ref<User | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!user.value)

  // Actions
  async function fetchUser(id: number) {
    const data = await userApi.getById(id)
    user.value = data
  }

  return { user, isLoggedIn, fetchUser }
})
```

---

## 后端开发规范 (Java + Spring Boot)

### 命名规范
```
- 类名：PascalCase，如 UserController
- 方法名：camelCase，如 getUserList()
- 常量：UPPER_SNAKE_CASE，如 MAX_SIZE
- 包名：小写，如 com.example.controller
```

### 分层架构
```
com.example.project/
├── controller/       # 控制层
├── service/          # 业务逻辑层
├── repository/       # 数据访问层
├── entity/           # 实体类
├── dto/              # 数据传输对象
├── vo/               # 视图对象
├── enums/            # 枚举类
└── util/             # 工具类
```

### API 接口规范

#### RESTful 风格
```
GET    /api/users          # 获取用户列表
GET    /api/users/{id}     # 获取单个用户
POST   /api/users          # 创建用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
```

#### Controller 示例
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserVO> getList() {
        return userService.getList();
    }

    @GetMapping("/{id}")
    public UserVO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserVO create(@Valid @RequestBody UserDTO dto) {
        return userService.create(dto);
    }
}
```

### 异常处理

#### 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常，请联系管理员");
    }
}
```

---

## 通用规范

### Git 提交规范
```
feat: 新功能
fix: 修复 bug
docs: 文档变更
style: 代码格式调整（不影响功能）
refactor: 重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 注释规范
```typescript
/**
 * 获取用户列表
 * @param page 页码
 * @param size 每页数量
 * @returns 用户列表
 */
function getUserList(page: number, size: number): Promise<User[]> {
  // 实现
}
```

---

**文档版本：** v1.0
**更新时间：** 2026-03-06
