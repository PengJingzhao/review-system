package com.pjz.review.common.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("questions")
public class Question implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String answer;

    private String source;

    @TableField(value = "appear_rate")
    private Float appearRate;

    private Integer difficulty;

    @TableField(value = "favorite_count")
    private Integer favoriteCount;

    @TableField(value = "like_count")
    private Integer likeCount;

    @TableField(value = "comment_count")
    private Integer commentCount;

    @TableField(value = "view_count")
    private Integer viewCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 非数据库字段，题目标签列表 */
    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private List<Comment> comments;
}
