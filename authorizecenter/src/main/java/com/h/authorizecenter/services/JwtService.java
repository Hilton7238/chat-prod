package com.h.authorizecenter.services;

import com.auth0.jwt.interfaces.Claim;

import java.util.Map;

public interface JwtService {
    public Map<String, String> getJwt(Map<String, String> playLoad);

    public void vertify(String token);

    /**
     * @param token
     * @return true mean need to flush
     */
    public boolean isFlush(String token);

    public Map<String, Claim> getPayLoad(String token);
}
