<template>
  <div id="app">
    <div class="header">
      <h1>🐕 知识库系统</h1>
      <div class="search-box">
        <input
          v-model="searchKeyword"
          placeholder="搜索笔记..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">搜索</button>
      </div>
      <button class="btn-primary" @click="showCreateModal = true">
        + 新建笔记
      </button>
    </div>

    <div class="content">
      <div class="category-filter">
        <select v-model="selectedCategory" @change="filterByCategory">
          <option value="">所有分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
      </div>

      <div class="note-list">
        <div
          v-for="note in filteredNotes"
          :key="note.id"
          class="note-card"
          @click="viewNote(note)"
        >
          <div class="note-header">
            <h3>{{ note.title }}</h3>
            <div class="note-actions">
              <button @click.stop="editNote(note)">编辑</button>
              <button @click.stop="deleteNote(note.id)">删除</button>
            </div>
          </div>
          <div class="note-content-preview">
            {{ note.content.substring(0, 150) }}...
          </div>
          <div class="note-meta">
            <span v-if="note.tags" class="tags">{{ note.tags }}</span>
            <span class="time">{{ formatTime(note.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑/创建弹窗 -->
    <div v-if="showCreateModal || editingNote" class="modal">
      <div class="modal-content">
        <h2>{{ editingNote ? '编辑笔记' : '新建笔记' }}</h2>
        <div class="form-group">
          <label>标题</label>
          <input v-model="noteForm.title" placeholder="笔记标题" />
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="noteForm.categoryId">
            <option value="">选择分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>标签（逗号分隔）</label>
          <input v-model="noteForm.tags" placeholder="Java, SpringBoot" />
        </div>
        <div class="form-group">
          <label>内容（支持 Markdown）</label>
          <textarea
            v-model="noteForm.content"
            placeholder="笔记内容..."
            rows="10"
          ></textarea>
        </div>
        <div class="form-actions">
          <button @click="saveNote">保存</button>
          <button @click="cancelEdit">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from './api'

export default {
  name: 'App',
  data() {
    return {
      notes: [],
      categories: [],
      searchKeyword: '',
      selectedCategory: '',
      showCreateModal: false,
      editingNote: null,
      noteForm: {
        title: '',
        content: '',
        categoryId: null,
        tags: ''
      }
    }
  },

  computed: {
    filteredNotes() {
      let result = this.notes

      if (this.selectedCategory) {
        result = result.filter(n => n.categoryId === this.selectedCategory)
      }

      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        result = result.filter(n =>
          n.title.toLowerCase().includes(keyword) ||
          n.content.toLowerCase().includes(keyword)
        )
      }

      return result
    }
  },

  async mounted() {
    await this.loadData()
  },

  methods: {
    async loadData() {
      try {
        const [notesRes, catsRes] = await Promise.all([
          api.notes.getAll(),
          api.categories.getAll()
        ])
        this.notes = notesRes.data
        this.categories = catsRes.data
      } catch (error) {
        alert('加载数据失败：' + error.message)
      }
    },

    handleSearch() {
      // 搜索逻辑在 computed 中处理
    },

    viewNote(note) {
      alert(`标题：${note.title}\n\n内容：\n${note.content}`)
    },

    editNote(note) {
      this.editingNote = note
      this.noteForm = {
        title: note.title,
        content: note.content,
        categoryId: note.categoryId,
        tags: note.tags || ''
      }
    },

    async saveNote() {
      if (!this.noteForm.title || !this.noteForm.content) {
        alert('请填写标题和内容')
        return
      }

      try {
        if (this.editingNote) {
          await api.notes.update(this.editingNote.id, this.noteForm)
        } else {
          await api.notes.create(this.noteForm)
        }

        this.cancelEdit()
        await this.loadData()
        alert('保存成功')
      } catch (error) {
        alert('保存失败：' + error.message)
      }
    },

    async deleteNote(id) {
      if (confirm('确定删除这条笔记吗？')) {
        try {
          await api.notes.delete(id)
          await this.loadData()
          alert('删除成功')
        } catch (error) {
          alert('删除失败：' + error.message)
        }
      }
    },

    cancelEdit() {
      this.editingNote = null
      this.showCreateModal = false
      this.noteForm = {
        title: '',
        content: '',
        categoryId: null,
        tags: ''
      }
    },

    filterByCategory() {
      // 过滤逻辑在 computed 中处理
    },

    formatTime(time) {
      return new Date(time).toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: Arial, sans-serif;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid #eee;
}

.header h1 {
  flex: 1;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box input {
  padding: 8px;
  width: 200px;
}

.btn-primary {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 4px;
}

.content {
  margin-top: 20px;
}

.category-filter select {
  padding: 8px;
  width: 200px;
  margin-bottom: 20px;
}

.note-list {
  display: grid;
  gap: 15px;
}

.note-card {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.note-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.note-header h3 {
  font-size: 18px;
  color: #333;
}

.note-actions {
  display: flex;
  gap: 10px;
}

.note-actions button {
  padding: 4px 8px;
  cursor: pointer;
}

.note-actions button:last-child {
  background: #ff4444;
  color: white;
  border: none;
  border-radius: 3px;
}

.note-content-preview {
  color: #666;
  margin-bottom: 10px;
  line-height: 1.6;
}

.note-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.tags {
  background: #e3f2fd;
  color: #1976d2;
  padding: 2px 8px;
  border-radius: 10px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
}

.modal-content h2 {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-group textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.form-actions button {
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 4px;
  border: none;
}

.form-actions button:first-child {
  background: #4CAF50;
  color: white;
}
</style>
