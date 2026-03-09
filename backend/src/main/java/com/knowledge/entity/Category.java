package com.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
@Entity
@Table(name = "category")
public class Category {
    
    /** 分类ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 分类名称 */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;
    
    /** 分类描述 */
    private String description;

    /** 笔记数量（不持久化） */
    @Transient
    private Integer noteCount;

    /** 创建时间 */
    @JsonIgnore
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}