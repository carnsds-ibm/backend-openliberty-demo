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

import static io.openliberty.guides.application.util.UserManager.*;

@RequestScoped
@Path("")
public class UserResource {

    @GET
    @Path("/AllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@CookieParam(COOKIEOFTHEGODS) Cookie cookie) {
        if (USERCACHE.get(cookie.getValue()) == null) {
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();
        }

        System.out.println("Retrieving All User Data ... " + UserResource.class.getSimpleName() + " [38]");
        
        Document userDoc = new Document();
        ArrayList<String> names = new ArrayList<>();

        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            names.add(doc.getString(DBManager.USER));
        }
        userDoc.append(DBManager.USERS, names);
        System.out.println("Finished retrieving All User Data ... " + UserResource.class.getSimpleName() + " [47]");

        return Response.status(Response.Status.OK).entity(userDoc.toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/CreateUser")
    public Response createUser(User user) { 
        System.out.println(user.getUserName() + " " + user.getPassword());

        if (user.getUserName().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();
        }

        if (user.getPassword().length() < MINIMUMCHARS ) {
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();
        }

        System.out.println("Creating User Data ... for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [81]");

        Document newUser = new Document(DBManager.USER, user.getUserName());

        if (checkUsernameExists(user.getUserName())) {   
            System.out.println("User Data already exists for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [86]");
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.USERALREADYEXISTS + "\"}").build();
        }

        MongoClient client = DBManager.createUser(user.getUserName(), user.getPassword());

        if (client == null) 
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();

        DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [94]");
        
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, client);
        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, hash);
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 90, false);
        
        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(newUser.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/LoginUser")
    public Response loginUser(User user) {
        System.out.println("Logging into user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [107]");

        MongoClient client = DBManager.loginUser(user.getUserName(), user.getPassword());

        if (client == null) {
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();
        }
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash, client);

        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, hash);
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 90, false);
        Document newUser = new Document(DBManager.USER, user.getUserName());
        System.out.println("Finished logging in user ... " + UserResource.class.getSimpleName() + " [118]");

        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(newUser.toString()).build();
    }
}