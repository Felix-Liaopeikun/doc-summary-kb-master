package com.example.dockb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 回答评测请求。
 */
@Data
public class EvaluateRequest {

    /** 评分 1-5 星。 */
    @Min(1)
    @Max(5)
    private Integer rating;

    /** 是否有用。 */
    private Boolean useful;

    /** 反馈文本（可选）。 */
    private String feedback;
}
