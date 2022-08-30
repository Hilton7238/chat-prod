package com.h.userservice.service;

import com.h.common.bean.User;
import com.h.userservice.dao.LoginDao;
import com.h.userservice.util.MD5Encrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service("loginService")
public class LoginService {
    @Resource
    LoginDao loginDao;
    @Resource
    MD5Encrypt md5Encrypt;
    @Resource
    RedisTemplate redisTemplate;

    public boolean authorizeUser(User user) {
        if (redisTemplate.hasKey(user.getUid() + "_password")) {
            return redisTemplate.opsForValue().get(user.getUid() + "_password").equals(md5Encrypt.md5(user.getPassword()));
        }
        String password = loginDao.getPasswordByUid(user.getUid());
        redisTemplate.opsForValue().set(user.getUid() + "_password", password, 1, TimeUnit.DAYS);
        return md5Encrypt.md5(user.getPassword()).equals(password);
    }
}
