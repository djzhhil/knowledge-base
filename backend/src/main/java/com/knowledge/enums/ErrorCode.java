package com.knowledge.enums;

import lombok.Getter;

/**
 * 业务异常码枚举
 * 统一管理业务异常的错误码和错误信息
 */
@Getter
public enum ErrorCode {

    // 文件上传相关错误码 (1000-1999)
    FILE_EMPTY(1001, "文件不能为空"),
    FILENAME_EMPTY(1002, "文件名不能为空"),
    FILE_TYPE_INVALID(1003, "不支持的文件类型，仅支持 .md 或 .markdown 文件"),
    FILE_SIZE_EXCEEDED(1004, "文件大小超出限制，最大允许 5MB"),
    FILE_PARSE_FAILED(1005, "文件解析失败"),
    FILE_READ_FAILED(1006, "文件读取失败"),

    // 批量导入相关错误码 (2000-2999)
    BATCH_IMPORT_FAILED(2001, "批量导入失败"),
    BATCH_IMPORT_EMPTY(2002, "请选择要上传的文件"),
    BATCH_IMPORT_PARTIAL_FAILED(2003, "部分文件导入失败"),

    // 笔记相关错误码 (3000-3999)
    NOTE_NOT_FOUND(3001, "笔记不存在"),
    NOTE_CREATE_FAILED(3002, "笔记创建失败"),
    NOTE_UPDATE_FAILED(3003, "笔记更新失败"),
    NOTE_DELETE_FAILED(3004, "笔记删除失败"),

    // 系统错误码 (5000-5999)
    SYSTEM_ERROR(5000, "系统异常，请联系管理员"),
    DATABASE_ERROR(5001, "数据库操作失败"),
    NETWORK_ERROR(5002, "网络异常");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
