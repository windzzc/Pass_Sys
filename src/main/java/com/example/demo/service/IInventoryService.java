package com.example.demo.service;

import com.example.demo.vo.Response;

public interface IInventoryService {
    Response getInventoryInfo(Long userId) throws Exception;
}
