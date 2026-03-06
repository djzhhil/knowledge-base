<template>
  <el-dialog
    v-model="visible"
    title="导入 Markdown 文件"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="upload-dialog">
      <!-- 拖拽上传区域 -->
      <el-upload
        ref="uploadRef"
        class="upload-area"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleRemove"
        :file-list="fileList"
        :limit="10"
        accept=".md,.markdown"
        multiple
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持批量上传 .md 文件，单个文件不超过 5MB
          </div>
        </template>
      </el-upload>

      <!-- 文件预览 -->
      <div v-if="previewContent" class="preview-section">
        <div class="preview-header">
          <span class="preview-title">文件内容预览</span>
          <el-button link :icon="Close" @click="previewContent = ''" />
        </div>
        <el-scrollbar height="300px">
          <pre class="preview-content">{{ previewContent }}</pre>
        </el-scrollbar>
      </div>

      <!-- 上传选项 -->
      <div class="upload-options">
        <el-form :model="options" label-width="80px" size="small">
          <el-form-item label="分类">
            <el-select v-model="options.categoryId" placeholder="选择分类" clearable>
              <el-option
                v-for="cat in categories"
                :key="cat.id"
                :label="cat.name"
                :value="cat.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="options.tagIds" multiple placeholder="添加标签" collapse-tags>
              <el-option
                v-for="tag in tags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          确定导入
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { UploadFilled, Close } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  categories: {
    type: Array,
    default: () => []
  },
  tags: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'upload'])

// 响应式数据
const uploadRef = ref(null)
const fileList = ref([])
const previewContent = ref('')
const uploading = ref(false)
const options = ref({
  categoryId: null,
  tagIds: []
})

// 对话框可见性
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 处理文件选择
const handleFileChange = (file, files) => {
  // 验证文件类型
  const validTypes = ['.md', '.markdown']
  const fileExt = '.' + file.name.split('.').pop().toLowerCase()
  if (!validTypes.includes(fileExt)) {
    fileList.value = files.filter(f => f !== file)
    ElMessage.error('只支持 .md 文件')
    return
  }

  // 验证文件大小（5MB）
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    fileList.value = files.filter(f => f !== file)
    ElMessage.error('文件大小不能超过 5MB')
    return
  }

  // 读取文件内容预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewContent.value = e.target.result
  }
  reader.readAsText(file.raw)
}

// 处理移除文件
const handleRemove = () => {
  previewContent.value = ''
}

// 处理上传
const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  uploading.value = true

  try {
    // 读取所有文件内容
    const files = await Promise.all(
      fileList.value.map((file) => {
        return new Promise((resolve, reject) => {
          const reader = new FileReader()
          reader.onload = (e) => {
            resolve({
              title: file.name.replace(/\.(md|markdown)$/, ''),
              content: e.target.result,
              category: options.value.categoryId,
              tags: options.value.tagIds
            })
          }
          reader.onerror = reject
          reader.readAsText(file.raw)
        })
      })
    )

    // 向父组件传递上传的文件
    emit('upload', files)

    // 成功后关闭对话框
    handleClose()
    ElMessage.success(`成功导入 ${files.length} 个文件`)
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

// 处理关闭对话框
const handleClose = () => {
  fileList.value = []
  previewContent.value = ''
  options.value = {
    categoryId: null,
    tagIds: []
  }
  visible.value = false
}
</script>

<style scoped>
.upload-dialog {
  padding: 10px 0;
}

/* 拖拽上传区域 */
.upload-area {
  margin-bottom: 20px;
}

:deep(.el-upload-dragger) {
  padding: 40px;
  background: var(--bg-secondary);
  border: 2px dashed var(--border-color);
}

:deep(.el-upload-dragger:hover) {
  border-color: var(--primary-color);
}

:deep(.el-upload__text) {
  color: var(--text-secondary);
}

:deep(.el-upload__text em) {
  color: var(--primary-color);
}

:deep(.el-upload__tip) {
  color: var(--text-tertiary);
  font-size: 12px;
}

/* 预览区域 */
.preview-section {
  margin-bottom: 20px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  overflow: hidden;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
}

.preview-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.preview-content {
  padding: 16px;
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-all;
}

/* 上传选项 */
.upload-options {
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 8px;
}

:deep(.el-form-item) {
  margin-bottom: 12px;
}

:deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.el-select) {
  width: 100%;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.el-dialog) {
    width: 90%;
  }

  :deep(.el-upload-dragger) {
    padding: 20px;
  }

  .preview-section {
    height: 200px;
  }

  :deep(.el-form-item__label) {
    width: 60px;
  }
}
</style>
