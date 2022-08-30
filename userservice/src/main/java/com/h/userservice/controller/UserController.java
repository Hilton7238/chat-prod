package com.h.userservice.controller;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import com.h.common.bean.User;
import com.h.userservice.service.AuthorizeCenter;
import com.h.userservice.service.UserBasicService;
import com.h.userservice.util.Encryption;
import com.h.userservice.util.RandomCode;
import com.h.userservice.util.SendMail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    UserBasicService userBasicService;
    @Resource
    SendMail sendMail;
    @Resource
    RandomCode randomCode;
    @Resource
    AuthorizeCenter authorizeCenter;
    @Resource
    Encryption encryption;

    @PostMapping("/changePassword")
    public Response changePassword(@ModelAttribute User user, @RequestParam("code") String code, HttpSession session) {
        if (session.getAttribute("code") == null)
            return new Response(ResponseStatus.CODE_TIME_OUT.getCode(), ResponseStatus.CODE_TIME_OUT.getMsg(), null);
        if (!code.equals(session.getAttribute("code")))
            return new Response(ResponseStatus.WARN_CODE.getCode(), ResponseStatus.WARN_CODE.getMsg(), null);
        session.removeAttribute("code");
        userBasicService.changePassword(user.getUid(), user.getPassword());
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
    }

    @PostMapping("/update/changePassword")
    public Response changePassword(@RequestParam("password") String password, @RequestParam("code") String code, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("code") == null)
            return new Response(ResponseStatus.CODE_TIME_OUT.getCode(), ResponseStatus.CODE_TIME_OUT.getMsg(), null);
        if (!code.equals(session.getAttribute("code")))
            return new Response(ResponseStatus.WARN_CODE.getCode(), ResponseStatus.WARN_CODE.getMsg(), null);
        session.removeAttribute("code");
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        System.out.println(response);
        if (response.getCode() == 401 || response.getCode() == 500)
            return response;
        userBasicService.changePassword(((Map<String, String>) response.getData()).get("uid"), password);
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
    }

    /**
     * *
     *
     * @param uid
     * @param mail
     * @param state    //有传代表修改密码 没有代表忘记密码
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/changePasswordGetCode")
    public Response changePasswordGetCode(@RequestParam("uid") String uid, @RequestParam("mail") String mail, @RequestParam(value = "state", required = false) String state, HttpServletResponse response, HttpSession session) {
        if (state == null && !userBasicService.isUserMail(uid, mail)) {
            return new Response(ResponseStatus.CHANGE_PASSWORD_FAIL.getCode(), ResponseStatus.CHANGE_PASSWORD_FAIL.getMsg(), null);
        }
        try {
            String code = randomCode.generateCode();
            String html;
            if (state == null)
                html = "<h3>" + uid + "用户:</h3>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;听说您忘记了你的密码，所以您的验证码为<span style='color:red;font-weight:800;font-size:18px;'>" + code + "</span></p>";
            else {
                html = "<h3>" + uid + "用户:</h3>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;听说您想修改你的密码，所以您的验证码为<span style='color:red;font-weight:800;font-size:18px;'>" + code + "</span></p>";
            }
            sendMail.send(mail, "更改您的密码", html);
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(60);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            session.setAttribute("code", code);
        } catch (MessagingException e) {
            return new Response(ResponseStatus.CHANGE_PASSWORD_FAIL.getCode(), ResponseStatus.CHANGE_PASSWORD_FAIL.getMsg(), null);
        }
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), ResponseStatus.SUCCESS);
    }

    @RequestMapping("/getFriends")
    public Response getUsers(@RequestParam String uid, HttpServletRequest request) {
        //验证token
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        if (response.getCode() == 401 || response.getCode() == 500) {
            return response;
        }
        System.out.println(request.getHeader("token"));
        List<User> userFriends = userBasicService.getUserFriends(uid);
        Map<String, Object> map = new HashMap<>();
        map.put("friends", userFriends);
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), map);
    }

    @RequestMapping("/deleteFriend")
    public Response deleteFriend(@RequestParam("uid") String uid, @RequestParam("friendUid") String friendUid, HttpServletRequest request) {
        //验证token
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        if (response.getCode() == 401 || response.getCode() == 500) {
            return response;
        }
        boolean b = userBasicService.deleteFriend(uid, friendUid);
        if (b) {
            return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
        }
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }

    @RequestMapping("/getUserInf")
    public Response getUserInf(@RequestParam("uid") String uid, HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        System.out.println(response);
        if (response.getCode() == 401 || response.getCode() == 500) {
            return response;
        }
        Map<String, User> map = new HashMap<>();
        map.put("user", userBasicService.getUser(uid));
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), map);
    }

    @RequestMapping("/searchUsers")
    public Response searchUsers(@RequestParam("uid") String uid) {
        List<User> users = userBasicService.searchUsersByUid(uid);
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), users);
    }

    @RequestMapping("/addFriend")
    public Response addFriend(@RequestParam("uid") String uid, @RequestParam("friendUid") String friendUid, HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        if (response.getCode() == 401 || response.getCode() == 500) {
            return response;
        }
        if (userBasicService.addFriend(uid, friendUid) >= 2) {
            return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
        }
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }

    @PostMapping("updateInf")
    public Response updateInf(@ModelAttribute User user, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("code") String code, HttpServletRequest request) {
        String token = request.getHeader("token");
        boolean b;
        Response response = authorizeCenter.authorize(token);
        if (response.getCode() == 401 || response.getCode() == 204)
            return response;
        HttpSession session = request.getSession();
        System.out.println(code);
        if (!code.equals("") && code != null) {
            if (session.getAttribute("code") == null)
                return new Response(ResponseStatus.CODE_TIME_OUT.getCode(), ResponseStatus.CODE_TIME_OUT.getMsg(), null);
            if (!code.equals(session.getAttribute("code")))
                return new Response(ResponseStatus.WARN_CODE.getCode(), ResponseStatus.WARN_CODE.getMsg(), null);
        }
        try {
            b = userBasicService.updateUserInf(((Map<String, String>) response.getData()).get("uid"), user.getName(), user.getMail(), file);
        } catch (IOException e) {
            return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
        }
        if (b) return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }

    @RequestMapping("/update/getCode")
    public void updateGetCode(@RequestParam("to") String mail, @RequestParam("uid") String uid, HttpSession session, HttpServletResponse response) throws MessagingException {
        try {
            uid = encryption.desDecrypt(uid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String code = randomCode.generateCode();
        String html = "<h3>" + uid + "用户:</h3>" +
                "<p>&nbsp;&nbsp;&nbsp;&nbsp;您正在修改邮箱,您的验证码为<span style='color:red;font-weight:800;font-size:18px;'>" + code + "</span></p>";
        session.setAttribute("code", code);
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        sendMail.send(mail, "你正在修改你的邮箱", html);
    }
}
