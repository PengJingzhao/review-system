package com.pjz.review.common.entity.dto;


import lombok.Data;

import java.util.List;

@Data
public class QuestionPageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Integer difficulty; // 1-5，非必填
    private String keyword; // 模糊搜索title和answer
    private List<String> tags; // 以标签列表过滤
}
