package space.gavinklfong.photo.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Location {
    double longitude;
    double latitude;
}
