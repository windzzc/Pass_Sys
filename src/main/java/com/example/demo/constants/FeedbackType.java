package com.example.demo.constants;

public enum FeedbackType {
    PASS("pass","at pass"),
    APP("app","at app");
    private String code;
    private String msg;
    FeedbackType(String code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public String getCode(){
        return this.code;
    }
    public String getMsg(){
        return this.msg;
    }
}
