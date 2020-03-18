package io.openliberty.guides.application.util;

import java.util.Arrays;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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
    
    private static final String DB_HOST = System.getenv("DB_HOST");
    private static final int DB_PORT = Integer.parseInt(System.getenv("DB_PORT"));

    // Database 
    public static final MongoClient MONGO_ADMIN = MongoClients.create("mongodb://" + DBManager.DB_HOST + ":" + DBManager.DB_PORT);
    public static final MongoDatabase DATABASE = DBManager.MONGO_ADMIN.getDatabase(DBManager.DATABASENAME);


    static {
        if (DBManager.DATABASE.getCollection(DBManager.USERS) == null) {
            DBManager.DATABASE.createCollection(DBManager.USERS);
        }

        if (DBManager.DATABASE.getCollection(DBManager.ARTICLES) == null) {
            DBManager.DATABASE.createCollection(DBManager.ARTICLES);
        }
    }

    public static String createUser(String userName, String password) {
        //MongoCredential credential = MongoCredential.createScramSha256Credential(userName, DBManager.DATABASENAME, password.toCharArray());

        //MongoClient mongoClient = MongoClients.create("mongodb://user1:pwd1@host1/?authSource=db1&authMechanism=SCRAM-SHA-256");
        MongoClient mongoClient = MongoClients.create("mongodb://" + userName + ":" + password + "@" + DBManager.DB_HOST + "/?authSource=" + DBManager.DATABASENAME + "&authMechanism=SCRAM-SHA-256");

        return "";
    }
}