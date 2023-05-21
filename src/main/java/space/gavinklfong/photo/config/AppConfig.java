package space.gavinklfong.photo.config;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import space.gavinklfong.photo.dao.S3BucketBuilder;
import space.gavinklfong.photo.dao.S3ItemDao;

import java.net.URI;

import static java.util.Objects.nonNull;

@Configuration
public class AppConfig {

    @Bean
    public Region awsRegion(@Value("${aws.region}") String regionString) {
        return Region.of(regionString);
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(@Value("${aws.accessKeyId}") String accessKeyId,
                                                         @Value("${aws.secretAccessKey}") String secretAccessKey) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        );
    }

    @Bean
    public DynamoDbClient dynamoDbClient(Region region, AwsCredentialsProvider awsCredentialsProvider,
                                         @Value("${aws.endpointOverride}") String endpointOverride) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider);

        if (StringUtils.isNotBlank(endpointOverride)) {
            builder.endpointOverride(URI.create(endpointOverride));
        }

        return builder.build();
    }

    @Bean
    public S3Client s3Client(Region region, AwsCredentialsProvider awsCredentialsProvider,
                             @Value("${aws.endpointOverride}") String endpointOverride) {

        S3ClientBuilder builder = S3Client.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider);

        if (StringUtils.isNotBlank(endpointOverride)) {
            builder.endpointOverride(URI.create(endpointOverride))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(Region region, AwsCredentialsProvider awsCredentialsProvider,
                                   @Value("${aws.endpointOverride}") String endpointOverride) {

        S3Presigner.Builder builder = S3Presigner.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider);

        if (StringUtils.isNotBlank(endpointOverride)) {
            builder.endpointOverride(URI.create(endpointOverride))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }

        return builder.build();
    }

    @Bean
    public S3ItemDao s3ItemDao(S3Client s3Client, S3Presigner s3Presigner,
                               @Value("${aws.s3.bucketName}") String bucketName) {
        return new S3ItemDao(s3Client, s3Presigner, bucketName);
    }

    @Bean
    public S3BucketBuilder s3BucketBuilder(S3Client s3Client,
                                           @Value("${aws.s3.bucketName}") String bucketName) {
        return new S3BucketBuilder(s3Client, bucketName);
    }
}
