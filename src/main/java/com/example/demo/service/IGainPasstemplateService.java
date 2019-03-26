package com.example.demo.service;

import com.example.demo.vo.GainPasstemplateRequest;
import com.example.demo.vo.Response;

public interface IGainPasstemplateService {
    Response gainPasstemplate(GainPasstemplateRequest request) throws Exception;
}
