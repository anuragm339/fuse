package com.fuse.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomUtils {
    private static final int RANDOM_VERSION = 4;

    public static String getRandomUuid() {
        return getRandomUuid(SecureRandomLazyHolder.THREAD_LOCAL_RANDOM.get()).toString();
    }
    public static UUID getRandomUuid(Random random) {
        long msb;
        long lsb;
        if (random instanceof SecureRandom) {
            final byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            msb = toNumber(bytes, 0, 8);
            lsb = toNumber(bytes, 8, 16);
        } else {
            msb = random.nextLong();
            lsb = random.nextLong();
        }

        msb = (msb & 0xffffffffffff0fffL) | (RANDOM_VERSION & 0x0f) << 12;
        lsb = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L;

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
