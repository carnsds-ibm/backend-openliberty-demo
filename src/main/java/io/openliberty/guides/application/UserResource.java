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
        System.out.println("Retrieving All User Data ... " + UserResource.class.getSimpleName() + " [30]");
        Document userDoc = new Document();
        ArrayList<String> names = new ArrayList<>();

        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            names.add(doc.getString(DBManager.USER));
        }
        userDoc.append(DBManager.USERS, names);
        System.out.println("Finished retrieving All User Data ... " + UserResource.class.getSimpleName() + " [38]");

        if (cookie == null) {
            System.out.println("No Cookie");
        } else {
            System.out.println(cookie.getValue());
        }

        return Response.status(Response.Status.OK).entity(userDoc.toJson()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/User")
    public Response getUser(User user) {
        System.out.println("Retrieving User Data ... " + UserResource.class.getSimpleName() + " [47]");
        if (!validateUser(user.getUserName(), user.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.USERNOTFOUND + "\"}").build();
        }

        Document doc = findUser(user.getUserName());
        System.out.println("Finished retrieving User Data ... " + UserResource.class.getSimpleName() + " [53]");
        
        return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + doc.getString(DBManager.USER) + "\"}").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/CreateUser")
    public Response createUser(User user) {
        System.out.println("Creating User Data ... for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [63]");
        
        Document newUser = new Document(DBManager.USER, user.getUserName());
        if (DBManager.createUser(user.getUserName(), user.getPassword()) != 1) 
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();

        if (!checkUsernameExists(user.getUserName())) {
            DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        } else {
            System.out.println("User Data already exists for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [70]");
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.USERALREADYEXISTS + "\"}").build();
        }
        
        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            System.out.println(doc);
        }
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [76]");
        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, HelpTools.hash(user.getUserName(), user.getPassword()));
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 30, false);
        
        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(newUser.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/LoginUser")
    public Response loginUser(User user) {
        System.out.println("Logging into user: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [63]");

        if (DBManager.loginUser(user.getUserName(), user.getPassword()) != 1) {
            return Response.status(Response.Status.OK).entity("{ \"" + DBManager.USER + "\": \"" + DBManager.INVALID + "\"}").build();
        }
        String hash = HelpTools.hash(user.getUserName(), user.getPassword());
        insertCache(user.getUserName(), hash);

        NewCookie userCookie = new NewCookie(COOKIEOFTHEGODS, hash);
        NewCookie userDataCookie = new NewCookie(userCookie, "Hi", 30, false);
        Document newUser = new Document(DBManager.USER, user.getUserName());
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [76]");

        return Response.status(Response.Status.OK).cookie(userDataCookie).entity(newUser.toString()).build();
    }
}