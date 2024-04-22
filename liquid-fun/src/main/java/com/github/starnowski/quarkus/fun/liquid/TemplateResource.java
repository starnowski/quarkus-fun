package com.github.starnowski.quarkus.fun.liquid;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/template")
public class TemplateResource {

    @Inject
    private TemplateService templateService;

    @POST
    @Path("/{templateId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String generateTemplate(String templateId, String body) {
        return templateService.covert(templateId, body);
    }
}
