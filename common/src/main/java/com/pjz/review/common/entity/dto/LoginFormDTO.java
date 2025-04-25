package com.pjz.review.common.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormDTO implements Serializable {

    private String code;

    private String phone;

//    private String password;

}
