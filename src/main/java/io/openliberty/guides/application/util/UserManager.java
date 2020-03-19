package io.openliberty.guides.application.util;

import java.util.HashMap;
import java.util.Iterator;

import com.mongodb.client.model.Filters;

import org.bson.Document;

public class UserManager {

    public static final String COOKIEOFTHEGODS = "CookieTheDog";
    public static HashMap<String, CacheObject> USERCACHE = new HashMap<>();
    private static final long dayInMillis = 86400000;

    static {
        new Thread(){
            @Override
            public synchronized void run() {
                while (true) {
                    while (USERCACHE.keySet().iterator().hasNext()) {
                        String key = USERCACHE.keySet().iterator().next();
    
                        if (System.currentTimeMillis() - USERCACHE.get(key).time >= 30000) {
                            USERCACHE.remove(key);
                            System.out.println("Removing entry: " + key);
                        }
                    }
                }
            }
        }.start();
    }

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

    public static void insertCache(String userName, String hash) {
        USERCACHE.put(hash, new CacheObject(userName));
        System.out.println("Added to cache");
    }

    public static class CacheObject {
        public String userName;
        public long time;

        public CacheObject(String user) {
            userName = user;
            time = System.currentTimeMillis();
            System.out.println("Cache Object Created");
        }
    }
}