package com.github.tncrazvan.quarkus.tools;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundManager implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        URL indexResource = getClass().getClassLoader().getResource("META-INF/resources/index.html");
        
        try {
            File indexFile = new File(indexResource.toURI());
            return Response.status(Status.OK).entity(indexFile).build();  
        } catch (URISyntaxException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(Arrays.toString(e.getStackTrace())).build();
        }
    }
}