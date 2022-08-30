package com.h.msgservice.services;

import com.h.common.bean.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "AUTHORIZECENTER", fallback = AuthorizeCenterFallBack.class)
public interface AuthorizeCenter {
    @PostMapping("authorizecenter/authorize")
    public Response authorize(@RequestParam("token") String token);
}
