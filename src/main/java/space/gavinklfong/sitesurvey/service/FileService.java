package space.gavinklfong.sitesurvey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Component
public class FileService {

    private final S3Client s3Client;


}
