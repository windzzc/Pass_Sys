package com.example.demo.service.impl;

import com.example.demo.Utils.RowKeyGenUtil;
import com.example.demo.constants.Constants;
import com.example.demo.service.IHbasePassService;
import com.example.demo.vo.PassTemplate;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HbasePassServiceImply implements IHbasePassService {
    @Autowired
    private final HbaseTemplate hbaseTemplate;

    public HbasePassServiceImply(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public boolean dropPasstemplate2Hbase(PassTemplate passTemplate) throws Exception {
        if (null == passTemplate) return false;
        if (hbaseTemplate.getConnection().getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME)).
                exists(new Get(Bytes.toBytes(RowKeyGenUtil.genPasstemplateKey(passTemplate))))
        ) {
            log.info("yi cunzai");
            return false;
        }
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genPasstemplateKey(passTemplate)));
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.ID),
                Bytes.toBytes(passTemplate.getId())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.TITLE),
                Bytes.toBytes(passTemplate.getTitle())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.SUMMARY),
                Bytes.toBytes(passTemplate.getSummary())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.DESC),
                Bytes.toBytes(passTemplate.getDesc())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN),
                Bytes.toBytes(passTemplate.getHasToken())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND),
                Bytes.toBytes(passTemplate.getBackground())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                Bytes.toBytes(passTemplate.getLimit())
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.START),
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(passTemplate.getStart()))
        );
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.END),
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(passTemplate.getEnd()))
        );
        hbaseTemplate.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);
        return true;
    }

}

