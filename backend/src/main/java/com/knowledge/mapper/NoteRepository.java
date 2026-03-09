package com.knowledge.mapper;

import com.knowledge.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCategoryId(Long categoryId);

    Long countByCategoryId(Long categoryId);

    Page<Note> findAll(Pageable pageable);

    @Query("SELECT n FROM Note n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\' OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '\\'")
    List<Note> searchNotes(@Param("keyword") String keyword);

    boolean existsByContentHash(String contentHash);
}
