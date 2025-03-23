package com.pjz.review.aop;

import com.pjz.commons.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<Void> illegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException:",e);
        return CommonResult.failure(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Void> exception(Exception e) {
        log.error("Exception:",e);
        return CommonResult.failure(e.getMessage());
    }

}
