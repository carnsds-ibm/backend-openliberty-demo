package io.openliberty.guides.application.util;

import java.nio.charset.StandardCharsets;

import java.util.Random;

import com.google.common.hash.Hashing;

public class HelpTools {

    public static String hash(String name, String password) {
        String originalString = name + ":" + password;
        String tempString = randomizeLetters(originalString);
        try {
            String sha256hex = Hashing.sha256()
                .hashString(tempString, StandardCharsets.UTF_8)
                .toString();
 

            return sha256hex;
        } catch (Exception e) {
            return null;
        }
        
    }

    private static String randomizeLetters(String originalString) {
        String[] stringParts = originalString.split(":");
        String newString = "" + stringParts[0];
        Random rand = new Random();

        for (char c:stringParts[1].toCharArray()) {
            if (rand.nextInt(3) == 1) {
                newString += c;
            }
        }
        return newString;
    }
}