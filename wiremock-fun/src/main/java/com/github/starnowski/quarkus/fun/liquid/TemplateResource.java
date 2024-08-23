package com.github.starnowski.quarkus.fun.liquid;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/")
public class TemplateResource {

    @Inject
    private TemplateService templateService;

    @POST
    @Path("/template/{templateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTemplate(String templateId, String body) throws IOException {
        return Response.ok(templateService.covert(templateId, body)).build();
    }

    @POST
    @Path("/template-with-attributes/{templateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTemplateWithAttributes(String templateId, String body) throws IOException {
        return Response.ok(templateService.covertWithAttributes(templateId, body)).build();
    }

    @POST
    @Path("/template-with-custom-filters/{templateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateTemplateWithCustomFilters(String templateId, String body) throws IOException {
        return Response.ok(templateService.covertWithCustomFilters(templateId, body)).build();
    }
}
