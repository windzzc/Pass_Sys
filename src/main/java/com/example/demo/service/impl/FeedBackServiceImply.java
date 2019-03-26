package com.example.demo.service.impl;

import com.example.demo.Utils.RowKeyGenUtil;
import com.example.demo.constants.Constants;
import com.example.demo.mapper.FeedBackRowMapper;
import com.example.demo.service.IFeedBackService;
import com.example.demo.vo.FeedBack;
import com.example.demo.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FeedBackServiceImply implements IFeedBackService {

    private final HbaseTemplate hbaseTemplate;

    @Autowired
    public FeedBackServiceImply(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Response createFeedBack(FeedBack feedBack) {
        if(!feedBack.validate()){
            log.info("cuowu type");
            return Response.failure("cuowu type");
        }
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genFeedBackKey(feedBack)));
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.USER_ID),
                Bytes.toBytes(feedBack.getUserId())
        );
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TYPE),
                Bytes.toBytes(feedBack.getType())
        );
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TEMPLATE_ID),
                Bytes.toBytes(feedBack.getPasstemplateId())
        );
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.COMMENT),
                Bytes.toBytes(feedBack.getComment())
        );
        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME,put);
        return Response.success();
    }

    @Override
    public Response getFeedBack(Long userId) {
        byte[] reverseId = new StringBuilder(String.valueOf(userId)).reverse().toString().getBytes();
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseId));
        List<FeedBack> feedBacks = hbaseTemplate.find(Constants.Feedback.TABLE_NAME,scan,new FeedBackRowMapper());
        return new Response(feedBacks);
    }
}
