package com.knowledge.controller;

import com.knowledge.dto.Result;
import com.knowledge.entity.Note;
import com.knowledge.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public Result<Page<Note>> getAllNotes(@RequestParam(defaultValue = "0") @Min(0) int page,
                                   @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Page<Note> notes = noteService.getAllNotes(PageRequest.of(page, size));
        return Result.success(notes);
    }

    @GetMapping("/{id}")
    public Result<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        return Result.success(note);
    }

    @PostMapping
    public Result<Note> createNote(@Valid @RequestBody Note note) {
        Note createdNote = noteService.createNote(note);
        return Result.success(createdNote);
    }

    @PutMapping("/{id}")
    public Result<Note> updateNote(@PathVariable Long id, @Valid @RequestBody Note note) {
        Note updatedNote = noteService.updateNote(id, note);
        return Result.success(updatedNote);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/search")
    public Result<List<Note>> searchNotes(@RequestParam String keyword) {
        List<Note> notes = noteService.searchNotes(keyword);
        return Result.success(notes);
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<Note>> getNotesByCategory(@PathVariable Long categoryId) {
        List<Note> notes = noteService.getNotesByCategory(categoryId);
        return Result.success(notes);
    }

    @GetMapping("/tags")
    public Result<Map<String, Integer>> getAllTagsWithCount() {
        List<Note> notes = noteService.getAllNotes();
        Map<String, Integer> tagCountMap = new HashMap<>();

        for (Note note : notes) {
            if (note.getTags() != null && !note.getTags().isEmpty()) {
                String[] tagArray = note.getTags().split(",");
                for (String tag : tagArray) {
                    String tagName = tag.trim();
                    if (!tagName.isEmpty()) {
                        tagCountMap.put(tagName, tagCountMap.getOrDefault(tagName, 0) + 1);
                    }
                }
            }
        }

        return Result.success(tagCountMap);
    }
}
