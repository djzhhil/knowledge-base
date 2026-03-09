package com.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 笔记实体类
 */
@Data
@Entity
@Table(name = "note")
public class Note {
    /** 笔记ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 笔记标题 */
    @Column(nullable = false)
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /** 笔记内容 */
    @Column(columnDefinition = "TEXT")
    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    private String content;

    /** 所属分类 */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 分类ID（用于前端 JSON 反序列化）
     */
    @Column(name = "category_id", insertable = false, updatable = false)
    @JsonIgnore
    private Long categoryId;

    /**
     * 前端发送 categoryId 时自动转换为 Category 对象
     */
    @JsonProperty("categoryId")
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }

    /**
     * 前端发送 categoryId 自动设置
     */
    @JsonProperty
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /** 笔记标签 */
    @Size(max = 500, message = "标签长度不能超过500个字符")
    @Column(columnDefinition = "VARCHAR(500)")
    private String tags;

    /**
     * 前端发送 tags 数组时自动转换为逗号分隔字符串
     */
    @JsonProperty("tags")
    public List<String> getTagsList() {
        return tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>();
    }

    /**
     * 前端发送 tags 数组时自动转换为逗号分隔字符串
     */
    @JsonProperty("tags")
    public void setTagsList(List<String> tagsList) {
        this.tags = tagsList != null ? String.join(",", tagsList) : "";
    }

    /** 内容哈希值 */
    @Column(name = "content_hash", nullable = false)
    @NotBlank(message = "内容哈希值不能为空")
    private String contentHash;

    /** 创建时间 */
    @JsonIgnore
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 更新时间 */
    @JsonIgnore
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
