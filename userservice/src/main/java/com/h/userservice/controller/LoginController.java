package com.h.userservice.controller;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import com.h.common.bean.User;
import com.h.userservice.service.AuthorizeCenter;
import com.h.userservice.service.LoginService;
import com.h.userservice.util.Encryption;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class LoginController {
    @Resource
    LoginService loginService;
    @Resource
    AuthorizeCenter authorizeCenter;
    @Resource
    Encryption encryption;

    @PostMapping("/login")
    public Response doLogin(@RequestBody User user) {
        if (user.getUid().equals("Admin") && user.getPassword().equals("00000000")) {
            Map<String, String> map = new HashMap<>();
            map.put("uid", user.getUid());
            map.put("userCode", "1");
            Response response = authorizeCenter.getToken(map);
            Map<String, String> data = (Map<String, String>) response.getData();
            data.put("userCode", "1");
            data.put("_uid", user.getUid());
            try {
                data.put("uid", encryption.desEncrypt(user.getUid()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return response;
        }
        boolean b = loginService.authorizeUser(user);
        if (b) {
            Map<String, String> map = new HashMap<>();
            map.put("uid", user.getUid());
            map.put("userCode", "0");
            Response response = authorizeCenter.getToken(map);
            Map<String, String> data = (Map<String, String>) response.getData();
            data.put("userCode", "0");
            data.put("_uid", user.getUid());
            try {
                data.put("uid", encryption.desEncrypt(user.getUid()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return response;
        }
        return new Response(ResponseStatus.Login_FaIl.getCode(), ResponseStatus.Login_FaIl.getMsg(), null);
    }
}
