package com.pjz.review.common.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    private Integer id;

    private String name;

    private String email;

    private String phone;

    private Integer attentionCount;

    private Integer followerCount;

}
