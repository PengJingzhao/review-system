package com.pjz.review.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopBO {

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
