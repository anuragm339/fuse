package com.fuse.service.impl;

import com.fuse.entity.Token;
import com.fuse.repository.TokenRepository;
import com.fuse.repository.UserRepository;
import com.fuse.service.TokenService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private static final int RANDOM_VERSION = 4;

    public TokenServiceImpl(TokenRepository tokenRepository,UserRepository userRepository){
        this.tokenRepository=tokenRepository;
        this.userRepository=userRepository;
    }

    public void clearExpiredToken() {
        this.tokenRepository.deleteExpiredTokens();
    }
    public void clearToken(Long id) {
        this.tokenRepository.deleteByUserId(id);
    }

    public Token getToken(String referenceKey) {
        return this.tokenRepository.findOneByReferenceKey(referenceKey);
    }

    public Token getToken(long userId) {
        return this.tokenRepository.findOneByUserId(userId);
    }

    public String updateToken(Long userId, String newToken, Date expiryDate) {
        Token token = this.tokenRepository.findOneByUserId(userId);
        if (token != null) {
            token.setExpiryDate(expiryDate);
            token.setToken(newToken);
            token.setReferenceKey(getRandomUuid(SecureRandomLazyHolder.THREAD_LOCAL_RANDOM.get()).toString());
            this.tokenRepository.save(token);
            return token.getReferenceKey();
        }
        return null;
    }

    public String createToken(long userId, String newToken, Date expiryDate) {
        this.tokenRepository.deleteByUserId(userId);
        Token model = new Token();
        model.setCreateDate(new Date());
        model.setExpiryDate(expiryDate);
        model.setReferenceKey(getRandomUuid(SecureRandomLazyHolder.THREAD_LOCAL_RANDOM.get()).toString());
        model.setToken(newToken);
        model.setUserId(userId);

        this.tokenRepository.save(model);
        return model.getToken();
    }

    private static UUID getRandomUuid(Random random) {

        long msb = 0;
        long lsb = 0;

        // (3) set all bit randomly
        if (random instanceof SecureRandom) {
            // Faster for instances of SecureRandom
            final byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            msb = toNumber(bytes, 0, 8); // first 8 bytes for MSB
            lsb = toNumber(bytes, 8, 16); // last 8 bytes for LSB
        } else {
            msb = random.nextLong(); // first 8 bytes for MSB
            lsb = random.nextLong(); // last 8 bytes for LSB
        }

        // Apply version and variant bits (required for RFC-4122 compliance)
        msb = (msb & 0xffffffffffff0fffL) | (RANDOM_VERSION & 0x0f) << 12; // apply version bits
        lsb = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // apply variant bits

        // Return the UUID
        return new UUID(msb, lsb);
    }
    private static long toNumber(final byte[] bytes, final int start, final int length) {
        long result = 0;
        for (int i = start; i < length; i++) {
            result = (result << 8) | (bytes[i] & 0xff);
        }
        return result;
    }
    private static class SecureRandomLazyHolder {
        static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = ThreadLocal.withInitial(SecureRandom::new);
    }
}
