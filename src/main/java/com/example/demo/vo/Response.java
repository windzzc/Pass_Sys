package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Integer errorCode = 0;
    private String errorMsg =null;
    private Object data;
    public Response(Object data){
        this.data =data;
    }
    public static Response success(){
        return new Response();
    }
    public static Response failure(String erroMsg){
        return new Response(-1,erroMsg,null);
    }
}
