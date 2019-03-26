package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class GainPasstemplateRequest {
    private Long userId;
    private PassTemplate passTemplate;
}
