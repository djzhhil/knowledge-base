package com.knowledge.service;

import com.knowledge.entity.Note;
import com.knowledge.mapper.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            .orElseThrow(() -> new RuntimeException("笔记不存在"));
    }

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public Note updateNote(Long id, Note note) {
        Note existingNote = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("笔记不存在"));

        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setCategoryId(note.getCategoryId());
        existingNote.setTags(note.getTags());
        return noteRepository.save(existingNote);
    }

    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("笔记不存在");
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
