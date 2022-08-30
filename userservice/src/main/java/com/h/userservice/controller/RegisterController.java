package com.h.userservice.controller;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import com.h.common.bean.User;
import com.h.userservice.service.RegisterService;
import com.h.userservice.util.RandomCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RequestMapping("user")
@RestController
public class RegisterController {
    @Resource
    RegisterService registerService;
    @Resource
    RandomCode randomCode;


    @RequestMapping("/getCode")
    public void getCode(HttpSession session, HttpServletResponse response, @RequestParam("to") String to, @RequestParam(value = "uid", defaultValue = "", required = false) String uid) {
        try {
            String code = randomCode.generateCode();
            registerService.SendMail(to, "欢迎注册", uid, code);
            session.setAttribute("code", code);
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60);
            response.addCookie(cookie);
            System.out.println("code:" + code);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/register")
    public Response doRegister(@ModelAttribute User user, @RequestParam("file") MultipartFile file, @RequestParam("code") String code, HttpSession session) {
        if (session.getAttribute("code") == null)
            return new Response(ResponseStatus.CODE_TIME_OUT.getCode(), ResponseStatus.CODE_TIME_OUT.getMsg(), null);
        if (!code.equals(session.getAttribute("code")))
            return new Response(ResponseStatus.WARN_CODE.getCode(), ResponseStatus.WARN_CODE.getMsg(), null);
        session.removeAttribute("code");
        List<String> allMails = registerService.getAllMails();
        if (allMails.contains(user.getMail())) {
            return new Response(ResponseStatus.MAIL_ALREADY_EXIST.getCode(), ResponseStatus.MAIL_ALREADY_EXIST.getMsg(), null);
        }
        if (registerService.isUserExit(user.getUid())) {
            return new Response(ResponseStatus.USER_EXIT.getCode(), ResponseStatus.USER_EXIT.getMsg(), null);
        } else {
            try {
                registerService.register(user, file);
            } catch (IOException e) {
                return new Response(ResponseStatus.REGISTER_FAIL.getCode(), ResponseStatus.REGISTER_FAIL.getMsg(), null);
            }
            return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
        }
    }
}
