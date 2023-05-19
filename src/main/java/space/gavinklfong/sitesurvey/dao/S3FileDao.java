package space.gavinklfong.sitesurvey.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3FileDao {

    private final S3Client s3Client;

    public List<String> listObjects(String bucketName) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            return res.contents().stream()
                    .map(S3Object::key)
                    .toList();

        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    @SneakyThrows
    public void uploadItem(String bucketName, String itemName, MultipartFile file) {
        // First create a multipart upload and get the upload id
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(itemName)
                .build();

        try (InputStream fileInputStream = file.getInputStream()) {
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(fileInputStream, file.getSize()));
            log.info("file upload response {}", response.toString());
        } catch (IOException e) {
            log.error("file upload fail", e);
            throw e;
        }
    }
}
