package io.openliberty.guides.application.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBManager {
    private DBManager() {}

    // Strings
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DATABASENAME = "admin";
    public static final String ARTICLES = "articles";
    public static final String USERS = "users";
    public static final String USERNOTFOUND = "user not found";
    public static final String USERALREADYEXISTS = "user already exists";

    // Database 
    public static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:8081");
    public static final MongoDatabase DATABASE = DBManager.mongoClient.getDatabase(DBManager.DATABASENAME);


    static {
        if (DBManager.DATABASE.getCollection(DBManager.USERS) == null) {
            DBManager.DATABASE.createCollection(DBManager.USERS);
        }

        if (DBManager.DATABASE.getCollection(DBManager.ARTICLES) == null) {
            DBManager.DATABASE.createCollection(DBManager.ARTICLES);
        }
    }
}