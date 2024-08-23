package com.github.starnowski.quarkus.fun.liquid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Path("/")
public class TemplateResource {

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    @POST
    @Path("/send")
    @Produces(MediaType.TEXT_PLAIN)
    public Response send(String body) throws IOException {
        Client client = ClientBuilder.newClient();
        return Response.ok(client.target(url + "/test")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON)).readEntity(String.class)).build();
    }

    @POST
    @Path("/sendWrapped")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendWrapped(String request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = objectMapper.readValue(request, Map.class);
        Client client = ClientBuilder.newClient();
        byte[] requestBody = Base64.getDecoder().decode((String) body.get("data"));
        return Response.ok(client.target(url + "/test")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON)).readEntity(String.class)).build();
    }

}
