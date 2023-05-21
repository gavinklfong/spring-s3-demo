package space.gavinklfong.photo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.gavinklfong.photo.dto.Photo;
import space.gavinklfong.photo.service.PhotoFileService;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class PhotoController {

    private final PhotoFileService photoFileService;

    @GetMapping("/albums/{album}")
    public List<Photo> getPhotos(@PathVariable String album) {
        return photoFileService.listAlbumPhotos(album);
    }

    @PostMapping("/albums/{album}/photos")
    public void uploadPhoto(@PathVariable String album, Photo photo, MultipartFile file) {
        if (!album.equals(photo.getAlbum())) {
            throw new IllegalArgumentException("Upload photo with inconsistent album name");
        }

        photoFileService.addPhoto(photo, file);
    }

    @DeleteMapping("/albums/{album}/photos/{filename}")
    public void deletePhoto(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = photoFileService.getPhotoInfoOrEmpty(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        photoFileService.deletePhoto(album, filename);
    }

    @GetMapping("/albums/{album}/photos/{filename}")
    public Photo getPhotoInfo(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = photoFileService.getPhotoInfoOrEmpty(album, filename);
        return photo.orElseThrow();
    }

    @GetMapping(value = "/albums/{album}/photos/{filename}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadPhoto(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = photoFileService.getPhotoInfoOrEmpty(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        return photoFileService.getPhotoContent(album, filename);
    }

    @GetMapping("/albums/{album}/photos/{filename}/download-url")
    public URL getPhotoDownloadUrl(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = photoFileService.getPhotoInfoOrEmpty(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        return photoFileService.getDownloadUrl(album, filename);
    }
}
