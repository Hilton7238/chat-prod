package com.h.userservice.service;

import com.alibaba.fastjson.JSON;
import com.h.common.bean.User;
import com.h.userservice.dao.UserDao;
import com.h.userservice.util.Encryption;
import com.h.userservice.util.MD5Encrypt;
import com.h.userservice.util.UploadFile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("userBasicService")
public class UserBasicService {
    @Resource
    UserDao userDao;
    @Resource
    Encryption encryption;
    @Resource
    MD5Encrypt md5Encrypt;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UploadFile uploadFile;

    @Transactional
    public void changePassword(String uid, String password) {
        redisTemplate.delete(uid + "_password");
        String newPassword = md5Encrypt.md5(password);
        userDao.updatePasswordByUid(uid, newPassword);
    }

    public boolean isUserMail(String uid, String mail) {
        if (redisTemplate.hasKey(uid + "_mail")) {
            return mail.equals(redisTemplate.opsForValue().get(uid + "_mail"));
        }
        String emailByUid = userDao.getEmailByUid(uid);
        redisTemplate.opsForValue().set(uid + "_mail", emailByUid, 1, TimeUnit.DAYS);
        return mail.equals(emailByUid);
    }

    public List<User> getUserFriends(String uid) {
        try {
            uid = encryption.desDecrypt(uid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (redisTemplate.hasKey(uid + "_friends")) {
            List<User> users = redisTemplate.opsForList().range(uid + "_friends", 0, -1);
            return users;
        } else {
            List<User> friends = userDao.getFriends(uid);
            if (friends != null) {
                redisTemplate.opsForList().leftPushAll(uid + "_friends", friends);
                redisTemplate.expire(uid + "_friends", 1, TimeUnit.DAYS);
            }
            return friends;
        }
    }

    @Transactional
    public boolean deleteFriend(String uid, String friendUid) {
        //删除缓存
        redisTemplate.delete(uid + "_friends");
        return userDao.deleteFriend(uid, friendUid);
    }

    public User getUser(String uid) {
        try {
            uid = encryption.desDecrypt(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (redisTemplate.hasKey(uid)) {
            String userStr = (String) redisTemplate.opsForValue().get(uid);
            User user = JSON.parseObject(userStr, User.class);
            return user;
        }
        User user = userDao.getUser(uid);
        redisTemplate.opsForValue().set(uid, JSON.toJSONString(user));
        redisTemplate.expire(uid, 1, TimeUnit.DAYS);
        return user;
    }

    public List<User> searchUsersByUid(String uid) {
        return userDao.getUsers(uid);
    }

    @Transactional
    public int addFriend(String uid, String friendUid) {
        //更新缓存
        redisTemplate.delete(uid + "_friends");
        redisTemplate.delete(friendUid + "_friends");
        int i1 = userDao.addFriend(uid, friendUid);
        int i2 = userDao.addFriend(friendUid, uid);
        return i1 + i2;
    }

    @Transactional
    public boolean updateUserInf(String uid, String name, String mail, MultipartFile file) throws IOException {
        //更新缓存
        String oldFileName = null;
        if (redisTemplate.hasKey(uid)) {
            User user = JSON.parseObject(redisTemplate.opsForValue().get(uid).toString(), User.class);
            oldFileName = user.getImgUrl();
        }
        redisTemplate.delete(uid);
        if (file != null) {
            String path = "C:/Users/86131/Desktop/html/userpic/";
            //删除旧文件
            if (oldFileName == null)
                oldFileName = userDao.getUser(uid).getImgUrl();
            String oldSuffix = oldFileName.substring(oldFileName.lastIndexOf("."));
            oldFileName = uid + oldSuffix;
            File oldFile = new File(path + oldFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            //添加新文件
            //保存到数据库
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = uid + suffix;
            String imgUrl = "http://localhost:8080/userpic/" + newFileName;
            uploadFile.upload(newFileName, path, file);
            userDao.updateInf(uid, imgUrl, mail, name);
        } else {
            userDao.updateInf(uid, "", mail, name);
        }
        return true;
    }
}
