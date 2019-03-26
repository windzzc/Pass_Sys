package com.example.demo.service.impl;

import com.example.demo.Dao.MerchantsDao;
import com.example.demo.Utils.RowKeyGenUtil;
import com.example.demo.constants.Constants;
import com.example.demo.entity.Merchants;
import com.example.demo.mapper.PassTemplateMapper;
import com.example.demo.service.IInventoryService;
import com.example.demo.service.IUserPassService;
import com.example.demo.vo.*;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryServiceImply implements IInventoryService {
    //@Autowired
    private final HbaseTemplate hbaseTemplate;
    //@Autowired
    private final MerchantsDao merchantsDao;
    //@Autowired
    private final IUserPassService userPassService;

    @Autowired
    public InventoryServiceImply(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao, IUserPassService userPassService) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
        this.userPassService = userPassService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response getInventoryInfo(Long userId) throws Exception {
        Response allUserInfo = userPassService.getUserAllPassInfo(userId);
        List<PassInfo> infos = (List<PassInfo>) allUserInfo.getData();
        List<PassTemplate> passTemplates = infos.stream().map(PassInfo::getPassTemplate).collect(Collectors.toList());
        List<String> ids = new ArrayList<>(passTemplates.size());
        passTemplates.forEach(t->ids.add(RowKeyGenUtil.genPasstemplateKey(t)));
        return new Response(
                new InventoryResponse(userId,buildPassTemplateInfo(getAvailablePassTemplate(ids))));

    }

    private List<PassTemplate> getAvailablePassTemplate(List<String> ids){
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        Scan scan = new Scan();
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.GREATER,
                        new LongComparator(0L))

        );
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes("-1")
                )
                );
        scan.setFilter(filterList);
        List<PassTemplate> passTemplates = hbaseTemplate.find(Constants.PassTemplateTable.TABLE_NAME,scan,new PassTemplateMapper());
        List<PassTemplate> availablePassTemplates = new ArrayList<>();
        Date cur = new Date();
        for(PassTemplate valid : passTemplates){
            if(ids.contains(RowKeyGenUtil.genPasstemplateKey(valid))){
                continue;
            }
            if(cur.getTime()>=valid.getStart().getTime()&&
                    cur.getTime()<=valid.getEnd().getTime()
            ){
                availablePassTemplates.add(valid);
            }
        }
        return availablePassTemplates;

    }

    private List<PassTemplateInfo> buildPassTemplateInfo(List<PassTemplate> avail){
        Map<Integer, Merchants> map = new HashMap<>();
        List<Integer> ids = avail.stream().map(PassTemplate::getId).collect(Collectors.toList());
        List<Merchants> merchants = merchantsDao.findIdByIn(ids);
        merchants.forEach(t->map.put(t.getId(),t));
        List<PassTemplateInfo> res = new ArrayList<>(avail.size());
        for(PassTemplate passTemplate:avail){

            PassTemplateInfo passTemplateInfo = new PassTemplateInfo();
            passTemplateInfo.setMerchants(map.getOrDefault(passTemplate.getId(),null));
            passTemplateInfo.setPassTemplate(passTemplate);

        }
        return res;


    }

}
