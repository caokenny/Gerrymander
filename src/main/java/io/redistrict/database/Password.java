package io.redistrict.database;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

public class Password {

    private static final Random RANDOM = new SecureRandom();

    public Password() {}

    public String getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt.toString();
    }

    public String hashPassword(String salt,String password) {
        salt += password;
        String hashed = Hashing.sha256().hashString(salt, StandardCharsets.UTF_8).toString();
        return hashed;
    }

}
