package com.github.starnowski.quarkus.fun.liquid;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/template")
public class TemplateResource {

    @Inject
    private TemplateService templateService;

    @POST
    @Path("/{templateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTemplate(String templateId, String body) {
        return Response.ok(templateService.covert(templateId, body)).build();
    }
}
