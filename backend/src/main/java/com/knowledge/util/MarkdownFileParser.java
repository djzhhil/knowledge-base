package com.knowledge.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 文件解析工具类
 * 支持解析 Markdown 文件的标题、内容和 frontmatter 元数据
 */
@Slf4j
public class MarkdownFileParser {

    /**
     * Jackson ObjectMapper 用于 JSON 解析
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 默认最大文件大小（5MB）
     */
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 允许的文件扩展名
     */
    public static final String[] ALLOWED_EXTENSIONS = {"md", "markdown"};

    /**
     * 解析 Markdown 文件内容
     *
     * @param filename    文件名（用于获取扩展名和默认标题）
     * @param fileContent 文件内容
     * @return 解析结果 Map，包含 title, content, tags, category
     */
    public static Map<String, String> parseMarkdownFile(String filename, String fileContent) {
        Map<String, String> result = new HashMap<>();

        // 1. 解析 frontmatter 元数据
        Map<String, String> frontmatter = parseFrontmatter(fileContent);
        String contentWithoutFrontmatter = removeFrontmatter(fileContent);

        // 2. 提取标题（优先级：frontmatter > 第一级标题 > 文件名）
        String title = frontmatter.getOrDefault("title", "");
        if (StringUtils.isBlank(title)) {
            title = extractFirstHeading(contentWithoutFrontmatter);
        }
        if (StringUtils.isBlank(title)) {
            title = getDefaultTitle(filename);
        }

        // 3. 提取标签
        String tags = frontmatter.getOrDefault("tags", "");
        if (StringUtils.isBlank(tags)) {
            tags = extractTagsFromContent(contentWithoutFrontmatter);
        }

        // 4. 提取分类
        String category = frontmatter.getOrDefault("category", "");

        // 5. 设置结果
        result.put("title", title);
        result.put("content", contentWithoutFrontmatter);
        result.put("tags", tags);
        result.put("category", category);

        log.debug("解析完成 - 标题: {}, 标签: {}, 分类: {}", title, tags, category);
        return result;
    }

    /**
     * 解析 frontmatter 元数据
     * 支持两种格式：
     * 1. YAML 格式（以 --- 包围）
     * 2. JSON 格式（以 ```json 包围）
     *
     * @param content Markdown 内容
     * @return frontmatter 元数据 Map
     */
    private static Map<String, String> parseFrontmatter(String content) {
        Map<String, String> frontmatter = new HashMap<>();

        if (StringUtils.isBlank(content)) {
            return frontmatter;
        }

        // 检测 YAML 格式 frontmatter
        Pattern yamlPattern = Pattern.compile("^---\\s*\\n([\\s\\S]*?)\\n---", Pattern.MULTILINE);
        Matcher yamlMatcher = yamlPattern.matcher(content);

        if (yamlMatcher.find()) {
            String yamlContent = yamlMatcher.group(1);
            parseYamlFrontmatter(yamlContent, frontmatter);
            return frontmatter;
        }

        // 检测 JSON 格式 frontmatter
        Pattern jsonPattern = Pattern.compile("^```json\\s*\\n([\\s\\S]*?)\\n```", Pattern.MULTILINE);
        Matcher jsonMatcher = jsonPattern.matcher(content);

        if (jsonMatcher.find()) {
            String jsonContent = jsonMatcher.group(1);
            parseJsonFrontmatter(jsonContent, frontmatter);
            return frontmatter;
        }

        return frontmatter;
    }

    /**
     * 解析 YAML 格式 frontmatter
     *
     * @param yamlContent   YAML 内容
     * @param frontmatter   存储解析结果的 Map
     */
    private static void parseYamlFrontmatter(String yamlContent, Map<String, String> frontmatter) {
        try (BufferedReader reader = new BufferedReader(new StringReader(yamlContent))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || StringUtils.isBlank(line)) {
                    continue;
                }

                // 解析 key: value 格式
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();

                    // 去除引号
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    frontmatter.put(key, value);
                }
            }
        } catch (IOException e) {
            log.error("解析 YAML frontmatter 失败", e);
        }
    }

    /**
     * 解析 JSON 格式 frontmatter（使用 Jackson）
     *
     * @param jsonContent   JSON 内容
     * @param frontmatter   存储解析结果的 Map
     */
    private static void parseJsonFrontmatter(String jsonContent, Map<String, String> frontmatter) {
        try {
            Map<String, String> parsed = objectMapper.readValue(jsonContent, new TypeReference<Map<String, String>>() {});
            frontmatter.putAll(parsed);
        } catch (Exception e) {
            log.warn("JSON frontmatter 解析失败: {}", e.getMessage());
        }
    }

    /**
     * 移除 frontmatter，返回纯内容
     *
     * @param content Markdown 内容
     * @return 移除 frontmatter 后的内容
     */
    private static String removeFrontmatter(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        // 移除 YAML 格式 frontmatter
        content = content.replaceFirst("^---\\s*\\n[\\s\\S]*?\\n---\\s*\\n", "");

        // 移除 JSON 格式 frontmatter
        content = content.replaceFirst("^```json\\s*\\n[\\s\\S]*?\\n```\\s*\\n", "");

        return content.trim();
    }

    /**
     * 提取第一个一级标题
     *
     * @param content Markdown 内容
     * @return 标题文本，如果没有则返回空字符串
     */
    private static String extractFirstHeading(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        Pattern pattern = Pattern.compile("^#+\\s+(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "";
    }

    /**
     * 从内容中提取标签（简单的 #tag 语法）
     *
     * @param content Markdown 内容
     * @return 标签字符串，多个标签用逗号分隔
     */
    private static String extractTagsFromContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);
        StringBuilder tags = new StringBuilder();
        boolean first = true;

        while (matcher.find()) {
            if (!first) {
                tags.append(", ");
            }
            tags.append(matcher.group(1));
            first = false;
        }

        return tags.toString();
    }

    /**
     * 从文件名获取默认标题
     *
     * @param filename 文件名
     * @return 默认标题（去除扩展名）
     */
    public static String getDefaultTitle(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }

        // 获取不含扩展名的文件名
        String baseName = FilenameUtils.getBaseName(filename);
        if (StringUtils.isBlank(baseName)) {
            return filename;
        }

        String result = baseName.replace("-", " ").replace("_", " ");

        Pattern pattern = Pattern.compile("\\b\\w");
        Matcher matcher = pattern.matcher(result);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().toUpperCase());
        }
        matcher.appendTail(sb);

        return sb.toString().trim();
    }

    /**
     * 验证文件大小
     *
     * @param size 文件大小（字节）
     * @return 是否通过验证
     */
    public static boolean validateFileSize(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    /**
     * 验证文件扩展名
     *
     * @param filename 文件名
     * @return 是否通过验证
     */
    public static boolean validateFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return false;
        }

        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        if (StringUtils.isBlank(extension)) {
            return false;
        }

        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 验证文件是否有效
     *
     * @param filename 文件名
     * @param size     文件大小
     * @return 是否通过验证
     */
    public static void validateFile(String filename, long size) {
        if (!validateFileExtension(filename)) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 .md 或 .markdown 文件");
        }

        if (!validateFileSize(size)) {
            throw new IllegalArgumentException("文件大小超出限制，最大允许 5MB");
        }
    }
}
