package com.example.demo.service;

import com.example.demo.vo.Pass;
import com.example.demo.vo.Response;

public interface IUserPassService {
    //yonghu youhuiquan xinxi my pass
    Response getUserPassInfo(Long userId) throws Exception;

    Response getUserUsedPassInfo(Long userId) throws Exception;

    Response getUserAllPassInfo(Long userId) throws Exception;

    Response userUsePass(Pass pass);
}
