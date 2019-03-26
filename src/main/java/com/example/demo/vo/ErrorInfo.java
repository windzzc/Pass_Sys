package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo<T> {

    public static final Integer ERROR = -1;
    private Integer code;
    private String msg;
    private String url;
    private T data;
}
