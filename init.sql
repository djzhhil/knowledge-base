-- 知识库数据库初始化脚本

CREATE DATABASE IF NOT EXISTS knowledge_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE knowledge_db;

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(255) COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容（Markdown格式）',
    category_id BIGINT COMMENT '分类ID',
    tags VARCHAR(255) COMMENT '标签（逗号分隔）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_category (category_id),
    KEY idx_title (title),
    FULLTEXT INDEX idx_content (title, content),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记表';

-- 插入默认分类数据
INSERT INTO category (name, description) VALUES
('计算机专业课', '大学计算机专业课相关笔记'),
('编程语言', 'Java, Python, JavaScript 等编程语言'),
('框架技术', 'SpringBoot, Vue, Uniapp 等框架'),
('算法与数据结构', '算法、数据结构相关笔记'),
('网络安全', 'Web渗透测试、安全相关笔记'),
('其他', '其他分类');
