package com.h.userservice.service;

import com.h.common.bean.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "AUTHORIZECENTER", fallback = AuthorizeCenterFallBack.class)
public interface AuthorizeCenter {
    @PostMapping("authorizecenter/getToken")
    public Response getToken(@RequestBody Map<String, String> map);

    @PostMapping("authorizecenter/authorize")
    public Response authorize(@RequestParam("token") String token);
}
