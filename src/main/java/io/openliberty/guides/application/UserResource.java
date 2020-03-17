package io.openliberty.guides.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import io.openliberty.guides.application.util.DBManager;

@RequestScoped
@Path("")
public class UserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/User")
    public Response getResource() {
        System.out.println("Retrieving User Data ... " + UserResource.class.getSimpleName() + " [21]");
        String test = DBManager.DATABASE.getCollection(DBManager.USERS).find(Filters.eq(DBManager.USER, "Dennis")).first().toString();
        System.out.println(test);
        System.out.println(checkUsernameExists("Dennis"));
        System.out.println(validateUser("Dennis", "password"));
        System.out.println("Finished retrieving User Data ... " + UserResource.class.getSimpleName() + " [23]");

        return Response.status(Response.Status.OK).entity("{ \"user\": \"" + test + "\" , \"user2\": 2}").build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/CreateUser")
    public Response createUser() {
        Document newUser = new Document(DBManager.USER, "Dennis").append(DBManager.PASSWORD, "password");
        DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            System.out.println(doc);
        }

        return Response.status(Response.Status.OK).entity(newUser.toString()).build();
    }

    private boolean checkUsernameExists(String userName) {
        Iterator<Document> test = DBManager.DATABASE.getCollection(DBManager.USERS).find(Filters.eq(DBManager.USER, userName)).iterator();
        boolean exists = false;

        while (test.hasNext()) {
            Document result = test.next();
            String name = result.getString(DBManager.USER);
            exists = name.equals(userName);
        }

        return exists;
    } 

    private boolean validateUser(String userName, String password) {
        Iterator<Document> test = DBManager.DATABASE.getCollection(DBManager.USERS).find(Filters.eq(DBManager.USER, userName)).iterator();

        while (test.hasNext()) {
            Document result = test.next();
            String name = result.getString(DBManager.USER);
            String temppassword = result.getString(DBManager.PASSWORD);

            if (name.equals(userName) && temppassword.equals(password)) {
                return true;
            }
        }

        return false;
    }

}