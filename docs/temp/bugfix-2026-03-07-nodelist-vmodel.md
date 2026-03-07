# Bug修复记录 - NoteList.vue v-model错误

**日期：** 2026-03-07
**修复人：** 耀
**记录人：** 小克 🐕💎

---

## 问题描述

### 错误信息

在构建前端项目时遇到编译错误：

```
VueCompilerError: v-model cannot be used on a prop, because local prop bindings are not writable.
Use a v-bind binding combined with a v-on listener that emits update:x event instead.
at /root/.openclaw/workspace/knowledge-base/frontend/src/components/NoteList.vue:23:20
21 |          <!-- 排序 -->
22 |          <el-select
23 |            v-model="sortBy"
   |                     ^^^^^^
24 |            placeholder="排序"
25 |            @change="handleSortChange"
```

### 错误原因

在 `NoteList.vue` 组件中，`sortBy` 是一个 prop（从父组件传入），但尝试使用 `v-model` 直接绑定。

**Vue 3 规则：**
- `v-model` 会尝试双向绑定
- prop 是只读的，不能直接修改
- 需要使用 `:model-value` 和 `@update:modelValue` 组合

---

## 修复方案

### 修复前

```vue
<el-select
  v-model="sortBy"
  placeholder="排序"
  @change="handleSortChange"
  class="sort-select"
>
```

### 修复后（用户已修复）

```vue
<el-select
  :model-value="sortBy"
  placeholder="排序"
  @change="handleSortChange"
  class="sort-select"
>
```

---

## 技术原理

### Vue 3 v-model 绑定规则

**在子组件中：**
```vue
// ❌ 错误：v-model 不能用于 prop
<el-select v-model="sortBy" />

// ✅ 正确：使用 :model-value 和 update:modelValue
<el-select 
  :model-value="sortBy" 
  @update:modelValue="$emit('update:sortBy', $event)" 
/>
```

**在父组件中：**
```vue
// 方式1：v-model 自动处理
<NoteList v-model:sortBy="sortBy" />

// 方式2：手动监听
<NoteList 
  :sort-by="sortBy" 
  @update:sortBy="sortBy = $event" 
/>
```

### Element Plus 组件的 v-model

对于 Element Plus 的 `el-select` 组件：

```vue
<!-- ❌ 错误：如果 sortBy 来自 prop -->
<el-select v-model="sortBy" />

<!-- ✅ 正确：使用 model-value -->
<el-select :model-value="sortBy" @change="handleSortChange" />
```

---

## 相关知识

### Vue 3 Props 限制

- **Props 是只读的**
- 子组件不能修改 props
- 修改 props 会警告（Vue 3 直接报错）

### v-model 的工作原理

```vue
<!-- 父组件 -->
<Child v-model:value="parentValue" />

<!-- 编译后等价于 -->
<Child 
  :value="parentValue" 
  @update:value="newValue => parentValue = newValue" 
/>
```

### 双向绑定的正确做法

**子组件需要：**
1. 接收 prop
2. 触发 update 事件

```vue
<script setup>
const props = defineProps(['value'])
const emit = defineEmits(['update:value'])

// 修改时触发事件
function handleChange(newValue) {
  emit('update:value', newValue)
}
</script>
```

---

## 预防措施

### 代码审查检查点

1. **检查 v-model 绑定的对象**
   - 是否是 prop？
   - 是否需要双向绑定？

2. **使用正确的绑定方式**
   - prop → 使用 `:model-value`
   - 本地状态 → 使用 `v-model`

3. **子组件设计原则**
   - 明确区分可控和不可控模式
   - 提供 update 事件接口

### 代码示例对比

```vue
<!-- ❌ 错误：对 prop 使用 v-model -->
<template>
  <el-input v-model="modelValue" />
</template>

<script setup>
const props = defineProps(['modelValue'])
</script>

<!-- ✅ 正确：使用 model-value -->
<template>
  <el-input :model-value="modelValue" @input="handleInput" />
</template>

<script setup>
const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

function handleInput(value) {
  emit('update:modelValue', value)
}
</script>
```

---

## 相关资源

- [Vue 3 官方文档 - Props](https://vuejs.org/guide/components/props.html)
- [Vue 3 官方文档 - v-model](https://vuejs.org/guide/components/v-model.html)
- [Element Plus Select 组件](https://element-plus.org/en-US/component/select.html)

---

## 经验教训

1. **在迁移 Vue 2 到 Vue 3 时需要注意**
   - Vue 2 中 v-model 对 prop 的使用可能不会报错
   - Vue 3 严格检查 props 的只读性

2. **使用 TypeScript 可以提前发现问题**
   - 类型检查可以发现 props 的只读性
   - 减少 runtime 错误

3. **组件设计时明确接口**
   - 是否需要双向绑定
   - props 和 events 的设计规范

---

**记录时间：** 2026-03-07 17:49
**最后更新：** 2026-03-07 17:50
