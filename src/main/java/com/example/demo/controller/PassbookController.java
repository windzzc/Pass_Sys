package com.example.demo.controller;

import com.example.demo.constants.Constants;
import com.example.demo.log.LogConstants;
import com.example.demo.log.LogGenerator;
import com.example.demo.service.IFeedBackService;
import com.example.demo.service.IGainPasstemplateService;
import com.example.demo.service.IInventoryService;
import com.example.demo.service.IUserPassService;
import com.example.demo.vo.FeedBack;
import com.example.demo.vo.GainPasstemplateRequest;
import com.example.demo.vo.Pass;
import com.example.demo.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {
    /** 用户优惠券服务 */
    private final IUserPassService userPassService;

    /** 优惠券库存服务 */
    private final IInventoryService inventoryService;

    /** 领取优惠券服务 */
    private final IGainPasstemplateService gainPassTemplateService;

    /** 反馈服务 */
    private final IFeedBackService feedbackService;

    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PassbookController(IUserPassService userPassService,
                              IInventoryService inventoryService,
                              IGainPasstemplateService gainPassTemplateService,
                              IFeedBackService feedbackService,
                              HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.gainPassTemplateService = gainPassTemplateService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }
    @ResponseBody
    @GetMapping("/getuserinfo")
    Response getUserInfo(Long userId) throws Exception{
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_PASS_INFO,
                null
        );
        return userPassService.getUserPassInfo(userId);
    }
    @ResponseBody
    @GetMapping("/getusedinfo")
    Response getUsedInfo(Long userId) throws Exception{

        LogGenerator.genLog(
                httpServletRequest,
                userId, LogConstants.ActionName.USER_USED_PASS_INFO,
                null
        );
        return userPassService.getUserUsedPassInfo(userId);
    }
    @ResponseBody
    @PostMapping("/usepass")
    Response usePass(@RequestBody Pass pass){
        LogGenerator.genLog(
                httpServletRequest,
                pass.getUserId(),
                LogConstants.ActionName.USER_USE_PASS,
                pass
        );
        return userPassService.userUsePass(pass);
    }
    @ResponseBody
    @GetMapping("/inventoryinfo")
    Response inventoryInfo(Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.INVENTORY_INFO,
                null
        );
        return inventoryService.getInventoryInfo(userId);
    }
    @ResponseBody
    @PostMapping("/gainpasstemplate")
    Response gainPassTemplate(@RequestBody GainPasstemplateRequest request)
            throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                request.getUserId(),
                LogConstants.ActionName.GAIN_PASSTEMPLATE,
                request
        );
        return gainPassTemplateService.gainPasstemplate(request);
    }
    @ResponseBody
    @PostMapping("/createcomment")
    Response createComment(@RequestBody FeedBack feedBack){

        LogGenerator.genLog(
                httpServletRequest,
                feedBack.getUserId(),
                LogConstants.ActionName.CREATE_FEEDBACK,
                feedBack
        );
        return feedbackService.createFeedBack(feedBack);
    }
    @ResponseBody
    @GetMapping("/getcomment")
    Response getComment(Long userId){
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.GET_FEEDBACK,
                null
        );
        return feedbackService.getFeedBack(userId);
    }
}
