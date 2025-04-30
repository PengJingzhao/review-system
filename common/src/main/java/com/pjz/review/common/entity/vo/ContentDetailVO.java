package com.pjz.review.common.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private UserVO user;

    private String textContent;

    private String mediaUrls;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String tags;

    private String location;

    private Boolean status;

    private Long commentCount;

    private Long likeCount;

    private Long collectionCount;

    private Boolean like;

}
