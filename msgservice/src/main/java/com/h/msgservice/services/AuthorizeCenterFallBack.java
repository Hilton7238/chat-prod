package com.h.msgservice.services;

import com.h.common.bean.Response;
import com.h.common.bean.ResponseStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeCenterFallBack implements AuthorizeCenter {
    @Override
    public Response authorize(String token) {
        System.out.println(token);
        return new Response(ResponseStatus.SERVICE_ERROR.getCode(), ResponseStatus.SERVICE_ERROR.getMsg(), null);
    }
}
