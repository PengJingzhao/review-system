package com.pjz.review.common.entity.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Data
public class QuestionCreateRequest implements Serializable {

    @NotBlank(message = "题目标题不能为空")
    @Size(max = 255, message = "题目标题长度不能超过255")
    private String title;

    @NotBlank(message = "答案不能为空")
    private String answer;

    @Size(max = 255, message = "出处长度不能超过255")
    private String source;

    @DecimalMin(value = "0.0", inclusive = true, message = "出现概率范围0~1")
    @DecimalMax(value = "1.0", inclusive = true, message = "出现概率范围0~1")
    private Float appearRate = 0f;

    @NotNull(message = "难度不能为空")
    @Min(value = 1, message = "难度范围 1~5")
    @Max(value = 5, message = "难度范围 1~5")
    private Integer difficulty;

    private List<Long> tagIds;

    private String video;
}
