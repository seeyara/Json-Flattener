package io.jsonflattener.controller;

import io.jsonflattener.service.FlattenerService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/json")
public class JsonFlattenerController {
    private FlattenerService flattenerService;

    public JsonFlattenerController(FlattenerService service) {
        flattenerService = service;
    }

    @PUT
    @Path("/flatten")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response flattenJson(String input) throws Exception {
        return Response.ok(flattenerService.flattenJson(input)).build();
    }

    @GET
    public String flattenJson() throws Exception {
        return "UP";
    }
}
