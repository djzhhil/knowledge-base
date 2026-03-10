package com.knowledge.dto;

import com.knowledge.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    
    /** 上传成功后关联的笔记ID */
    private Long noteId;
    
    /** 笔记标题 */
    private String title;
    
 /** 响应消息 */
    private String message;
    
    /** 是否上传成功 */
    private Boolean success;

    public static FileUploadResponse success(Note note) {
        FileUploadResponse response = new FileUploadResponse();
        response.setNoteId(note.getId());
        response.setTitle(note.getTitle());
        response.setMessage("文件上传成功");
        response.setSuccess(true);
        return response;
    }

    public static FileUploadResponse error(String message) {
        FileUploadResponse response = new FileUploadResponse();
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }
}
