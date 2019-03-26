package com.example.demo.service.impl;

import com.example.demo.Utils.RowKeyGenUtil;
import com.example.demo.constants.Constants;
import com.example.demo.mapper.PassTemplateMapper;
import com.example.demo.service.IGainPasstemplateService;
import com.example.demo.vo.GainPasstemplateRequest;
import com.example.demo.vo.PassTemplate;
import com.example.demo.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GainPasstemplateService implements IGainPasstemplateService {


    private final HbaseTemplate hbaseTemplate;

    private final StringRedisTemplate redisTemplate;
    @Autowired
    public GainPasstemplateService(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Response gainPasstemplate(GainPasstemplateRequest request) throws Exception {
        PassTemplate passTemplate;
        String id = RowKeyGenUtil.genPasstemplateKey(request.getPassTemplate());
        try {
            passTemplate = hbaseTemplate.get(
                    Constants.PassTemplateTable.TABLE_NAME,
                    id,
                    new PassTemplateMapper()
            );
        }
        catch (Exception e){
            log.info("weichadao");
            return Response.failure("CUOWU");
        }
        if(passTemplate.getLimit()<0&&passTemplate.getLimit()!=-1){
            return Response.failure("kucun buzu");
        }
        Date cur = new Date();
        if(! (cur.getTime()>=passTemplate.getStart().getTime()&&cur.getTime()<=passTemplate.getEnd().getTime())){
            return  Response.failure("guoqi");
        }
        if(passTemplate.getLimit()!=-1){
            byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
            List<Mutation> datas = new ArrayList<>();
            Put put = new Put(Bytes.toBytes(passTemplate.getId()));
            put.addColumn(FAMILY_C,
                    LIMIT,
                    Bytes.toBytes(passTemplate.getLimit()-1)
                    );
            datas.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME,datas);
        }
        return Response.success();
    }
}
