package com.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量导入响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImportResponse {
    private Integer total;
    private Integer success;
    private Integer failed;
    private String summaryMessage;
    private List<FileUploadResponse> results;

    /**
     * 获取消息（兼容性方法）
     */
    public String getMessage() {
        return summaryMessage;
    }

    /**
     * 创建批量导入响应
     */
    public static BatchImportResponse create(List<FileUploadResponse> results) {
        BatchImportResponse response = new BatchImportResponse();
        response.setTotal(results.size());
        response.setSuccess(0);
        response.setFailed(0);
        response.setResults(new ArrayList<>());

        for (FileUploadResponse result : results) {
            response.getResults().add(result);
            if (result.getSuccess()) {
                response.setSuccess(response.getSuccess() + 1);
            } else {
                response.setFailed(response.getFailed() + 1);
            }
        }

        response.setSummaryMessage(
                String.format("批量导入完成：成功 %d 个，失败 %d 个", response.getSuccess(), response.getFailed())
        );
        return response;
    }

    public static BatchImportResponse error(String message) {
        BatchImportResponse response = new BatchImportResponse();
        response.setTotal(0);
        response.setSuccess(0);
        response.setFailed(0);
        response.setResults(new ArrayList<>());
        response.setSummaryMessage(message);
        return response;
    }
}
