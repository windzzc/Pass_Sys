package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class InventoryResponse {
    private Long userId;

    private List<PassTemplateInfo> passTemplateInfos;

}
