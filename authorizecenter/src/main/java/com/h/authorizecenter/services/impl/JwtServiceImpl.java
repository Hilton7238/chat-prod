package com.h.authorizecenter.services.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.h.authorizecenter.services.JwtService;
import com.h.authorizecenter.util.Jwt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service("jwtService")
public class JwtServiceImpl implements JwtService {
    @Resource
    Jwt jwt;

    @Override
    public Map<String, String> getJwt(Map<String, String> playLoad) {
        return jwt.generateJwt(playLoad);
    }

    @Override
    public void vertify(String token) throws JWTVerificationException {
        jwt.verify(token);
    }

    @Override
    public boolean isFlush(String token) {
        Map<String, Claim> payload = jwt.getPayload(token);
        Long exp = payload.get("exp").asLong();
        if (exp - System.currentTimeMillis() <= 1000 * 60 * 5 && exp >= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Claim> getPayLoad(String token) {
        return jwt.getPayload(token);
    }
}
