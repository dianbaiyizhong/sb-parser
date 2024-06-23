package com.nntk.sb.service;

import com.nntk.sb.domain.TUserExample;
import com.nntk.sb.manager.UserInfoManager;
import com.nntk.sb.mapper.TUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoService implements IUserInfoService {


    @Autowired
    private UserInfoManager userInfoManager;

    @Autowired
    private TUserMapper userMapper;

    @Override
    public void queryUser() {
        log.info("=======================queryUser");
        userInfoManager.getSomethingByRedis();
    }

    @Override
    public void queryOrder() {
        log.warn("=======================queryOrder");
        userMapper.selectByExample(null);
    }


}
