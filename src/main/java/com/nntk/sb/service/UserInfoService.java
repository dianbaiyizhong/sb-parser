package com.nntk.sb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoService implements IUserInfoService {
    @Override
    public void queryUser() {
        log.info("=======================queryUser");
    }

    @Override
    public void queryOrder() {
        log.warn("=======================queryOrder");
    }


}
