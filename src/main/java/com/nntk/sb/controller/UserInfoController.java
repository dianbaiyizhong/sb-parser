package com.nntk.sb.controller;

import com.nntk.sb.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.ApplicationPath;

@RestController
@ApplicationPath("rest") // set the path to REST web services
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    @RequestMapping("/user/api/query")
    public String queryApi() {
        userInfoService.queryUser();
        userInfoService.queryOrder();
        return "success";
    }


    @RequestMapping("/user/api/query/order")
    public String queryOrderApi() {
        userInfoService.queryOrder();
        return "success";
    }
}
