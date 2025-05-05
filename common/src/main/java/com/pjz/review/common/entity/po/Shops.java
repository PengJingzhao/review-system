package com.pjz.review.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author pjz
 * @since 2025-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shops")
@NoArgsConstructor
@AllArgsConstructor
public class Shops implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
