package com.github.starnowski.quarkus.fun.liquid;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/")
public class TemplateResource {

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    @POST
    @Path("/send")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTemplate(String body) throws IOException {
        Client client = ClientBuilder.newClient();
        return Response.ok(client.target(url + "/test")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON)).readEntity(String.class)).build();
    }

}
