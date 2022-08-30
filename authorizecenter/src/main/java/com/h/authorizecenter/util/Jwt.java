package com.h.authorizecenter.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class Jwt {
    private final String KEY = "213E184HRWEJHRRIU34U%&9014";

    public Map<String, String> generateJwt(Map<String, String> payLoad) {
        JWTCreator.Builder builder = JWT.create();
        payLoad.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);
        builder.withClaim("exp", calendar.getTimeInMillis());
        Map<String, String> map = new HashMap<>();
        map.put("exp", String.valueOf(calendar.getTimeInMillis()));
        map.put("token", builder.sign(Algorithm.HMAC256(KEY)));
        return map;
    }

    public void verify(String token) throws JWTVerificationException {
        //解析token
        JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);
    }

    public Map<String, Claim> getPayload(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaims();
    }
}
