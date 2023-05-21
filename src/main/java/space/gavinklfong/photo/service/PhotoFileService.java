package space.gavinklfong.photo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import space.gavinklfong.photo.dao.S3ItemDao;
import space.gavinklfong.photo.dto.Location;
import space.gavinklfong.photo.dto.Photo;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URL;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class PhotoFileService {

    private static final String METADATA_TIMESTAMP = "timestamp";
    private static final String METADATA_LOCATION_LONGITUDE = "location-longitude";
    private static final String METADATA_LOCATION_LATITUDE = "location-latitude";

    private final S3ItemDao s3ItemDao;

    public void addPhoto(Photo photo, MultipartFile file) {
        String itemKey = generateItemKey(photo.getAlbum(), photo.getFilename());
        Map<String, String> metadata = new HashMap<>();
        if (nonNull(photo.getTimestamp())) {
            metadata.put(METADATA_TIMESTAMP, DateTimeFormatter.ISO_INSTANT.format(photo.getTimestamp()));
        }
        if (nonNull(photo.getLocation())) {
            metadata.put(METADATA_LOCATION_LONGITUDE, String.valueOf(photo.getLocation().getLongitude()));
            metadata.put(METADATA_LOCATION_LATITUDE, String.valueOf(photo.getLocation().getLatitude()));
        }
        s3ItemDao.uploadItem(itemKey, metadata, file);
    }

    public List<Photo> listAlbumPhotos(String album) {
        List<String> filenames = s3ItemDao.listItems(album);

        return filenames.stream()
                .map(filename -> getPhotoInfo(album, filename))
                .toList();
    }

    private Photo getPhotoInfo(String album, String filename) {
        String itemKey = generateItemKey(album, filename);
        Map<String, String> metadata = s3ItemDao.retrieveMetadata(itemKey);
        Photo.PhotoBuilder builder = Photo.builder()
                .album(album)
                .filename(filename);

        if (metadata.containsKey(METADATA_TIMESTAMP)) {
            builder.timestamp(Instant.parse(metadata.get(METADATA_TIMESTAMP)));
        }

        if (metadata.containsKey(METADATA_LOCATION_LONGITUDE)) {
            builder.location(Location.builder()
                    .latitude(Double.parseDouble(metadata.get(METADATA_LOCATION_LATITUDE)))
                    .longitude(Double.parseDouble(metadata.get(METADATA_LOCATION_LONGITUDE)))
                    .build());
        }

        return builder.build();
    }

    public Optional<Photo> getPhotoInfoOrEmpty(String album, String filename) {
        String itemKey = generateItemKey(album, filename);
        if (!s3ItemDao.listItems(album).contains(itemKey)) {
            return Optional.empty();
        }
        return Optional.of(getPhotoInfo(album, filename));
    }

    public byte[] getPhotoContent(String album, String filename) {
        return s3ItemDao.downloadItemData(generateItemKey(album, filename));
    }

    public void deletePhoto(String album, String filename) {
        s3ItemDao.deleteItem(generateItemKey(album, filename));
    }

    public URL getDownloadUrl(String album, String filename) {
        return s3ItemDao.generatePresignedUrl(generateItemKey(album, filename));
    }

    private String generateItemKey(String album, String filename) {
        return String.format("%s/%s", album, filename);
    }
}
