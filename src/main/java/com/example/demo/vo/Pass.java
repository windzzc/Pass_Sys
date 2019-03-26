package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * yonghu lingqu de youhuiquan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Pass {
    private Long userId;
    private String rowKey;
    private String passtemplateId;
    private String token;
    private Date assignedDate;
    private Date conDate;

}
