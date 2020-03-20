package io.openliberty.guides.application.util;

import java.util.Arrays;
import java.util.Collections;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class DBManager {
    private DBManager() {}

    // Strings
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DATABASENAME = "clients";
    public static final String ARTICLES = "articles";
    public static final String USERS = "users";
    public static final Document USERNOTFOUND = new Document("msg", "error: user not found");
    public static final Document USERALREADYEXISTS = new Document("msg", "error: user already exists");
    public static final Document INVALID = new Document("msg", "error: credentials invalid");
    public static final Document NOTLOGGEDIN = new Document("msg", "error: user not logged in");
    public static final Document USRLENGTHSHORT = new Document("msg", "error: username length too short");
    public static final Document PWDLENGTHSHORT = new Document("msg", "error: password length too short");
    public static final Document DBFAILURE = new Document("msg", "error: user not created");
    public static final Document SUCCESS = new Document("msg", "success");
    
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

    public static MongoClient createUser(String userName, String password) {
        try {
            final BasicDBObject command = new BasicDBObject("createUser", userName).append("pwd", password).append("roles",
                Collections.singletonList(new BasicDBObject("role", "readWrite").append("db", DBManager.DATABASENAME)));
            if (DATABASE.runCommand(command).getDouble("ok").intValue() == 1) {
                return loginUser(userName, password);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static MongoClient loginUser(String userName, String password) {
        MongoCredential credential = MongoCredential.createScramSha1Credential(userName, DBManager.DATABASENAME, password.toCharArray());

        MongoClient mongoClient = MongoClients.create(
        MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress(DBManager.DB_HOST, DBManager.DB_PORT))))
                .credential(credential)
                .build());

        return mongoClient;
    }
}
