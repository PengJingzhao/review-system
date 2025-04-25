package com.pjz.review.common.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopVO implements Serializable {

    private Integer id;

    private String shopName;

    private String shopLogo;

    private String description;

    private String category;

    private String address;

    private String phone;

    private String email;

    private String websiteUrl;

    private String socialMediaLinks;

    private Integer status;

}
