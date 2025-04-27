package com.pjz.review.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("biz_counts")
public class BizCount implements Serializable {

    @TableId
    private Long id;

    private String bizType;    // 业务类型，如 article

    private String bizId;      // 业务ID，如文章ID

    private String countType;  // 计数字段类型，如 like, comment, favorite

    private Long countValue;   // 计数值

    private LocalDateTime updateTime; // 更新时间
}