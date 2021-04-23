package util;

import domain.Tuple;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

    public static Tuple<String, String> encodePassword(String password)
    {
        // Create salt
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*.,/?-=_+`~";

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<10; i++)
        {
            int charPos = (int)(Math.random()*allowedChars.length());
            builder.append(allowedChars.charAt(charPos));
        }

        String salt = builder.toString();

        return new Tuple<>(encodePassword(password, salt), salt);
    }

    public static String encodePassword(String password, String salt)
    {
        //Add salt to password
        String toHash = password + salt;

        try {
            //Hash
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = hasher.digest(
                    toHash.getBytes(StandardCharsets.UTF_8));

            //Return Salt+Hashed Password
            return bytesToHex(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
