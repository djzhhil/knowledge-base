package com.knowledge.controller;

import com.knowledge.dto.BatchImportResponse;
import com.knowledge.dto.FileUploadResponse;
import com.knowledge.dto.Result;
import com.knowledge.entity.Note;
import com.knowledge.exception.BusinessException;
import com.knowledge.service.NoteService;
import com.knowledge.util.MarkdownFileParser;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 * 处理单文件上传和批量导入功能
 */
@Slf4j
@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final NoteService noteService;
    private final ExecutorService executorService;

    public FileUploadController(NoteService noteService) {
        this.noteService = noteService;
        // 创建固定大小线程池，用于并行处理文件
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * 单文件上传接口
     * POST /api/notes/upload
     *
     * @param file 上传的文件（必须为 .md 文件）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("开始处理文件上传: {}", file.getOriginalFilename());

        try {
            // 1. 文件校验
            validateUploadedFile(file);

            // 2. 读取文件内容
            String filename = file.getOriginalFilename();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 3. 解析 Markdown 文件
            Map<String, String> parsed = MarkdownFileParser.parseMarkdownFile(filename, content);

            // 4. 创建笔记实体
            Note note = createNoteFromParsedData(parsed);

            // 5. 保存到数据库
            Note savedNote = noteService.createNote(note);

            log.info("文件上传成功: {} -> ID: {}", filename, savedNote.getId());

            return Result.success(FileUploadResponse.success(savedNote));

        } catch (IllegalArgumentException e) {
            log.warn("文件上传失败: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return Result.error(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入接口
     * POST /api/notes/import
     *
     * @param files 上传的文件列表
     * @return 批量导入结果
     */
    @PostMapping("/import")
    public Result<BatchImportResponse> importFiles(@RequestParam("files") List<MultipartFile> files) {
        log.info("开始批量导入: 文件数量 = {}", files.size());

        try {
            // 校验文件列表
            if (files == null || files.isEmpty()) {
                return Result.error(400, "请选择要上传的文件");
            }

            // 并行处理所有文件
            List<CompletableFuture<FileUploadResponse>> futures = files.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> processSingleFile(file), executorService))
                    .collect(Collectors.toList());

            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            allFutures.join();

            // 收集结果
            List<FileUploadResponse> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // 构建批量导入响应
            BatchImportResponse response = BatchImportResponse.create(results);

            log.info("批量导入完成: {}", response.getMessage());

            return Result.success(response);

        } catch (Exception e) {
            log.error("批量导入异常", e);
            return Result.error(500, "批量导入失败: " + e.getMessage());
        }
    }

    /**
     * 处理单个文件上传（内部方法）
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    private FileUploadResponse processSingleFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.debug("处理文件: {}", filename);

        try {
            // 文件校验
            validateUploadedFile(file);

            // 读取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 解析 Markdown 文件
            Map<String, String> parsed = MarkdownFileParser.parseMarkdownFile(filename, content);

            // 创建笔记实体
            Note note = createNoteFromParsedData(parsed);

            // 保存到数据库
            Note savedNote = noteService.createNote(note);

            log.debug("文件处理成功: {} -> ID: {}", filename, savedNote.getId());

            return FileUploadResponse.success(savedNote);

        } catch (IllegalArgumentException e) {
            log.warn("文件处理失败: {} - {}", filename, e.getMessage());
            return FileUploadResponse.error(filename + ": " + e.getMessage());
        } catch (Exception e) {
            log.error("文件处理异常: {}", filename, e);
            return FileUploadResponse.error(filename + ": " + e.getMessage());
        }
    }

    /**
     * 验证上传的文件
     *
     * @param file 上传的文件
     * @throws IllegalArgumentException 如果文件无效
     */
    private void validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 验证文件扩展名和大小
        MarkdownFileParser.validateFile(filename, file.getSize());
    }

    /**
     * 从解析数据创建笔记实体
     *
     * @param parsed 解析后的数据
     * @return 笔记实体
     */
    private Note createNoteFromParsedData(Map<String, String> parsed) {
        Note note = new Note();
        note.setTitle(parsed.get("title"));
        note.setContent(parsed.get("content"));
        note.setTags(parsed.get("tags"));

        // 处理分类（如果数据库中有对应分类则关联，否则为 null）
        String categoryName = parsed.get("category");
        if (StringUtils.isNotBlank(categoryName)) {
            // 注意：这里需要根据实际需求实现分类查找逻辑
            // 暂时设置为 null，可以根据实际需求扩展
            note.setCategoryId(null);
        }

        return note;
    }

    /**
     * 应用关闭时优雅关闭线程池
     */
    @jakarta.annotation.PreDestroy
    public void shutdown() {
        log.info("开始关闭文件上传线程池...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                log.warn("线程池未在60秒内关闭，强制关闭");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.error("线程池无法关闭");
                }
            } else {
                log.info("文件上传线程池已成功关闭");
            }
        } catch (java.lang.InterruptedException e) {
            log.error("线程池关闭时被中断", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
