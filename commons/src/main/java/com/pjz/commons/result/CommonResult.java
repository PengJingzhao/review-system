package com.pjz.commons.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonResult<T> {

    private static final long serialVersionUID = 1L;

    // 状态码
    private int code;

    // 响应消息
    private String message;

    // 返回数据
    private T data;

    // 静态方法：成功响应
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "Success", data);
    }

    public static <T> CommonResult<T> success(String message, T data) {
        return new CommonResult<>(200, message, data);
    }

    // 静态方法：失败响应
    public static <T> CommonResult<T> failure(int code, String message) {
        return new CommonResult<>(code, message, null);
    }

    public static <T> CommonResult<T> failure(String message) {
        return new CommonResult<>(400, message, null);
    }


}
