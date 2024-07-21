package com.luyou.bi.model.vo;

import lombok.Data;

/**
 * @Author: 鹿又笑
 * @Create: 2024/7/16 16:42
 * @description:
 */
@Data
public class BiResponse {

    private String genChart;

    private String genResult;

    // 新生成的图标id
    private Long chartId;

}
