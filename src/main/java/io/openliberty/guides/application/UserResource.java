package io.openliberty.guides.application;

import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.model.Filters;

import org.bson.Document;

import io.openliberty.guides.application.models.User;
import io.openliberty.guides.application.util.DBManager;

@RequestScoped
@Path("")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/User")
    public Response getUser(User user) {
        System.out.println("Retrieving User Data ... " + UserResource.class.getSimpleName() + " [31]");
        Document test = findUser(user.getUserName());

        System.out.println("Finished retrieving User Data ... " + UserResource.class.getSimpleName() + " [34]");

        return Response.status(Response.Status.OK).entity("{ \"user\": \"" + test.getString(DBManager.USER) + "\" , \"user2\": 2}").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/CreateUser")
    public Response createUser(User user) {
        System.out.println("Creating User Data ... for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [44]");
        
        Document newUser = new Document(DBManager.USER, user.getUserName()).append(DBManager.PASSWORD, user.getPassword());

        if (!checkUsernameExists(user.getUserName())) {
            DBManager.DATABASE.getCollection(DBManager.USERS).insertOne(newUser);
        } else {
            System.out.println("User Data already exists for User: " + user.getUserName() + " " + UserResource.class.getSimpleName() + " [51]");
        }
        
        for (Document doc:DBManager.DATABASE.getCollection(DBManager.USERS).find()) {
            System.out.println(doc);
        }
        System.out.println("Finished creating User Data ... " + UserResource.class.getSimpleName() + " [55]");
        return Response.status(Response.Status.OK).entity(newUser.toString()).build();
    }

    private boolean checkUsernameExists(String userName) {
        Document result = findUser(userName);
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