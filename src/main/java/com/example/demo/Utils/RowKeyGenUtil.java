package com.example.demo.Utils;

import com.example.demo.vo.FeedBack;
import com.example.demo.vo.GainPasstemplateRequest;
import com.example.demo.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class RowKeyGenUtil {
    public static String genPasstemplateKey(PassTemplate passTemplate){
        String passInfo = String.valueOf(passTemplate.getId()+"_"+passTemplate.getTitle());
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("genPasstemplateKey{}{}",passInfo,rowKey);
        return rowKey;
    }
    public static String genPassKey(GainPasstemplateRequest request){
        return new StringBuilder( String.valueOf(request.getUserId())
                                    ).reverse().toString()
                + (Long.MAX_VALUE-System.currentTimeMillis())
                + genPasstemplateKey(request.getPassTemplate());
    }
    public static String genFeedBackKey(FeedBack feedBack){
        return  new StringBuilder(String.valueOf(feedBack.getUserId())).reverse().toString()
                + (Long.MAX_VALUE-System.currentTimeMillis());

    }
}
