package com.nntk.sb.controller;

import com.nntk.sb.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MoneyController {

    @Autowired
    private IUserInfoService userInfoService;

    @RequestMapping("/money/api/query")
    public String queryApi() {
        userInfoService.queryUser();
        userInfoService.queryOrder();
        return "success";
    }


    @RequestMapping("/money/api/query/order")
    public String queryMoneyApi() {
        userInfoService.queryOrder();
        return "success";
    }
}
