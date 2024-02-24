package de.janbnz.url.auth;

import org.mindrot.jbcrypt.BCrypt;

public class Encryption {

    private final String cryptSalt;

    public Encryption(String cryptSalt) {
        this.cryptSalt = cryptSalt;
    }

    public String hash(String toHash) {
        return BCrypt.hashpw(toHash, this.cryptSalt);
    }

    public boolean verify(String original, String hashed) {
        return BCrypt.checkpw(original, hashed);
    }
}