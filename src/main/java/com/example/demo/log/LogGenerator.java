package com.example.demo.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class LogGenerator {
    public static void genLog(HttpServletRequest request, Long userId, String action, Object info){
        log.info(
                JSON.toJSONString(
                        new LogObject(action,userId,System.currentTimeMillis(),request.getRemoteAddr(),info)
                )
        );
    }
}
