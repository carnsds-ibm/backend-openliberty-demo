package io.openliberty.guides.application;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/User")
public class UserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResource() {
        return Response.status(Response.Status.OK).entity("{ \"user\": 1, \"user2\": 2}").build();
    }

}