package com.knowledge.service;

import com.knowledge.entity.Note;
import com.knowledge.mapper.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
