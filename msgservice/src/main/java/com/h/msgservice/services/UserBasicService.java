package com.h.msgservice.services;

import com.h.common.bean.User;
import com.h.msgservice.dao.UserDao;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("userBasicService")
public class UserBasicService {
    @Resource
    UserDao userDao;
    @Resource
    RedisTemplate redisTemplate;

    public boolean ifExitFriend(String uid, String friendUid) {
        if (redisTemplate.hasKey(uid + "_friends")) {
            List<User> range = redisTemplate.opsForList().range(uid + "_friends", 0, -1);
            for (User user : range) {
                if (user.getUid().equals(friendUid))
                    return true;
            }
            return false;
        } else {
            if (userDao.getFriend(uid, friendUid) != null)
                return true;
            return false;
        }
    }

}
