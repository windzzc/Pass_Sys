package com.example.demo.vo;

import com.example.demo.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PassInfo {
    private Pass pass;
    private PassTemplate passTemplate;
    private Merchants merchants;
}
