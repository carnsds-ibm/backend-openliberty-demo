package io.openliberty.guides.application;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoClient;

import org.bson.Document;

import io.openliberty.guides.application.models.User;
import io.openliberty.guides.application.util.DBManager;
import io.openliberty.guides.application.util.HelpTools;
import io.openliberty.guides.application.util.UserManager;

import static io.openliberty.guides.application.util.UserManager.*;

@RequestScoped
@Path("/User")
public class UserResource {

    @GET
    @Path("/All")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam(COOKIEOFTHEGODS) Cookie cookie) {
        if (USERCACHE.get(cookie.getValue()) == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        System.out.println("Retrieving All User Data ... " + UserResource.class.getSimpleName() + " [44]");
        
        Document userDoc = new Document();
        ArrayList<String> names = new ArrayList<>();

        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            names.add(doc.getString(DBManager.USER));
        }
        userDoc.append(DBManager.USERS, names);
        System.out.println("Finished retrieving All User Data ... " + UserResource.class.getSimpleName() + " [48]");

        return Response.status(Response.Status.OK).entity(userDoc.toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Create")
    public Response createUser(User user) { 
        if (user.getUserName().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity(DBManager.USRLENGTHSHORT.toJson()).build();
        }

        if (user.getPassword().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity(DBManager.PWDLENGTHSHORT.toJson()).build();
        }

        if (checkUsernameExists(user.getUserName())) {   
            System.out.println("User Data already exists for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [66]");
            return Response.status(Response.Status.OK).entity(DBManager.USERALREADYEXISTS.toJson()).build();
        }

        if (user.isCheck()) {
            return Response.status(Response.Status.OK).entity(DBManager.SUCCESS.toJson()).build();
        }

        System.out.println("Creating User Data ... for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [76]");
        MongoClient client = DBManager.createUser(user.getUserName(), user.getPassword());

        if (client == null) 
            return Response.status(Response.Status.OK).entity(DBManager.DBFAILURE.toJson()).build();

        Document newUser = new Document(DBManager.USER, user.getUserName());
        DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [82]");
        
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, client);
        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, hash);
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 90, false);
        
        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(DBManager.SUCCESS).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Login")
    public Response loginUser(User user) {
        System.out.println("Logging into user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [100]");

        MongoClient client = DBManager.loginUser(user.getUserName(), user.getPassword());

        if (client == null) {
            return Response.status(Response.Status.OK).entity(DBManager.USERNOTFOUND.toJson()).build();
        }
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, client);

        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, hash);
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 90, false);
        
        System.out.println("Finished logging in user ... " + UserResource.class.getSimpleName() + " [109]");

        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(DBManager.SUCCESS).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/Logout")
    public synchronized Response logoutUser(@CookieParam(COOKIEOFTHEGODS) Cookie cookie, User user) {
        System.out.println("Logging out of user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [129]");
        String cookieValue = cookie.getValue();

        CacheObject cache = USERCACHE.get(cookieValue);
        if (cache == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }
        cache.client.close();
        USERCACHE.remove(cookieValue);

        System.out.println("Finished logging out of user ... " + UserResource.class.getSimpleName() + " [130]");

        return Response.status(Response.Status.OK).entity(DBManager.SUCCESS).build();
    }
}
