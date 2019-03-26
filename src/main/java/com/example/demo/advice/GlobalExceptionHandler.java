package com.example.demo.advice;

import com.example.demo.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value=Exception.class)
    public ErrorInfo<String> errorHandler(HttpServletRequest request,Exception ex)throws Exception{
        ErrorInfo<String> errorInfo = new ErrorInfo<>();
        errorInfo.setCode(ErrorInfo.ERROR);
        errorInfo.setMsg(ex.getMessage());
        errorInfo.setUrl(request.getRequestURL().toString());
        errorInfo.setData("do not return data");
        return errorInfo;

    }
}
