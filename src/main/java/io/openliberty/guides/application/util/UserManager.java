package io.openliberty.guides.application.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.Iterator;

import javax.ws.rs.core.Cookie;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import redis.clients.jedis.Jedis;

public class UserManager {
    private static final String R_HOST = System.getenv("R_HOST") == null ? "localhost" : System.getenv("R_HOST");
    private static final String R_PORT = System.getenv("R_PORT") == null ? "6379" : System.getenv("R_PORT");
    private static final String R_PASS = System.getenv("R_PASS") == null ? "password" : System.getenv("R_PASS");
    private static final String R_ADDR = "redis://" + R_HOST + ":" + R_PORT;
    private static final String RMAPNAME = "usermap";
    
    public static final Jedis JEDIS = new Jedis(R_HOST);
    public static final String COOKIEOFTHEGODS = "CookieTheDog";
    public static final Base64.Decoder DECODER = Base64.getDecoder();
    public static final int MINIMUMCHARS = 8;
    public static final int FOURHOURSINMILLIS = 14400000;

    public static boolean checkUsernameExists(String userName) {
        Document result = findUser(userName);

        if (result == null) return false;
        return userName.equals(result.getString(DBManager.USER));
    } 

    public static boolean validateUser(String userName, String password) {
        Document result = findUser(userName);
        String name = result.getString(DBManager.USER);
        String temppassword = result.getString(DBManager.PASSWORD);

        if (name.equals(userName) && temppassword.equals(password)) {
            return true;
        }
        return false;
    }

    public static Document findUser(String userName) {
        Iterator<Document> test = DBManager.DATABASE.getCollection(DBManager.USERS).find(Filters.eq(DBManager.USER, userName)).iterator();

        while (test.hasNext()) {
            Document result = test.next();
            String name = result.getString(DBManager.USER);

            if (name.equals(userName)) {
                return result;
            }
        }

        return null;
    }

    public static void insertCache(String userName, String hash, String pass) {
        try {
            JEDIS.set(hash, Serializer.toString(new CacheObject(userName, pass)));
        } catch (IOException e) {
            System.out.println("Could not serialize username: " + userName + " " + e);
        }
        
    }

    public static String checkCache(Cookie cookie, String key) {
        if (cookie == null) {
            if (key == null || key.equals("")) {
                return null;
            } else {
                return key;
            }
        } else {
            return cookie.getValue();
        }
    }

    public static class CacheObject implements Serializable {
        public static final long serialVersionUID = 42;
        public String userName;
        public String mongoPass;

        public CacheObject(String user, String pass) {
            userName = user;
            mongoPass = pass;
        }
    }
}
