# Vue 3 前端面试题整理

> 更新时间：2026-03-06  
> 侧重：Vue 3 新特性、Composition API、Pinia、Vue Router 4

---

## 目录
1. [Vue 3 核心概念](#1-vue-3-核心概念)
2. [Vue 生命周期](#2-vue-生命周期)
3. [组件通信](#3-组件通信)
4. [状态管理 Pinia/Vuex](#4-状态管理-piniavuex)
5. [Vue Router 路由](#5-vue-router-路由)
6. [性能优化](#6-性能优化)

---

## 1. Vue 3 核心概念

### 1.1 响应式系统原理

#### Q1: Vue 3 的响应式系统是如何工作的？

**答案：**
Vue 3 使用 **ES6 Proxy** 实现响应式系统，相比 Vue 2 的 `Object.defineProperty` 更加完善。

**核心原理：**
```javascript
// Proxy 实现响应式
const proxy = new Proxy(target, {
  get(target, key, receiver) {
    track(target, key)  // 收集依赖
    return Reflect.get(target, key, receiver)
  },
  set(target, key, value, receiver) {
    trigger(target, key)  // 触发更新
    return Reflect.set(target, key, value, receiver)
  }
})
```

**依赖收集和触发流程：**
1. **track()** - 在 getter 中收集当前活跃的 effect 到依赖映射表
2. **trigger()** - 在 setter 中执行所有收集的 effect

**Vue 3 响应式 API：**
- `ref()` - 创建响应式引用，适用于原始类型
- `reactive()` - 创建响应式对象，适用于对象类型
- `computed()` - 创建计算属性
- `watch()` / `watchEffect()` - 监听数据变化

#### Q2: ref 和 reactive 的区别？

**答案：**

| 特性 | ref() | reactive() |
|------|-------|------------|
| 适用类型 | 原始类型和对象 | 仅对象类型 |
| 访问方式 | 需要 `.value` | 直接访问属性 |
| 解构丢失响应式 | 会丢失 | 会丢失 |
| 返回值 | Ref 对象 | Proxy 代理对象 |

**使用示例：**
```javascript
import { ref, reactive } from 'vue'

// ref - 适合原始类型
const count = ref(0)
count.value++  // 需要通过 .value 访问

// reactive - 适合对象类型
const state = reactive({ count: 0 })
state.count++  // 直接访问

// ref 在模板中自动解包
// {{ count }} // 不需要 .value
```

**注意事项：**
- `reactive()` 不能替换整个对象（会丢失响应式连接）
- `reactive()` 解构会丢失响应性，需要用 `toRefs()` 保持响应性
- 数组和 Map 中的 ref 不会自动解包，需要使用 `.value`

#### Q3: Vue 3 组合式 API (Composition API) 的优势？

**答案：**

**1. 更好的逻辑复用**
```javascript
// 抽取可复用的逻辑
function useMouse() {
  const x = ref(0)
  const y = ref(0)
  // ... 监听鼠标事件
  return { x, y }
}

// 在组件中使用
const { x, y } = useMouse()
```

**2. 更灵活的代码组织**
- 按功能组织代码，而非按选项类型
- 相关的逻辑放在一起

**3. 更好的 TypeScript 支持**
- 类型推断更准确
- 函数式 API 更友好

**4. 更好的 Tree-shaking**
- 只会引入用到的 API
- 减小打包体积

**setup() 函数的执行时机：**
- 在 `beforeCreate` 之前执行
- `this` 是 undefined

#### Q4: watch 和 watchEffect 的区别？

**答案：**

| 特性 | watch | watchEffect |
|------|-------|-------------|
| 执行时机 | 懒执行，依赖变化才执行 | 立即执行一次 |
| 依赖收集 | 显式指定监听源 | 自动收集函数内使用的响应式数据 |
| 访问新旧值 | ✅ 可以 | ❌ 不行 |
| 返回值 | 停止监听函数 | 停止监听函数 |

**示例：**
```javascript
import { watch, watchEffect } from 'vue'

// watch - 懒执行，需要显式指定依赖
watch(count, (newVal, oldVal) => {
  console.log(`count 从 ${oldVal} 变成 ${newVal}`)
}, { immediate: true })

// watchEffect - 立即执行，自动收集依赖
watchEffect(() => {
  console.log(`当前 count: ${count.value}`)
})
```

#### Q5: Vue 3 的响应式局限性有哪些？

**答案：**

1. **原始类型限制**
   - `reactive()` 不能处理 string、number、boolean 等原始类型

2. **响应式对象替换**
   ```javascript
   let state = reactive({ count: 0 })
   state = reactive({ count: 1 }) // ❌ 响应式连接丢失
   ```

3. **解构丢失响应性**
   ```javascript
   const { count } = state // ❌ 失去响应式
   ```

4. **数组 ref 不自动解包**
   ```javascript
   const arr = reactive([ref(0)])
   arr[0].value // ✅ 需要 .value
   ```

5. **Set/Map 不支持**
   - Vue 3 不支持 Set/Map 的响应式转换

---

### 1.2 Composition API 进阶

#### Q6: provide 和 inject 的用法？

**答案：**

用于跨层级组件通信（祖先 → 后代）。

**基本用法：**
```javascript
// 祖先组件
import { provide, ref } from 'vue'

const count = ref(0)
provide('count', count)  // 提供响应式数据
provide('constant', '固定值')  // 提供普通值

// 后代组件
import { inject } from 'vue'

const count = inject('count')
const constant = inject('constant')
```

**响应式更新：**
- provide 的值响应式变化时，inject 的组件自动更新
- 使用 `readonly()` 保护注入的值不被修改

#### Q7: 组合式函数 (Composables) 的最佳实践？

**答案：**

**命名规范：** 以 `use` 开头，`useXxx`

**标准结构：**
```javascript
// composables/useMouse.js
import { ref, onMounted, onUnmounted } from 'vue'

export function useMouse() {
  const x = ref(0)
  const y = ref(0)

  function update(e) {
    x.value = e.pageX
    y.value = e.pageY
  }

  onMounted(() => window.addEventListener('mousemove', update))
  onUnmounted(() => window.removeEventListener('mousemove', update))

  return { x, y }
}
```

**使用：**
```javascript
const { x, y } = useMouse()
```

---

## 2. Vue 生命周期

### 2.1 Vue 3 生命周期钩子

#### Q8: Vue 3 的生命周期有哪些？与 Vue 2 有什么区别？

**答案：**

**Vue 3 生命周期图示：**

```
创建阶段 → 更新阶段 → 卸载阶段
```

**生命周期钩子对照表：**

| Vue 2 | Vue 3 | 说明 |
|-------|-------|------|
| beforeCreate | ❌ (setup 中执行) | 组件创建前 |
| created | ❌ (setup 中执行) | 组件创建后 |
| beforeMount | onBeforeMount | 挂载前 |
| mounted | onMounted | 挂载后 |
| beforeUpdate | onBeforeUpdate | 更新前 |
| updated | onUpdated | 更新后 |
| beforeDestroy | onBeforeUnmount | 卸载前 |
| destroyed | onUnmounted | 卸载后 |
| activated | onActivated | 激活时 |
| deactivated | onDeactivated | 失活时 |

**Vue 3 使用组合式 API：**
```javascript
import { 
  onBeforeMount, 
  onMounted, 
  onBeforeUpdate, 
  onUpdated,
  onBeforeUnmount, 
  onUnmounted 
} from 'vue'

onMounted(() => {
  console.log('挂载完成')
})

onUnmounted(() => {
  console.log('卸载清理')
})
```

**重要区别：**
- `beforeCreate` 和 `created` 不再使用，由 `setup()` 替代
- `beforeDestroy` → `beforeUnmount`
- `destroyed` → `unmounted`

#### Q9: Vue 3 组件卸载时会触发哪些生命周期？

**答案：**
```javascript
// 1. onBeforeUnmount - 卸载前
// 2. 移除事件监听、计时器
// 3. onUnmounted - 卸载后
// 4. DOM 移除
```

**清理工作：**
```javascript
onUnmounted(() => {
  // 清理定时器
  clearInterval(timer)
  // 移除事件监听
  window.removeEventListener('resize', handleResize)
  // 取消订阅
  subscription.unsubscribe()
})
```

---

## 3. 组件通信

### 3.1 通信方式

#### Q10: Vue 组件间通信的方式有哪些？

**答案：**

| 方式 | 方向 | 适用场景 |
|------|------|----------|
| **props / emits** | 父 → 子 / 子 → 父 | 父子组件通信 |
| **v-model** | 双向绑定 | 表单组件 |
| **provide / inject** | 祖先 → 后代 | 跨层级通信 |
| **Vuex / Pinia** | 全局状态 | 跨组件、跨页面 |
| **事件总线 ( mitt )** | 任意组件 | 兄弟、无关组件 |
| **ref / expose** | 父 → 子 | 组件方法调用 |
| **$attrs** | 父 → 子 | 透传属性 |

#### Q11: props 和 emits 的用法？

**答案：**

**子组件定义 props 和 emits：**
```vue
<!-- Child.vue -->
<script setup>
// 定义 props（推荐数组语法或对象语法）
const props = defineProps({
  title: String,
  count: {
    type: Number,
    required: true,
    default: 0
  }
})

// 定义 emits
const emit = defineEmits(['update', 'delete'])

// 发送事件
function handleClick() {
  emit('update', newValue)
}
</script>
```

**父组件使用：**
```vue
<!-- Parent.vue -->
<Child 
  title="标题"
  :count="count"
  @update="handleUpdate"
/>
```

**v-model 双向绑定：**
```vue
<!-- 子组件 -->
<script setup>
const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

function onInput(e) {
  emit('update:modelValue', e.target.value)
}
</script>
<input :value="modelValue" @input="onInput" />

<!-- 父组件 -->
<Child v-model="value" />
```

#### Q12: $attrs 和 $listeners 的区别？

**答案：**

**Vue 2：**
- `$attrs` - 父组件传递的、非 prop 属性
- `$listeners` - 父组件传递的事件监听器

**Vue 3：**
- `$attrs` 包含属性和事件
- `$listeners` 移除（合并到 `$attrs`）

```vue
<!-- 透传所有属性和事件 -->
<div v-bind="$attrs">...</div>
```

---

## 4. 状态管理 Pinia/Vuex

### 4.1 Pinia

#### Q13: Pinia 和 Vuex 的区别？

**答案：**

| 特性 | Pinia | Vuex |
|------|-------|------|
| API 风格 | 简洁直观 | 较复杂 |
| TypeScript 支持 | 优秀 | 一般 |
| mutations | 无 | 有 |
| 嵌套模块 | 扁平化 | 需要 modules |
| 热更新 | 支持 | 需插件 |
| 体积 | 更小 | 较大 |

**Pinia 优势：**
1. 取消 mutations，actions 同步/异步都可以
2. 不再有模块嵌套，自动扁平化
3. 更好的 TypeScript 支持
4. 不需要注入、映射
5. 支持 Vue DevTools

#### Q14: Pinia 的使用方法？

**答案：**

**1. 定义 Store**
```javascript
// stores/counter.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCounterStore = defineStore('counter', () => {
  // state
  const count = ref(0)
  
  // getters
  const doubleCount = computed(() => count.value * 2)
  
  // actions
  function increment() {
    count.value++
  }
  
  return { count, doubleCount, increment }
})
```

**Option Store 写法：**
```javascript
export const useCounterStore = defineStore('counter', {
  state: () => ({ count: 0 }),
  getters: {
    doubleCount: (state) => state.count * 2
  },
  actions: {
    increment() {
      this.count++
    }
  }
})
```

**2. 使用 Store**
```javascript
import { useCounterStore } from '@/stores/counter'

const store = useCounterStore()

// 直接访问
store.count
store.increment()

// 解构保持响应式
import { storeToRefs } from 'pinia'
const { count, doubleCount } = storeToRefs(store)
```

#### Q15: Pinia 的插件机制？

**答案：**

```javascript
// pinia-plugin-xxx.js
export default function myPlugin({ store }) {
  // 初始化时调用
  store.$subscribe((mutation, state) => {
    // 状态变化时调用
  })
  
  store.$onAction(({ name, store, args, after }) => {
    // action 执行前后
    after(() => {
      // action 完成后
    })
  })
}
```

---

## 5. Vue Router 路由

### 5.1 路由基础

#### Q16: Vue Router 的导航守卫有哪些？

**答案：**

**导航解析流程：**
```
1. 导航触发
2. 失活组件 beforeRouteLeave
3. 全局 beforeEach
4. 复用组件 beforeRouteUpdate
5. 路由配置 beforeEnter
6. 解析异步路由组件
7. 激活组件 beforeRouteEnter
8. 全局 beforeResolve
9. 导航确认
10. 全局 afterEach
11. DOM 更新
12. beforeRouteEnter 回调
```

**全局守卫：**
```javascript
// 全局前置守卫
router.beforeEach((to, from) => {
  // 返回 false 取消导航
  // 返回路由地址重定向
  // 返回 true 继续
})

// 全局解析守卫（在导航确认前）
router.beforeResolve(async (to, from) => {
  // 适合获取数据
})

// 全局后置钩子（不改变导航）
router.afterEach((to, from) => {
  // 分析、埋点等
})
```

**路由独享守卫：**
```javascript
const routes = [
  {
    path: '/admin',
    component: Admin,
    beforeEnter: (to, from) => {
      // 只有进入此路由时触发
    }
  }
]
```

**组件内守卫：**
```javascript
// 组合式 API
import { onBeforeRouteLeave, onBeforeRouteUpdate } from 'vue-router'

onBeforeRouteLeave((to, from) => {
  // 导航离开时
})

onBeforeRouteUpdate((to, from) => {
  // 路由更新时
})
```

#### Q17: Vue Router 4 的新特性？

**答案：**

**1. 组合式 API 支持**
```javascript
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 获取参数
route.params.id

// 编程式导航
router.push('/home')
```

**2. 移除 `next()` 参数**
```javascript
// ❌ Vue Router 3
router.beforeEach((to, from, next) => {
  next()
})

// ✅ Vue Router 4
router.beforeEach((to, from) => {
  // 直接返回结果
  return true / false / path
})
```

**3. 更好的 TypeScript 支持**

**4. 动态路由添加**
```javascript
// 添加路由
router.addRoute({
  path: '/about',
  name: 'about',
  component: About
})

// 删除路由
router.removeRoute('about')
```

**5. 路由元信息 (meta) 类型化**
```typescript
declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: string[]
  }
}
```

#### Q18: 如何实现路由懒加载？

**答案：**

**方式一：动态导入**
```javascript
const routes = [
  {
    path: '/home',
    component: () => import('@/views/Home.vue')
  }
]
```

**方式二：路由分组**
```javascript
const routes = [
  {
    path: '/admin',
    component: () => import(/* webpackChunkName: "admin" */ '@/views/Admin.vue'),
    children: [
      {
        path: 'dashboard',
        component: () => import(/* webpackChunkName: "admin" */ '@/views/Dashboard.vue')
      }
    ]
  }
]
```

**方式三：Suspense + async component**
```vue
<template>
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <Loading />
    </template>
  </Suspense>
</template>
```

---

## 6. 性能优化

### 6.1 虚拟 DOM 和 Diff 算法

#### Q19: 虚拟 DOM 是什么？有什么优缺点？

**答案：**

**虚拟 DOM (VDOM)** 是真实 DOM 的 JavaScript 对象表示。

**优点：**
1. **减少直接操作 DOM** - 批量更新，减少重排重绘
2. **跨平台** - 同一套 VDOM 可渲染到不同平台
3. **函数式 UI** - 可预测的状态变化

**缺点：**
1. 首次渲染可能稍慢（需创建 VDOM）
2. 内存占用（额外的 JS 对象）

**Vue 中的 VDOM：**
```javascript
// VDOM 示例
{
  tag: 'div',
  props: { class: 'container' },
  children: [
    { tag: 'h1', children: '标题' },
    { tag: 'p', children: '内容' }
  ]
}
```

#### Q20: Vue 的 diff 算法原理？

**答案：**

**核心思想：**
- 同层级比较，不跨层级
- 使用 key 标记节点身份

**patch 过程：**
```javascript
function patch(oldVnode, newVnode) {
  // 1. 不同标签 → 替换
  if (oldVnode.tag !== newVnode.tag) {
    replaceNode(oldVnode, newVnode)
    return
  }
  
  // 2. 相同节点 → 比较属性和子节点
  patchData(oldVnode, newVnode)
  patchChildren(oldVnode.children, newVnode.children)
}
```

**子节点对比策略：**
```
旧子节点: [A, B, C, D]
新子节点: [A, B, E, C]

1. A 相同 → patch
2. B 相同 → patch
3. E 是新节点 → 插入
4. C 相同 → patch
5. D 不存在 → 删除
```

#### Q21: key 的作用是什么？为什么不能用 index？

**答案：**

**key 的作用：**
- 标识节点的**身份**
- diff 算法中判断节点是否可复用
- 优化渲染性能

**为什么不用 index 作为 key：**
```vue
<!-- 错误示例 -->
<li v-for="(item, index) in list" :key="index">
  {{ item.name }}
</li>
```

**问题：**
1. **列表重排时** - index 变化，key 变化，节点被误判为新节点
2. **性能问题** - 无法复用 DOM 节点，频繁创建/销毁
3. **状态问题** - 输入框焦点、选中状态错乱

**正确做法：**
```vue
<!-- 使用唯一 ID -->
<li v-for="item in list" :key="item.id">
  {{ item.name }}
</li>
```

**key 不需要场景：**
- 静态列表（内容不变）
- 简单文本节点
- 列表项顺序不变

#### Q22: Vue 性能优化的方法？

**答案：**

**1. 响应式优化**
```javascript
// 浅响应式，避免深层监听
const state = shallowRef({ deep: 'data' })

// 只监听特定属性
watch(() => state.value.count, (val) => {})

// 大列表使用 shallowRef
const bigList = shallowRef([])
```

**2. v-show vs v-if**
- `v-show` - 频繁切换（display: none）
- `v-if` - 条件很少改变（移除 DOM）

**3. 列表优化**
```vue
<!-- 使用 key -->
<transition-group tag="ul">
  <li v-for="item in items" :key="item.id">...</li>
</transition-group>

<!-- 虚拟滚动（长列表） -->
<virtual-list :items="items" />
```

**4. 组件懒加载**
```javascript
// 路由懒加载
{
  path: '/heavy',
  component: () => import('@/views/Heavy.vue')
}

// 异步组件
const AsyncComponent = defineAsyncComponent({
  loader: () => import('@/views/Heavy.vue'),
  loadingComponent: Loading,
  delay: 200
})
```

**5. 减少不必要的响应式**
```javascript
// 不需要响应式的数据
import { markRaw } from 'vue'

function createInstance() {
  const instance = {
    id: 1,
    // ... 不会被响应式转换
  }
  return markRaw(instance)
}
```

**6. 合理使用 computed**
```javascript
// 有缓存，避免重复计算
const double = computed(() => count.value * 2)
```

**7. 事件委托**
```vue
<!-- 单个监听器处理多个子元素 -->
<ul @click="handleClick">
  <li v-for="item in items">...</li>
</ul>
```

**8. 长列表优化**
```javascript
// 使用 shallowRef 减少响应式开销
const list = shallowRef([])

// 分页加载
const page = ref(1)
function loadMore() {
  fetch(`/api/items?page=${page.value}`)
    .then(data => {
      list.value = [...list.value, ...data]
    })
}
```

---

## 常见面试题补充

### Q23: Vue 3 中如何实现组件通信？

**答案：**
- **父子**：`props` + `defineEmits`
- **子父**：`$emit`
- **跨级**：`provide` / `inject`
- **任意**：`mitt` 事件总线
- **全局**：`Pinia` 状态管理

### Q24: Vue 3 的 teleport 是什么？

**答案：**

**Teleport** 允许将组件渲染到 DOM 的其他位置。

```vue
<template>
  <button @click="show = true">打开弹窗</button>
  
  <Teleport to="body">
    <div v-if="show" class="modal">
      弹窗内容
      <button @click="show = false">关闭</button>
    </div>
  </Teleport>
</template>
```

**场景：**
- 模态框（渲染到 body）
- 通知提示（渲染到指定容器）
- 避免 z-index 层级问题

### Q25: Vue 3 的 Suspense 是什么？

**答案：**

**Suspense** 用于处理异步组件的加载状态。

```vue
<template>
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <Loading />
    </template>
  </Suspense>
</template>
```

**组合式 API 写法：**
```javascript
// AsyncComponent.vue
<script setup>
const { data } = await useAsyncData('/api/data')
</script>
```

---

## 参考资料

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Vue Router 4 文档](https://router.vuejs.org/zh/)
- [Pinia 官方文档](https://pinia.vuejs.org/zh/)
- [Vue 3 面试题整理](https://vue3js.cn/interview/)