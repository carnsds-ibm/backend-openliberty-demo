package io.openliberty.guides.application;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoClient;

import org.bson.Document;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import io.openliberty.guides.application.client.UnknownUriException;
import io.openliberty.guides.application.client.UnknownUriExceptionMapper;
import io.openliberty.guides.application.models.User;
import io.openliberty.guides.application.util.DBManager;
import io.openliberty.guides.application.util.HelpTools;
import io.openliberty.guides.application.util.Serializer;

import static io.openliberty.guides.application.util.UserManager.*;

@RegisterProvider(UnknownUriExceptionMapper.class)
@RequestScoped
@Path("/User")
public class UserResource {

    @POST
    @Path("/All")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam(COOKIEOFTHEGODS) Cookie cookie, User user)  throws UnknownUriException {
        String key = checkCache(cookie, user.getKey());

        if (key == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }
        
        if (JEDIS.get(key) == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        System.out.println("Retrieving All User Data ... " + UserResource.class.getSimpleName() + " [51]");
        
        Document userDoc = new Document();
        ArrayList<String> names = new ArrayList<>();

        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            names.add(doc.getString(DBManager.USER));
        }
        userDoc.append(DBManager.USERS, names);
        System.out.println("Finished retrieving All User Data ... " + UserResource.class.getSimpleName() + " [55]");

        return Response.status(Response.Status.OK).entity(userDoc.toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Create")
    public Response createUser(User user) throws UnknownUriException { 
        if (user.getUserName().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity(DBManager.USRLENGTHSHORT.toJson()).build();
        }

        if (user.getPassword().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity(DBManager.PWDLENGTHSHORT.toJson()).build();
        }

        if (checkUsernameExists(user.getUserName())) {   
            System.out.println("User Data already exists for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [73]");
            return Response.status(Response.Status.OK).entity(DBManager.USERALREADYEXISTS.toJson()).build();
        }

        if (user.isCheck()) {
            return Response.status(Response.Status.OK).entity(DBManager.SUCCESS.toJson()).build();
        }

        System.out.println("Creating User Data ... for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [83]");
        MongoClient client = DBManager.createUser(user.getUserName(), user.getPassword());

        if (client == null) 
            return Response.status(Response.Status.OK).entity(DBManager.DBFAILURE.toJson()).build();

        Document newUser = new Document(DBManager.USER, user.getUserName());
        DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [89]");
        
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, user.getPassword());
        NewCookie userDataCookie = new NewCookie(COOKIEOFTHEGODS, hash, "/", null, 1,"Hi", FOURHOURSINMILLIS, false);
        client.close();
        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(DBManager.SUCCESS.append(DBManager.KEY, hash).toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Login")
    public Response loginUser(User user) throws UnknownUriException {
        System.out.println("Logging into user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [107]");

        System.out.println("password: " + user.getPassword());
        MongoClient client = DBManager.loginUser(user.getUserName(), user.getPassword());

        if (client == null) {
            return Response.status(Response.Status.OK).entity(DBManager.USERNOTFOUND.toJson()).build();
        }
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, user.getPassword());

        NewCookie userDataCookie = new NewCookie(COOKIEOFTHEGODS, hash, "/", null, 1,"Hi", FOURHOURSINMILLIS, false);
        
        System.out.println("Finished logging in user ... " + UserResource.class.getSimpleName() + " [116]");
        client.close();

        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(DBManager.SUCCESS.append(DBManager.KEY, hash).toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Logout")
    public synchronized Response logoutUser(@CookieParam(COOKIEOFTHEGODS) Cookie cookie, User user) throws UnknownUriException {
        System.out.println("Logging out of user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [136]");
        String key = checkCache(cookie, user.getKey());

        if (key == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        CacheObject cache;

        try {
            cache = (CacheObject)Serializer.fromString(JEDIS.get(key));
        } catch (Exception e) {
            System.out.println("Something bad happened: " + e);
            return Response.status(Response.Status.OK).entity(DBManager.FAILURE.toJson()).build();
        }
        
        if (cache == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        MongoClient client = DBManager.loginUser(cache.userName, cache.mongoPass);
        if (client == null) {
            return Response.status(Response.Status.OK).entity(DBManager.USERNOTFOUND.toJson()).build();
        }
        
        JEDIS.del(key);
        client.close();

        System.out.println("Finished logging out of user ... " + UserResource.class.getSimpleName() + " [137]");

        return Response.status(Response.Status.OK).entity(DBManager.SUCCESS.toJson()).build();
    }
}
