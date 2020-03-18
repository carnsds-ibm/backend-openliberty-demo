package io.openliberty.guides.application;

import java.util.ArrayList;
import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.model.Filters;

import org.bson.Document;

import io.openliberty.guides.application.models.User;
import io.openliberty.guides.application.util.DBManager;

@RequestScoped
@Path("")
public class UserResource {

    @GET
    @Path("/AllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        System.out.println("Retrieving All User Data ... " + UserResource.class.getSimpleName() + " [30]");
        Document userDoc = new Document();
        ArrayList<String> names = new ArrayList<>();

        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            names.add(doc.getString(DBManager.USER));
        }
        userDoc.append(DBManager.USERS, names);
        System.out.println("Finished retrieving All User Data ... " + UserResource.class.getSimpleName() + " [38]");
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
        
        Document newUser = new Document(DBManager.USER, user.getUserName()).append(DBManager.PASSWORD, user.getPassword());

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
        DBManager.createUser(user.getUserName(), user.getPassword());
        return Response.status(Response.Status.OK).entity(newUser.toString()).build();
    }

    private boolean checkUsernameExists(String userName) {
        Document result = findUser(userName);

        if (result == null) return false;
        return userName.equals(result.getString(DBManager.USER));
    } 

    private boolean validateUser(String userName, String password) {
        Document result = findUser(userName);
        String name = result.getString(DBManager.USER);
        String temppassword = result.getString(DBManager.PASSWORD);

        if (name.equals(userName) && temppassword.equals(password)) {
            return true;
        }
        return false;
    }

    private Document findUser(String userName) {
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
}