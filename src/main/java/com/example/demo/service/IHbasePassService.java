package com.example.demo.service;

import com.example.demo.vo.PassTemplate;

public interface IHbasePassService {
    boolean dropPasstemplate2Hbase(PassTemplate passTemplate) throws Exception;
}
