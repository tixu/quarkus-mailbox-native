package org.acme.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 
@Path("/mailboxes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class MailBoxResource {
     
    private Log log = LogFactory.getLog(MailBoxResource.class);


    @Inject
    @Stream("my-amqp-output")
    Emitter<String> emitter;

    @ConfigProperty(name = "s3.accesskey")
    String s3AccessKey;

    @ConfigProperty(name = "s3.secretkey")
    String s3SecretKey;

    @ConfigProperty(name = "s3.endpoint")
    String s3Endpoint;

    private static final Jsonb JSON = JsonbBuilder.create();

    public MailBoxResource() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(MailBox.listAll()).build();
    }

    @POST
    @Path("/{accessKey}/publications")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("accessKey") String accessKey, MultipartFormDataInput input)
            throws IOException {
        log.info(accessKey);
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

     
        for (Entry<String,List<InputPart>> entry : uploadForm.entrySet()) {
            String key =  entry.getKey();
            List<InputPart> inputParts  =entry.getValue();
            for (InputPart inputPart : inputParts) {
                try {
    
                    MultivaluedMap<String, String> header = inputPart.getHeaders();
                    // convert the uploaded file to inputstream
                    InputStream inputStream = inputPart.getBody(InputStream.class, null);
                    AmazonS3 client = getS3client();
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.addUserMetadata("key", key);
                    String id = UUID.randomUUID().toString();
                    PutObjectResult rs =  client.putObject(accessKey+"/in", UUID.randomUUID().toString(), inputStream, meta);
                    
                    Content c = new Content();
                    c.setId(id);
                    c.setName(key);
                    c.setState("in");
                    c.setAccessKey(accessKey);
                    String payload = JSON.toJson(c);
                    emitter.send(payload);
                } catch (com.amazonaws.services.s3.model.AmazonS3Exception e) {
                    log.fatal(e);
                    return Response.serverError().entity("S3 error").build();
                }
                 catch (Exception e) {
                    log.fatal(e);
                    return Response.status(500).build();
                }
            }
        }

        
        return Response.status(200).build();
    }

    @POST
    @Transactional
    public Response create(@Valid MailBox item) {
        item.persist();
        AmazonS3 s3Client = getS3client();
        s3Client.createBucket(item.getAccessKey());
        return Response.status(Status.CREATED).entity(item).build();
    }

    @GET
    @Path("/{accessKey}")
    @Counted(name = "getMailBox", monotonic = true, description = "How many primality time boxe where get")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
    public Response getOne(@PathParam("accessKey") String accessKey) {
        MailBox item = MailBox.findByAccessKey(accessKey);
        if (item == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(item).build();
    }

    @GET
    @Path("/{accessKey}/folders")
    public Response getFoldersContent(@PathParam("accessKey") String accessKey) {
        List<Content> folders = new ArrayList<Content>();
        try {

            AmazonS3 s3Client = getS3client();
            ObjectListing list = s3Client.listObjects(accessKey, "");
            List<S3ObjectSummary> buckets = list.getObjectSummaries();
            for (S3ObjectSummary b : buckets) {
                Content content = new Content();
                content.setId(b.getKey());
                content.setName(s3Client.getObjectMetadata(accessKey, b.getKey()).getUserMetaDataOf("key"));
                folders.add(content);
            }
            return Response.ok(folders).build();

        } catch (Exception e) {
            log.fatal("Error occurred: " + e);
            return Response.serverError().build();
        }

    }


    @GET
    @Path("/{accessKey}/folders/{folder}")
    public Response getFoldersContent(@PathParam("accessKey") String accessKey,@PathParam("folder")String folder) {
        List<Content> folders = new ArrayList<Content>();
        try {

            AmazonS3 s3Client = getS3client();
            // Check if the bucket already exists.
            ListObjectsRequest req = new ListObjectsRequest();

            ObjectListing list = s3Client.listObjects(req.withBucketName(accessKey).withPrefix(folder).withMarker("in/"));
            List<S3ObjectSummary> buckets = list.getObjectSummaries();
            for (S3ObjectSummary b : buckets) {
                Content content = new Content();
                int prefixLength = (folder+"/").length();
                content.setId(b.getKey().substring(prefixLength));
                content.setName(s3Client.getObjectMetadata(accessKey, b.getKey()).getUserMetaDataOf("key"));
                folders.add(content);
            }
            return Response.ok(folders).build();

        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
            return Response.serverError().build();
        }

    }

    

    private AmazonS3 getS3client() {
        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(s3Endpoint, Regions.US_EAST_1.name()))
                .withPathStyleAccessEnabled(true).withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        return s3Client;
    }
}
