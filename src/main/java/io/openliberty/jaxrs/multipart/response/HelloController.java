package io.openliberty.jaxrs.multipart.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonString;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ibm.websphere.jaxrs20.multipart.AttachmentBuilder;
import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;

/**
 *
 */
@Path("/multipart")
@ApplicationScoped
@Produces(MediaType.MULTIPART_FORM_DATA)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class HelloController {

    // Invoked with:
    // curl -v -F key1=value1 -F upload=@readme.md http://localhost:9080/data/multipart
    @POST
    public IMultipartBody returnExistingMultipartBody(IMultipartBody body) {
        body.getAllAttachments().stream().forEach(this::printAttachment);
        return body;
    }

    // Invoked with: 
    // curl -v -F key1=value1 -F upload=@readme.md http://localhost:9080/data/multipart/list
    @POST
    @Path("/list")
    public List<IAttachment> returnExistingAttachmentList(List<IAttachment> list) {
        //System.out.println(list);
        list.stream().forEach(this::printAttachment);
        return new ArrayList<>(list);
    }

    // Invoked with:
    // curl http://localhost:9080/data/multipart/list
    @GET
    @Path("/list")
    public List<IAttachment> returnNewAttachmentList() throws FileNotFoundException {
        List<IAttachment> list = new ArrayList<>();

        AttachmentBuilder builder = AttachmentBuilder.newBuilder("readme");
        builder.inputStream("readme.md", new FileInputStream(new File("/Users/andymc/dev/multipart-response/multipart-response/readme.md")));
        list.add(builder.build());
        return list;
    }

    // Invoked with:
    // curl -v -F key1=value1 -F upload=@readme.md
    // http://localhost:9080/data/multipart/list
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/introduction")
    public String introduction(List<IAttachment> list) throws IOException {
        Person person = null;
        for (IAttachment att : list) {
            if (MediaType.APPLICATION_JSON_TYPE.isCompatible(att.getContentType())) {
                Jsonb jsonb = JsonbBuilder.create();
                person = jsonb.fromJson(att.getDataHandler().getInputStream(), Person.class);
            }
        }

        return "Hello " + person;
    }

    @GET
    @Path("/listWithBoundary")
    @Produces("multipart/form-data; boundary=\"thisIsMyBoundary\"")
    public List<IAttachment> returnNewAttachmentListWithBoundary() throws FileNotFoundException {
        return returnNewAttachmentList();
    }

    void printAttachment(IAttachment attachment) {
        System.out.println("Attachment: " + attachment.getContentId());
        System.out.println("  MediaType: " + attachment.getContentType());
        System.out.println("  Headers:");
        for (Map.Entry<String,List<String>> entry : attachment.getHeaders().entrySet()) {
            System.out.println("    " + entry.getKey() + " : " + entry.getValue());
        }
        DataHandler dh = attachment.getDataHandler();
        System.out.println("  DataHandler:");
        String contentType = dh.getContentType();
        System.out.println("    ContentType: " + contentType);
        System.out.println("    Name: " + dh.getName());
        if (contentType != null) {
            System.out.println("    AllCommands: " + dh.getAllCommands());
        }
        try {
            if (contentType != null) {
                System.out.println("    Content: " + dh.getContent());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        DataSource ds = dh.getDataSource();
        System.out.println("    DataSource: " + ds);
        System.out.println("    DataSource.ContentType: " + ds.getContentType());
        System.out.println("    DataSource.Name: " + ds.getName());
        try {
            System.out.println("    DataSource.InputStream: " + ds.getInputStream());
            System.out.println("    DataSource.OutputStream: " + ds.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (contentType != null) {
            System.out.println("    PreferredCommands: " + dh.getPreferredCommands());
            System.out.println("    TransferDataFlavors: " + dh.getTransferDataFlavors());
        }
        try {
            System.out.println("    InputStream: " + dh.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if (contentType != null) {
                System.out.println("    OutputStream: " + dh.getOutputStream());
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @GET
    @Path("/abc")
    @Produces("text/plain;qs=0.5")
    public String textResponse() {
        return "textString";
    }

    @GET
    @Path("/abc")
    @Produces("application/json;qs=1")
    public JsonString jsonResponse() {
        return Json.createValue("jsonString");
    }


}
