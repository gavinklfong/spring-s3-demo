package space.gavinklfong.sitesurvey.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TicketReservation {
    String showId;
    String ticketId;
    String reference;
}
