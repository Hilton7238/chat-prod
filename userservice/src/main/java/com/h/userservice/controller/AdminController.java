package com.h.userservice.controller;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import com.h.common.bean.User;
import com.h.userservice.service.AdminService;
import com.h.userservice.service.AuthorizeCenter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Resource
    AuthorizeCenter authorizeCenter;
    @Resource
    AdminService adminService;

    @RequestMapping("/resetPassword")
    public Response resetPassword(@RequestBody List<User> users, HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        Map<String, String> data = (Map<String, String>) response.getData();
        if (response.getCode() == 401 || response.getCode() == 500 || !data.get("userCode").equals("1"))
            return response;
        try {
            adminService.resSetPassword(users);
        } catch (MessagingException e) {
            return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
        }
        if (response.getCode() == 204)
            return response;
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
    }

    @RequestMapping("/deleteUsers")
    public Response deleteUsers(@RequestBody List<User> users, HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        Map<String, String> data = (Map<String, String>) response.getData();
        if (response.getCode() == 401 || response.getCode() == 500 || !data.get("userCode").equals("1"))
            return response;
        adminService.deleteUser(users);
        if (response.getCode() == 204)
            return response;
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
    }

    @RequestMapping("/getCount")
    public Response getCount(@RequestParam("tableName") String tableName) {
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), adminService.getCount(tableName));
    }

    @RequestMapping("/updateMail")
    public Response updateMail(@RequestParam("uid") String uid, @RequestParam("mail") String mail, HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        Map<String, String> data = (Map<String, String>) response.getData();
        if (response.getCode() == 401 || response.getCode() == 500 || !data.get("userCode").equals("1"))
            return response;
        adminService.updateMailByUid(uid, mail);
        if (response.getCode() == 204)
            return response;
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), null);
    }

    @RequestMapping("/getAllUsers")
    public Response getAllUsers(HttpServletRequest request) {
        Response response = authorizeCenter.authorize(request.getHeader("token"));
        Map<String, String> data = (Map<String, String>) response.getData();
        if (response.getCode() == 401 || response.getCode() == 500 || !data.get("userCode").equals("1"))
            return response;
        List<User> allUsers = adminService.getAllUsers();
        if (response.getCode() == 204) {
            response.setData(allUsers);
            return response;
        }
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), allUsers);
    }
}
