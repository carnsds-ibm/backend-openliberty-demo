package io.openliberty.guides.application.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;

import org.bson.Document;

public class UserManager {

    public static final String COOKIEOFTHEGODS = "CookieTheDog";
    public static HashMap<String, CacheObject> USERCACHE = new HashMap<>();
    public static final Base64.Decoder DECODER = Base64.getDecoder();
    public static final int MINIMUMCHARS = 8;

    private static final long FOURHOURSINMILLIS = 14400000;
    private static final int SLEEPTIME = 10000;

    static {
        new Thread(){
            @Override
            public synchronized void run() {
                while (true) {
                    while (USERCACHE.keySet().iterator().hasNext()) {
                        String key = USERCACHE.keySet().iterator().next();
    
                        if (System.currentTimeMillis() - USERCACHE.get(key).time >= 90500) {
                            USERCACHE.get(key).client.close();
                            USERCACHE.remove(key);
                            System.out.println("Removing cache entry: " + key);
                        }
                    }
                    try {
                        Thread.sleep(SLEEPTIME);
                    } catch (IllegalArgumentException iae) {
                        System.err.println("Negative sleeptime provided to thread " + UserManager.class.getSimpleName() + "[37]");
                    } catch (InterruptedException ie) {
                        System.err.println("Thread was interrupted " + UserManager.class.getSimpleName() + "[37]");
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

    public static void insertCache(String userName, String hash, MongoClient client) {
        USERCACHE.put(hash, new CacheObject(userName, client));
    }

    public static class CacheObject {
        public String userName;
        public long time;
        public MongoClient client;

        public CacheObject(String user, MongoClient c) {
            userName = user;
            time = System.currentTimeMillis();
            client = c;
        }
    }
}
