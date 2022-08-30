package com.h.userservice.service;

import com.h.common.bean.User;
import com.h.userservice.dao.UserDao;
import com.h.userservice.util.MD5Encrypt;
import com.h.userservice.util.RandomCode;
import com.h.userservice.util.SendMail;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("adminService")
public class AdminService {
    @Resource
    SendMail sendMail;
    @Resource
    RandomCode randomCode;

    @Resource
    UserDao userDao;

    @Resource
    MD5Encrypt md5Encrypt;
    @Resource
    RedisTemplate redisTemplate;

    @Transactional
    public void resSetPassword(List<User> users) throws MessagingException {
        for (User user : users) {
            redisTemplate.delete(user.getUid());
            redisTemplate.delete(user.getUid() + "_password");
            String newPassword = randomCode.generatePassword();
            userDao.updatePasswordByUid(user.getUid(), md5Encrypt.md5(newPassword));
            String html = "<h3>" + user.getUid() + "用户:</h3>" +
                    "<p>&nbsp;&nbsp;&nbsp;&nbsp;您的新密码为：<span style='color:red;font-weight:800;font-size:18px;'>" + newPassword + "</span></p>";
            sendMail.send(user.getMail(), "更改您的密码", html);
        }
    }

    @Transactional
    public void deleteUser(List<User> users) {
        for (User user : users) {
            redisTemplate.delete(user.getUid());
            redisTemplate.delete(user.getUid() + "_password");
            redisTemplate.delete(user.getUid() + "_mail");
            redisTemplate.delete(user.getUid() + "_friends");
            userDao.deleteUser(user.getUid());
        }
    }

    public int getCount(String tableName) {
        return userDao.countColumns(tableName);
    }

    @Transactional
    public void updateMailByUid(String uid, String mail) {
        userDao.updateMailByUid(uid, mail);
    }

    public List<User> getAllUsers() {
        if (redisTemplate.hasKey("users")) {
            List<User> users = redisTemplate.opsForList().range("users", 0, -1);
            return users;
        }
        List<User> allUsers = userDao.getAllUsers();
        redisTemplate.opsForList().leftPushAll("users", allUsers);
        redisTemplate.expire("users", 1, TimeUnit.DAYS);
        return allUsers;
    }
}
