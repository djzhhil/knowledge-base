import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 5000
})

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
  }
}
