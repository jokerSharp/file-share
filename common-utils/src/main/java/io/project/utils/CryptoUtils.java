package io.project.utils;

import org.hashids.Hashids;

public class CryptoUtils {

    private final Hashids hashids;

    public CryptoUtils(String salt) {
        int minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    public Long idOf(String value) {
        long[] result = hashids.decode(value);
        if (result != null && result.length > 0) {
            return result[0];
        }
        return null;
    }
}
