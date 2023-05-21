package space.gavinklfong.photo.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class S3ItemDao {

    private static final String METADATA_FILENAME = "filename";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public List<String> listItems() {
        return listItems("");
    }

    public List<String> listItems(String prefix) {
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsResponse res = s3Client.listObjects(listObjects);
        return res.contents().stream()
                .map(S3Object::key)
                .toList();
    }

    private Map<String, String> retrieveMetadata(String itemKey) {
        HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(itemKey)
                        .build());

        return response.metadata();
    }

    private Map<String, String> retrieveTags(String itemKey) {
        GetObjectTaggingResponse response = s3Client.getObjectTagging(GetObjectTaggingRequest.builder()
                        .bucket(bucketName)
                        .key(itemKey)
                        .build());

        return response.tagSet().stream().collect(toMap(Tag::key, Tag::value));
    }

    public URL generatePresignedUrl(String itemKey) {

        Map<String, String> metadata = retrieveMetadata(itemKey);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .getObjectRequest(getObjectRequest -> getObjectRequest
                        .bucket(bucketName)
                        .key(itemKey)
                        .responseContentDisposition(String.format("attachment;filename=%s", metadata.get(METADATA_FILENAME)))
                )
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url();
    }

    @SneakyThrows
    public void uploadItem(String itemKey, String filename, Map<String, String> metadata, MultipartFile file) {

        if (metadata.containsKey(METADATA_FILENAME)) {
            throw new IllegalArgumentException("Metadata should not have filename");
        }

        Map<String, String> metadataWithFilename = new HashMap<>(metadata);
        metadataWithFilename.put(METADATA_FILENAME, filename);

        // First create a multipart upload and get the upload id
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(itemKey)
                .metadata(metadataWithFilename)
                .build();

        try (InputStream fileInputStream = file.getInputStream()) {
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(fileInputStream, file.getSize()));
            log.info("file upload response {}", response.toString());
        } catch (IOException e) {
            log.error("file upload fail", e);
            throw e;
        }
    }

    public byte[] downloadItemData(String itemKey) {
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(itemKey)
                .bucket(bucketName)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
        return objectBytes.asByteArray();
    }
}
