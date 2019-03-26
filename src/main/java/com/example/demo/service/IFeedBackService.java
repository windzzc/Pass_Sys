package com.example.demo.service;

import com.example.demo.vo.FeedBack;
import com.example.demo.vo.Response;

public interface IFeedBackService {
    Response createFeedBack(FeedBack feedBack);
    Response getFeedBack(Long userId);
}
