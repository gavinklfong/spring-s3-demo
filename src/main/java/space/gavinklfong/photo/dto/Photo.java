package space.gavinklfong.photo.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Builder
@Value
public class Photo {
    String album;
    String filename;
    Instant timestamp;
    Location location;
}
