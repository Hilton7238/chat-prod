package com.h.userservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class SendMail {
    @Value("${spring.mail.username}")
    private String from;
    @Resource
    JavaMailSender javaMailSender;

    public void send(String to, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        // 设置邮件内容，第二个参数设置是否支持 text/html 类型
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

}