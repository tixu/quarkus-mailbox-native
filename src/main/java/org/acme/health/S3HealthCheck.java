package org.acme.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Health;
import io.agroal.api.AgroalDataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;


@Health
@ApplicationScoped
public class S3HealthCheck implements HealthCheck {

    @ConfigProperty(name = "s3.accesskey")
    String s3AccessKey;

    @ConfigProperty(name = "s3.secretkey")
    String s3SecretKey;

    @ConfigProperty(name = "s3.endpoint")
    String s3Endpoint;


    @Inject
    AgroalDataSource ds;

    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("S3 health check");

        try {
            AmazonS3 client =  getS3client();
            client.listBuckets();
            responseBuilder.up();
        } catch (Exception e) {
            // cannot access the database
            responseBuilder.down()
                    .withData("error", e.getMessage());
        }

        return responseBuilder.build();
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