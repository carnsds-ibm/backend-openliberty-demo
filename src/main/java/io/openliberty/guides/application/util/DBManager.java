package io.openliberty.guides.application.util;

import java.util.Collections;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBManager {
    private DBManager() {}

    // Strings
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DATABASENAME = "clients";
    public static final String ARTICLES = "articles";
    public static final String USERS = "users";
    public static final String USERNOTFOUND = "user not found";
    public static final String USERALREADYEXISTS = "user already exists";
    public static final String INVALID = "credentials invalid";
    
    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final int DB_PORT = Integer.parseInt(System.getenv("DB_PORT"));
    private static final String DB_ADMINUSER = System.getenv("DB_ADMINUSER");
    private static final String DB_ADMINPWD = System.getenv("DB_ADMINPWD");

    // Database 
    public static final MongoClient MONGO_ADMIN = MongoClients.create("mongodb://" + DBManager.DB_ADMINUSER + ":" + DBManager.DB_ADMINPWD + "@" + DBManager.DB_HOST + ":" + DBManager.DB_PORT + "/?authSource=admin&authMechanism=SCRAM-SHA-1");
    public static final MongoDatabase DATABASE = DBManager.MONGO_ADMIN.getDatabase(DBManager.DATABASENAME);


    static {
        if (DBManager.DATABASE.getCollection(DBManager.USERS) == null) {
            DBManager.DATABASE.createCollection(DBManager.USERS);
        }

        if (DBManager.DATABASE.getCollection(DBManager.ARTICLES) == null) {
            DBManager.DATABASE.createCollection(DBManager.ARTICLES);
        }
    }

    public static int createUser(String userName, String password) {
        try {
            final BasicDBObject command = new BasicDBObject("createUser", userName).append("pwd", password).append("roles",
                Collections.singletonList(new BasicDBObject("role", "readWrite").append("db", DBManager.DATABASENAME)));
            return DATABASE.runCommand(command).getDouble("ok").intValue();
        } catch (Exception e) {
            return 2;
        }
    }
}