package com.knowledge.service;

import com.knowledge.entity.Category;
import com.knowledge.entity.Note;
import com.knowledge.exception.BusinessException;
import com.knowledge.mapper.CategoryRepository;
import com.knowledge.mapper.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Slf4j
public class NoteService {
    private final NoteRepository noteRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public NoteService(NoteRepository noteRepository, CategoryService categoryService, CategoryRepository categoryRepository) {
        this.noteRepository = noteRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    public List<Note> getAllNotes() {
        log.debug("获取所有笔记");
        return noteRepository.findAll();
    }

    public Page<Note> getAllNotes(Pageable pageable) {
        log.debug("获取笔记列表，分页参数: {}", pageable);
        return noteRepository.findAll(pageable);
    }

    public Note getNoteById(Long id) {
        log.debug("根据 ID 获取笔记: {}", id);
        return noteRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("笔记不存在，ID: {}", id);
                return new BusinessException("笔记不存在");
            });
    }

    @Transactional
    public Note createNote(Note note) {
        // 参数校验
        if (!StringUtils.hasText(note.getTitle())) {
            log.warn("创建笔记失败：笔记标题为空");
            throw new BusinessException("笔记标题不能为空");
        }

        // 自动生成 contentHash
        if (note.getContentHash() == null || note.getContentHash().isEmpty()) {
            note.setContentHash(calculateMD5Hash(note.getContent()));
        }

        // 通过 categoryId 查询 Category 对象并设置
        if (note.getCategoryId() != null) {
            Category category = categoryRepository.findById(note.getCategoryId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
            note.setCategory(category);
        }

        Note savedNote = noteRepository.save(note);
        log.info("创建笔记成功，ID={}, title={}", savedNote.getId(), savedNote.getTitle());
        return savedNote;
    }

    @Transactional
    public Note updateNote(Long id, Note note) {
        log.debug("更新笔记，ID={}", id);

        if (note == null) {
            log.warn("更新笔记失败：笔记信息为空");
            throw new BusinessException("笔记信息不能为空");
        }

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("更新笔记失败：笔记不存在，ID={}", id);
                    return new BusinessException("笔记不存在");
                });

        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setTags(note.getTags());

        // 通过 categoryId 查询 Category 对象并设置
        if (note.getCategoryId() != null) {
            Category category = categoryRepository.findById(note.getCategoryId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
            existingNote.setCategory(category);
        }

        Note updatedNote = noteRepository.save(existingNote);
        log.info("更新笔记成功，ID={}, title={}", updatedNote.getId(), updatedNote.getTitle());
        return updatedNote;
    }

    @Transactional
    public void deleteNote(Long id) {
        log.debug("删除笔记，ID={}", id);

        if (!noteRepository.existsById(id)) {
            log.warn("删除笔记失败：笔记不存在，ID={}", id);
            throw new BusinessException("笔记不存在");
        }
        noteRepository.deleteById(id);
        log.info("删除笔记成功，ID={}", id);
    }

    public List<Note> searchNotes(String keyword) {
        log.debug("搜索笔记，keyword={}", keyword);
        List<Note> notes = noteRepository.searchNotes(keyword);
        log.debug("搜索笔记完成，找到 {} 条结果", notes.size());
        return notes;
    }

    public List<Note> getNotesByCategory(Long categoryId) {
        log.debug("获取分类下的笔记，categoryId={}", categoryId);
        List<Note> notes = noteRepository.findByCategoryId(categoryId);
        log.debug("获取分类下的笔记完成，categoryId={}, 笔记数量={}", categoryId, notes.size());
        return notes;
    }

    /**
     * 检查是否存在相同内容哈希的笔记
     *
     * @param contentHash 内容哈希
     * @return 是否存在
     */
    public boolean existsByContentHash(String contentHash) {
        log.debug("检查内容哈希是否存在，contentHash={}", contentHash);
        boolean exists = noteRepository.existsByContentHash(contentHash);
        log.debug("检查内容哈希完成，contentHash={}, exists={}", contentHash, exists);
        return exists;
    }

    /**
     * 批量创建笔记（事务管理）
     *
     * @param notes 笔记列表
     * @return 保存后的笔记列表
     */
    @Transactional
    public List<Note> batchCreateNotes(List<Note> notes) {
        log.debug("批量创建笔记，数量={}", notes.size());
        List<Note> savedNotes = noteRepository.saveAll(notes);
        log.info("批量创建笔记成功，数量={}", savedNotes.size());
        return savedNotes;
    }

    /**
     * 计算内容的 MD5 哈希值
     *
     * @param content 内容
     * @return MD5 哈希字符串
     */
    private String calculateMD5Hash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(content.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 算法不可用", e);
            throw new RuntimeException("MD5 算法不可用", e);
        }
    }
}
