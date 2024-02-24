package il.cshaifasweng.OCSFMediatorExample.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashing {
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // Combine password and salt
            String passwordWithSalt = password + salt;

            // Convert the combined string to bytes
            byte[] bytes = passwordWithSalt.getBytes();

            // Update the digest with the bytes
            byte[] hashedBytes = md.digest(bytes);

            // Convert the hashed bytes to a base64-encoded string
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }
        return null;
    }
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
