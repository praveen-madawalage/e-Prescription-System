package system.utility;

import java.security.SecureRandom;

public class SecureTokenGenerator {
    private static final SecureRandom random = new SecureRandom();

    private SecureTokenGenerator() {}

    public static String generateToken() {
        int token = random.nextInt(100000);
        return String.format("%06d", token);
    }
}
