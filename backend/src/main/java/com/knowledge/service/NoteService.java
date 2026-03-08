package com.knowledge.service;

import com.knowledge.entity.Note;
import com.knowledge.exception.BusinessException;
import com.knowledge.mapper.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Page<Note> getAllNotes(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("笔记不存在"));
    }

    @Transactional
    public Note createNote(Note note) {
        // 参数校验
        if (!StringUtils.hasText(note.getTitle())) {
            throw new BusinessException("笔记标题不能为空");
        }
        // TODO: 分类是否存在校验需要依赖 CategoryService，暂时跳过
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long id, Note note) {
        // 参数校验
        if (note == null) {
            throw new BusinessException("笔记信息不能为空");
        }

        Note existingNote = noteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("笔记不存在"));

        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setCategoryId(note.getCategoryId());
        existingNote.setTags(note.getTags());
        return noteRepository.save(existingNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new BusinessException("笔记不存在");
        }
        noteRepository.deleteById(id);
    }

    public List<Note> searchNotes(String keyword) {
        return noteRepository.searchNotes(keyword);
    }

    public List<Note> getNotesByCategory(Long categoryId) {
        return noteRepository.findByCategoryId(categoryId);
    }

    /**
     * 检查是否存在相同内容哈希的笔记
     *
     * @param contentHash 内容哈希
     * @return 是否存在
     */
    public boolean existsByContentHash(String contentHash) {
        return noteRepository.existsByContentHash(contentHash);
    }

    /**
     * 批量创建笔记（事务管理）
     *
     * @param notes 笔记列表
     * @return 保存后的笔记列表
     */
    @Transactional
    public List<Note> batchCreateNotes(List<Note> notes) {
        return noteRepository.saveAll(notes);
    }
}
