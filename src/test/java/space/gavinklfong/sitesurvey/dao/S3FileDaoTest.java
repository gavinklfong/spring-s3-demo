package space.gavinklfong.sitesurvey.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class S3FileDaoTest {

    private static final Region REGION = Region.US_EAST_2;
    private static final String BUCKET_NAME = "test-bucket-ab034befaa6c";
    private static final String FILE_1 = "test_1.pdf";
    private static final String FILE_2 = "test_2.pdf";
    private static final String FILE_1_KEY = "case-1/test_1.pdf";
    private static final String FILE_2_KEY = "case-2/test_2.pdf";

    private final S3FileDao s3FileDao = new S3FileDao(S3Client.builder()
            .region(REGION)
            .build(),
            S3Presigner.builder()
                    .region(REGION)
                    .build());

    private final S3BucketBuilder s3BucketBuilder = new S3BucketBuilder(S3Client.builder()
            .region(REGION)
            .build());

    @BeforeEach
    void setup() {
        s3BucketBuilder.reCreateBucket(BUCKET_NAME);
    }

    @Test
    void uploadFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(FILE_1,
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_1)));

        s3FileDao.uploadItem(BUCKET_NAME, FILE_1_KEY, FILE_1, generateMetadata(), multipartFile);

        List<String> files = s3FileDao.listItems(BUCKET_NAME);
        assertThat(files).containsOnly(FILE_1_KEY);
    }

    @Test
    void uploadMultipleFiles() throws IOException {
        MultipartFile multipartFile1 = new MockMultipartFile(FILE_1,
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_1)));
        s3FileDao.uploadItem(BUCKET_NAME, FILE_1_KEY, FILE_1, generateMetadata(), multipartFile1);

        MultipartFile multipartFile2 = new MockMultipartFile(FILE_2,
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_2)));
        s3FileDao.uploadItem(BUCKET_NAME, FILE_2_KEY, FILE_2, generateMetadata(), multipartFile2);

        List<String> files = s3FileDao.listItems(BUCKET_NAME);
        assertThat(files).containsExactlyInAnyOrder(FILE_1_KEY, FILE_2_KEY);
    }

    @Test
    void listFileWithPrefix() throws IOException {
        MultipartFile multipartFile1 = new MockMultipartFile(FILE_1,
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_1)));
        s3FileDao.uploadItem(BUCKET_NAME, FILE_1_KEY, FILE_1, generateMetadata(), multipartFile1);

        MultipartFile multipartFile2 = new MockMultipartFile(FILE_2,
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_2)));
        s3FileDao.uploadItem(BUCKET_NAME, FILE_2_KEY, FILE_2, generateMetadata(), multipartFile2);

        List<String> files = s3FileDao.listItems(BUCKET_NAME, "case-2");
        assertThat(files).containsOnly(FILE_2_KEY);
    }

    @Test
    void downloadItemData() throws IOException {
        byte[] uploadFileContent = IOUtils.toByteArray(Objects.requireNonNull(
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_1))));

        MultipartFile multipartFile = new MockMultipartFile(FILE_1, uploadFileContent);

        s3FileDao.uploadItem(BUCKET_NAME, FILE_1_KEY, FILE_1, generateMetadata(), multipartFile);

        byte[] fileContent = s3FileDao.downloadItemData(BUCKET_NAME, FILE_1_KEY);
        assertThat(fileContent).isEqualTo(uploadFileContent);
    }

    @Test
    void getPresignedUrl() throws IOException {
        byte[] uploadFileContent = IOUtils.toByteArray(Objects.requireNonNull(
                S3FileDaoTest.class.getResourceAsStream(String.format("/%s", FILE_1))));

        MultipartFile multipartFile = new MockMultipartFile(FILE_1, uploadFileContent);
        s3FileDao.uploadItem(BUCKET_NAME, FILE_1_KEY, FILE_1, generateMetadata(), multipartFile);

        URL presignedUrl = s3FileDao.generatePresignedUrl(BUCKET_NAME, FILE_1_KEY);
        log.info("url = {}", presignedUrl);
    }

    private Map<String, String> generateMetadata() {
        return Map.of("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
    }

}
