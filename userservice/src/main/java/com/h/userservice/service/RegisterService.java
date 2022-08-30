package com.h.userservice.service;

import com.h.common.bean.User;
import com.h.userservice.dao.RegisterDao;
import com.h.userservice.util.MD5Encrypt;
import com.h.userservice.util.SendMail;
import com.h.userservice.util.UploadFile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("registerService")
public class RegisterService {
    @Resource
    SendMail sendMail;
    @Resource
    RegisterDao registerDao;
    @Resource
    MD5Encrypt md5Encrypt;
    @Resource
    UploadFile uploadFile;
    @Resource
    RedisTemplate redisTemplate;

    public void SendMail(String to, String subject, String uid, String code) throws MessagingException {
        String html = "<h3>" + uid + "用户:</h3>" +
                "<p>&nbsp;&nbsp;&nbsp;&nbsp;欢迎您注册,您的验证码为<span style='color:red;font-weight:800;font-size:18px;'>" + code + "</span></p>";
        sendMail.send(to, subject, html);
    }

    public boolean isUserExit(String uid) {
        if (registerDao.countUserById(uid) > 0)
            return true;
        else
            return false;
    }

    @Transactional
    public void register(User user, MultipartFile file) throws IOException {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = user.getUid() + suffix;
        String path = "C:/Users/86131/Desktop/html/userpic/";
        //保存到数据库
        String imgUrl = "http://localhost:8080/userpic/" + fileName;
        registerDao.addUser(user.getUid(), md5Encrypt.md5(user.getPassword()), imgUrl, user.getMail(), user.getName());
        uploadFile.upload(fileName, path, file);
    }

    public List<String> getAllMails() {
        if (redisTemplate.hasKey("mails")) {
            List<String> mails = redisTemplate.opsForList().range("mails", 0, -1);
            return mails;
        }
        List<String> allMails = registerDao.getAllMails();
        redisTemplate.opsForList().leftPushAll("mails", allMails);
        redisTemplate.expire("mails", 1, TimeUnit.DAYS);
        return allMails;
    }
}
