package com.pjz.review.common.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RedisData implements Serializable {

    private LocalDateTime expireTime;

    private Object data;
}
