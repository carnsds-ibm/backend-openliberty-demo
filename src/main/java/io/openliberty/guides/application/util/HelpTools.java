package io.openliberty.guides.application.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.google.common.hash.Hashing;

public class HelpTools {

    public static String hash(String originalString) {
        try {
            // MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // byte[] encodedhash = digest.digest(
            // originalString.getBytes(StandardCharsets.UTF_8));   
            String sha256hex = Hashing.sha256()
                .hashString(originalString, StandardCharsets.UTF_8)
                .toString();


            return sha256hex;
        } catch (Exception e) {
            return null;
        }
        
    }
}