package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.demo.Dao.MerchantsDao;
import com.example.demo.constants.Constants;
import com.example.demo.constants.PassStatus;
import com.example.demo.entity.Merchants;
import com.example.demo.mapper.PassRowMapper;
import com.example.demo.service.IUserPassService;
import com.example.demo.vo.Pass;
import com.example.demo.vo.PassInfo;
import com.example.demo.vo.PassTemplate;
import com.example.demo.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.scanner.Constant;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserPassServiceImpl implements IUserPassService {
    @Autowired
    private final HbaseTemplate hbaseTemplate;
    @Autowired
    private final MerchantsDao merchantsDao;

    public UserPassServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
    }

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return getUserInfoByStatus(userId,PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getUserInfoByStatus(userId,PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return getUserInfoByStatus(userId,PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(pass.getUserId())).reverse().toString());
        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        filters.add(new PrefixFilter(rowPrefix));
        filters.add(new SingleColumnValueFilter(
                Bytes.toBytes(Constants.PassTable.FAMILY_I
                        ),
                Constants.PassTable.TEMPLATE_ID.getBytes(),
                CompareFilter.CompareOp.EQUAL,
                pass.getPasstemplateId().getBytes()
                    )
        );
        //when the qualif == the fourth one then begin to search as the filter is
        filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("-1")
        ));
        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(Constants.PassTable.TABLE_NAME,scan,new PassRowMapper());

        if(null==passes||passes.size()!=1){
            log.error("UserUsePass Error: {}", JSON.toJSONString(pass));
            return Response.failure("UserUsePass Error");
        }

        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(passes.get(0).getRowKey().getBytes());
        put.addColumn(FAMILY_I,CON_DATE,Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date())));
        datas.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME,datas);
        return Response.success();

        //return null;
    }
    private Response getUserInfoByStatus(Long userId, PassStatus status) throws Exception{
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(userId)).reverse().toString());
        Scan scan = new Scan();
        CompareFilter.CompareOp comp = status == PassStatus.UNUSED?CompareFilter.CompareOp.EQUAL:
                CompareFilter.CompareOp.NOT_EQUAL;
        List<Filter> filters = new ArrayList<>();
        filters.add(new PrefixFilter(rowPrefix));
        if(status!=PassStatus.ALL){
                filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                comp,
                Bytes.toBytes("-1")
        ));}
        scan.setFilter(new FilterList(filters));

        List<Pass> passes = hbaseTemplate.find(Constants.PassTable.TABLE_NAME,scan,new PassRowMapper());
        //according to the pass id to search the passtemplate
        Map<String,PassTemplate> passTemplateMap = buildTemplateMap(passes);
        //according to the passtemplate id to search the merchants
        Map<Integer,Merchants> merchantsMap = buildMerchantsMap(new ArrayList<>(passTemplateMap.values()));
        List<PassInfo> result = new ArrayList<>();
        for(Pass pass:passes){
            PassTemplate passTemplate = passTemplateMap.getOrDefault(pass.getPasstemplateId(),null);
            if(passTemplate==null) {
                log.info("error");
                continue;
            }
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId(),null);
            //Merchants merchants = merchantsMap.getOrDefault()
            if(null ==  merchants) {
                log.info("error");
                continue;
            }
            result.add(new PassInfo(pass,passTemplate,merchants));

        }
         return new Response(result);
    }
    private Map<String, PassTemplate> buildTemplateMap(List<Pass> passes) throws Exception{
        String[] patterns = new String[] {"yyyy-MM-dd"};

        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);
        List<String> ids = passes.stream().map(Pass::getPasstemplateId).collect(Collectors.toList());
        List<Get> gets = new ArrayList<>(ids.size());
        ids.forEach(t->gets.add(new Get(Bytes.toBytes(t))));
        Result[] results =hbaseTemplate.getConnection().getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                        .get(gets);
        Map<String,PassTemplate> map = new HashMap<>();
        for(Result item:results){
            PassTemplate passTemplate = new PassTemplate();
            passTemplate.setId(Bytes.toInt(item.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(item.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(item.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(item.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(item.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(item.getValue(FAMILY_B, BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(item.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(
                    Bytes.toString(item.getValue(FAMILY_C, START)), patterns));
            passTemplate.setEnd(DateUtils.parseDate(
                    Bytes.toString(item.getValue(FAMILY_C, END)), patterns
            ));
            map.put(Bytes.toString(item.getRow()),passTemplate);
        }
        return map;
    }
    private Map<Integer, Merchants> buildMerchantsMap(List<PassTemplate> passTemplates){
        List<Integer> ids =  passTemplates.stream().map(PassTemplate::getId).collect(Collectors.toList());
        List<Merchants> merchants1 = merchantsDao.findIdByIn(ids);
        Map<Integer, Merchants> map = new HashMap<>();
        merchants1.forEach(t->map.put(t.getId(),t));
        return map;
    }
}
