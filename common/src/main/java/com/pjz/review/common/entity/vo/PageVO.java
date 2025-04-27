package com.pjz.review.common.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVO<T>  implements Serializable {

    private static final long serialVersionUID = 1L;

    List<T> records;

    Long total;

    Long current;

    Long size;

}
