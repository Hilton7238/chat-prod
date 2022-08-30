package com.h.authorizecenter.controller;

import com.auth0.jwt.interfaces.Claim;
import com.h.authorizecenter.services.JwtService;
import com.h.authorizecenter.util.Encryption;
import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("authorizecenter")
public class JwtController {
    @Resource
    JwtService jwtService;
    @Resource
    Encryption encryption;

    @PostMapping("/getToken")
    public Response getToken(@RequestBody Map<String, String> map) {
        Map<String, String> jwt = jwtService.getJwt(map);
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), jwt);
    }

    @PostMapping("/authorize")
    public Response doAuthorize(@RequestParam("token") String token) {
        try {
            jwtService.vertify(token);
        } catch (Exception e) {
            return new Response(ResponseStatus.AUTHORIZE_FAIL.getCode(), ResponseStatus.AUTHORIZE_FAIL.getMsg(), null);
        }
        if (jwtService.isFlush(token)) {
            Map<String, Claim> payLoad = jwtService.getPayLoad(token);
            Map<String, String> map = new HashMap<>();
            payLoad.forEach((k, v) -> {
                map.put(k, v.asString());
            });
            Map<String, String> response = jwtService.getJwt(map);
            response.put("uid", map.get("uid"));
            response.put("userCode", map.get("userCode"));
            return new Response(ResponseStatus.FLUSH_TOKEN.getCode(), ResponseStatus.FLUSH_TOKEN.getMsg(), response);
        }
        Map<String, Claim> payLoad = jwtService.getPayLoad(token);
        Map<String, String> response = new HashMap<>();
        response.put("userCode", payLoad.get("userCode").asString());
        response.put("uid", payLoad.get("uid").asString());
        return new Response(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), response);
    }
}
