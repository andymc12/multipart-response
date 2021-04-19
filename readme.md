# Demo of new AttachmentBuilder API in Open Liberty

Open Liberty now enables users to send `multipart/form-data` payloads via the JAX-RS APIs. To create the payload, a user
will need to build and send a `List` of `IAttachment` objects.  The `IAttachment` objects can be built using the new
`AttachmentBuilder` API.  Here is an example:
```
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
```

The `MyClient` class has an example that you can use by sending a name and file name to.  That client class will then
create a `multipart/form-data` request using (1) the name and (2) the actual file as the parts - and it will then send
it to a server resource (conviently located in the same server!) that will process each part and return that part's
size in bytes.

You can run this sample app by cloning this repo, entering the `multipart-response` directory and issuing the following
command: 
`mvn clean package liberty:run`
Next, browse to: http://localhost:9080/ and enter a name and the fully-qualified path of a file on your file system, and
then click the submit button.











