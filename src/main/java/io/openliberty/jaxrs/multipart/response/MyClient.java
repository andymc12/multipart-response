package io.openliberty.jaxrs.multipart.response;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
        /*
        List<File> files = Arrays.asList(new File("/Users/andymc/dev/multipart-response/multipart-response/pom.xml"),
                                         new File("/Users/andymc/dev/multipart-response/multipart-response/readme.md"),
                                         new File("/Users/andymc/dev/multipart-response/multipart-response/stuff.xml"));
        
        for (int i=0; i<files.size(); i++) {
            attachments.add(
                AttachmentBuilder.newBuilder("file" + i)
                                 .contentType(files.get(i).getName().endsWith(".xml") ? APPLICATION_XML : TEXT_PLAIN)
                                 .inputStream(files.get(i).getName(), new FileInputStream(files.get(i)))
                                 .build());
        }
        */
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
}
