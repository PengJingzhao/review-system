package com.pjz.review.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_content_relation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContentRelation implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long contentId;

    private String relationType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
