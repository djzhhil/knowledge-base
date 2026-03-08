package com.knowledge.service;

import com.knowledge.entity.Category;
import com.knowledge.exception.BusinessException;
import com.knowledge.mapper.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional
    public Category createCategory(Category category) {
        // 参数校验
        if (category == null || !StringUtils.hasText(category.getName())) {
            throw new BusinessException("分类名称不能为空");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("分类不存在");
        }
        categoryRepository.deleteById(id);
    }
}
