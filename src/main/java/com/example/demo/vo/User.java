package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private BaseInfo baseInfo;
    private OtherInfo otherInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor

    public static class BaseInfo{
        private String name;
        private Integer age;
        private String sex;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherInfo{
        private String phone;
        private String address;
    }
}
