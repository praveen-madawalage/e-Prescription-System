package system.utility;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    static final int work_factor = 10;

    private PasswordHasher() {}

    public String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(work_factor));
    }

    public boolean verify(String plain, String hashed) {
        if (plain == null || hashed == null) {
            return false;
        }
        return BCrypt.checkpw(plain, hashed);
    }
}
