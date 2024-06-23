package com.nntk.sb.manager;

import com.nntk.sb.mapper.TUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoManager implements IUserInfoManager {


    @Autowired
    private TUserMapper userMapper;

    @Override
    public void getSomethingByRedis() {
        userMapper.selectByExample(null);
    }
}
