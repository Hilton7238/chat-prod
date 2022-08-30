package com.h.userservice.service;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthorizeCenterFallBack implements AuthorizeCenter {
    @Override
    public Response getToken(Map<String, String> map) {
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }

    @Override
    public Response authorize(String token) {
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }
}
