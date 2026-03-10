package com.knowledge.service;

import com.knowledge.entity.Category;
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
import java.util.List;

@Service
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;

    public CategoryService(CategoryRepository categoryRepository, NoteRepository noteRepository) {
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
    }

    public List<Category> getAllCategories() {
        log.debug("获取所有分类");
        List<Category> categories = categoryRepository.findAll();

        // 为每个分类计算笔记数量
        for (Category category : categories) {
            Long count = noteRepository.countByCategoryId(category.getId());
            category.setNoteCount(count != null ? count.intValue() : 0);
        }

        return categories;
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        log.debug("获取分类列表，分页参数: {}", pageable);
        return categoryRepository.findAll(pageable);
    }

    /**
     * 根据 ID 获取分类
     *
     * @param id 分类 ID
     * @return 分类对象
     * @throws BusinessException 如果分类不存在
     */
    public Category getCategoryById(Long id) {
        log.debug("根据 ID 获取分类: {}", id);
        return categoryRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("分类不存在，ID: {}", id);
                return new BusinessException("分类不存在");
            });
    }

    @Transactional
    public Category createCategory(Category category) {
        // 参数校验
        if (category == null || !StringUtils.hasText(category.getName())) {
            log.warn("创建分类失败：分类名称为空");
            throw new BusinessException("分类名称不能为空");
        }

        // 检查分类名称是否已存在
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            log.warn("创建分类失败：分类名称已存在，name={}", category.getName());
            throw new BusinessException("分类名称已存在");
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("创建分类成功，ID={}, name={}", savedCategory.getId(), savedCategory.getName());
        return savedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.debug("删除分类，ID={}", id);

        if (!categoryRepository.existsById(id)) {
            log.warn("删除分类失败：分类不存在，ID={}", id);
            throw new BusinessException("分类不存在");
        }

        // 检查分类下是否有笔记
        List<com.knowledge.entity.Note> notes = noteRepository.findByCategoryId(id);
        if (!notes.isEmpty()) {
            log.warn("删除分类失败：该分类下存在笔记，ID={}, 笔记数量={}", id, notes.size());
            throw new BusinessException("该分类下存在笔记，无法删除");
        }

        categoryRepository.deleteById(id);
        log.info("删除分类成功，ID={}", id);
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        log.debug("更新分类，ID={}", id);

        // 参数校验
        if (category == null || !StringUtils.hasText(category.getName())) {
            log.warn("更新分类失败：分类信息为空或名称为空");
            throw new BusinessException("分类信息不能为空，名称不能为空");
        }

        // 查找已有分类
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("更新分类失败：分类不存在，ID={}", id);
                    return new BusinessException("分类不存在");
                });

        // 检查名称是否重复
        if (!existingCategory.getName().equals(category.getName())
                && categoryRepository.findByName(category.getName()).isPresent()) {
            log.warn("更新分类失败：分类名称已存在，name={}", category.getName());
            throw new BusinessException("分类名称已存在");
        }

        // 更新字段
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription()); // 如果有 description 字段
        // 如果有其他字段，也可以在这里更新

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("更新分类成功，ID={}, name={}", updatedCategory.getId(), updatedCategory.getName());
        return updatedCategory;
    }
}
