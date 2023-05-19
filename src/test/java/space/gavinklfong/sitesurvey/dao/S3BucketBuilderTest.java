package space.gavinklfong.sitesurvey.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

class S3BucketBuilderTest {

    private static final String BUCKET_NAME = "test-bucket-ab034befaa6c";

    private S3BucketBuilder s3BucketBuilder;

    @BeforeEach
    void setup() {
        s3BucketBuilder = new S3BucketBuilder(S3Client.builder()
                .region(Region.US_EAST_2)
                .build());
    }
    @Test
    void testCreateBucket() {
        s3BucketBuilder.createBucket(BUCKET_NAME);
    }

    @Test
    void testDeleteBucket() {
        s3BucketBuilder.deleteBucket(BUCKET_NAME);
    }
}
