package com.example.demo.controller;

import com.example.demo.constants.Constants;
import com.example.demo.log.LogConstants;
import com.example.demo.log.LogGenerator;
import com.example.demo.service.IUserService;
import com.example.demo.vo.Response;
import com.example.demo.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {
    private final IUserService userService;
    private final HttpServletRequest httpServletRequest;
    @Autowired
    public CreateUserController(IUserService userService, HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }
    @ResponseBody
    @PostMapping("/createuser")
    Response createuser(@RequestBody User user) throws Exception{
        LogGenerator.genLog(httpServletRequest,-1L, LogConstants.ActionName.CREATE_USER,user);
        return userService.createUser(user);
    }


}
