package space.gavinklfong.photo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.gavinklfong.photo.dto.Photo;
import space.gavinklfong.photo.service.FileService;

import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class PhotoController {

    private final FileService fileService;

    @PostMapping("/albums/{album}/photos")
    public void uploadPhoto(@PathVariable String album, Photo photo, MultipartFile file) {
        if (!album.equals(photo.getAlbum())) {
            throw new IllegalArgumentException("Upload photo with inconsistent album name");
        }

        fileService.addPhoto(photo, file);
    }

    @DeleteMapping("/albums/{album}/photos/{filename}")
    public void deletePhoto(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = fileService.getPhotoInfo(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        fileService.deletePhoto(album, filename);
    }

    @GetMapping("/albums/{album}/photos/{filename}")
    public Photo getPhotoInfo(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = fileService.getPhotoInfo(album, filename);
        return photo.orElseThrow();
    }

    @GetMapping("/albums/{album}/photos/{filename}/download")
    public byte[] downloadPhoto(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = fileService.getPhotoInfo(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        return fileService.getPhotoContent(album, filename);
    }

    @GetMapping("/albums/{album}/photos/{filename}/download-url")
    public URL getPhotoDownloadUrl(@PathVariable String album, @PathVariable  String filename) {
        Optional<Photo> photo = fileService.getPhotoInfo(album, filename);
        if (photo.isEmpty()) throw new NoSuchElementException();

        return fileService.getDownloadUrl(album, filename);
    }
}
