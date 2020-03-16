package io.openliberty.guides.application;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@RequestScoped
@Path("")
public class UserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/User")
    public Response getResource() {
        // String user = "admin"; // the user name
        // String database = "admin"; // the name of the database in which the user is defined
        // char[] password = "admin".toCharArray(); // the password as a character array

        // MongoCredential credential = MongoCredential.createCredential(user, database, password);

        // MongoClientSettings settings = MongoClientSettings.builder()
        //     .credential(credential)
        //     .applyToSslSettings(builder -> builder.enabled(true))
        //     .applyToClusterSettings(builder -> 
        //         builder.hosts(Arrays.asList(new ServerAddress("localhost", 8081))))
        //     .build();
        
        // MongoClient mongoClient = MongoClients.create(settings);

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:8081");

        String test = mongoClient.listDatabases().first().toString();
        System.out.println(test);

        return Response.status(Response.Status.OK).entity("{ \"user\": 1, \"user2\": 2}").build();
    }

}