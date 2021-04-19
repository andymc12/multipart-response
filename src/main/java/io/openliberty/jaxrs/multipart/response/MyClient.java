package io.openliberty.jaxrs.multipart.response;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.jaxrs20.multipart.AttachmentBuilder;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;

@Path("/client")
@Produces(MediaType.MULTIPART_FORM_DATA)
public class MyClient {

    @GET
    public List<IAttachment> sendAndGetAttachments() throws FileNotFoundException {
        List<IAttachment> attachments = new ArrayList<>();

        File personXml = new File("/Users/andymc/dev/multipart-response/multipart-response/person.xml");
        attachments.add(AttachmentBuilder.newBuilder("person.xml")
                                         .inputStream("person.xml", new FileInputStream(personXml))
                                         .contentType(MediaType.APPLICATION_XML_TYPE)
                                         .build());
        File personJson = new File("/Users/andymc/dev/multipart-response/multipart-response/person.json");
        attachments.add(AttachmentBuilder.newBuilder("person.json")
                                         .inputStream("person.json", new FileInputStream(personJson))
                                         .contentType(MediaType.APPLICATION_JSON_TYPE)
                                         .build());
        attachments.add(AttachmentBuilder.newBuilder("greeting")
                                         .inputStream(new ByteArrayInputStream("Hi, I'm John Doe".getBytes()))
                                         .build());

        Client c = ClientBuilder.newClient();
        WebTarget target = c.target("http://localhost:9079/data/multipart/introduction");
        Response r = target.request(MediaType.TEXT_PLAIN)
                           .header("Content-Type", "multipart/form-data")
                           .post(Entity.entity(attachments, MediaType.MULTIPART_FORM_DATA_TYPE));
        if (r.getStatus() == 200 && r.hasEntity()) {
            return r.readEntity(new GenericType<List<IAttachment>>(){});
        }
        throw new WebApplicationException("status: " + r.getStatus());
    }

    @GET
    @Path("/sendMultiPart")
    @Produces(MediaType.TEXT_PLAIN)
    public String acceptAndSendOnMultiPartData(@QueryParam("name") String name, @QueryParam("file") String fileName) {

        List<IAttachment> parts = new ArrayList<>();
        parts.add(AttachmentBuilder.newBuilder("name").inputStream(new ByteArrayInputStream(name.getBytes())).build());
        try {
            parts.add(AttachmentBuilder.newBuilder("file").inputStream(fileName, new FileInputStream(fileName)).build());
        } catch (FileNotFoundException e) {
            throw new BadRequestException("Unable to find specified file: " + fileName, e);
        }
        Client c = ClientBuilder.newClient();
        WebTarget target = c.target("http://localhost:9080/data/multipart/getContextLength");
        Response r = target.request(MediaType.TEXT_PLAIN)
                           .header("Content-Type", "multipart/form-data")
                           .post(Entity.entity(parts, MediaType.MULTIPART_FORM_DATA_TYPE));
        StringBuilder sb = new StringBuilder(r.readEntity(String.class));


        return sb.toString();
    }
}
