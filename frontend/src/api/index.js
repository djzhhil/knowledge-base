import axios from 'axios'
import { ElMessage } from 'element-plus'

const BASE_URL = '/api'

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 5000
})

// 添加响应拦截器：统一错误处理和数据解包
api.interceptors.response.use(
  response => {
    // 如果返回的数据有 content 字段，说明是分页结构，提取 content
    // 否则直接使用 data
    if (response.data && response.data.content !== undefined) {
      response.data = response.data.content
    }
    return response
  },
  error => {
    console.error('API 请求失败:', error)
    
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      const message = error.response.data?.message || '请求失败'
      
      switch (status) {
        case 400:
          ElMessage.error(`请求参数错误: ${message}`)
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(`错误: ${message}`)
      }
      throw error
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，请检查网络连接')
      throw error
    } else {
      // 请求配置出错
      ElMessage.error(`请求错误: ${error.message}`)
      throw error
    }
  }
)

export default {
  // 笔记相关
  notes: {
    getAll() {
      return api.get('/notes')
    },
    getById(id) {
      return api.get(`/notes/${id}`)
    },
    create(note) {
      return api.post('/notes', note)
    },
    update(id, note) {
      return api.put(`/notes/${id}`, note)
    },
    delete(id) {
      return api.delete(`/notes/${id}`)
    },
    search(keyword) {
      return api.get('/notes/search', { params: { keyword } })
    },
    getByCategory(categoryId) {
      return api.get(`/notes/category/${categoryId}`)
    }
  },

  // 分类相关
  categories: {
    getAll() {
      return api.get('/categories')
    },
    create(category) {
      return api.post('/categories', category)
    },
    delete(id) {
      return api.delete(`/categories/${id}`)
    }
  },

  // 文件相关
  files: {
    upload(formData) {
      return api.post('/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
    },
    import(file) {
      const formData = new FormData()
      formData.append('file', file)
      return api.post('/files/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
    }
  }
}
