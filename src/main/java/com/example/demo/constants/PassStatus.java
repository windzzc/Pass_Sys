package com.example.demo.constants;

public enum PassStatus {
    UNUSED(1,"weiyong"),
    USED(2,"yishiyong"),
    ALL(3,"quanbulingqu");
    private int code;
    private String msg;
    PassStatus(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode(){
        return this.code;
    }
    public String getMsg(){
        return this.msg;
    }

}
