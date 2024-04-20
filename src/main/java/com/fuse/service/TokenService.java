package com.fuse.service;

import com.fuse.entity.Token;

import java.util.Date;

public interface TokenService {
    void clearExpiredToken();
    void clearToken(Long id);
    Token getToken(String referenceKey);
    Token getToken(long userId);
    String updateToken(Long userId, String newToken, Date expiryDate);
    String createToken(long userId, String newToken, Date expiryDate);
}


