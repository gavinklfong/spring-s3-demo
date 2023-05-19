package space.gavinklfong.sitesurvey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import space.gavinklfong.sitesurvey.dto.Show;
import space.gavinklfong.sitesurvey.dto.Ticket;
import space.gavinklfong.sitesurvey.dto.TicketReservation;
import space.gavinklfong.sitesurvey.service.ShowTicketService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TicketController {

    private final ShowTicketService showTicketService;

    @GetMapping("/shows/{showId}")
    public Show retrieveShow(@PathVariable String showId) {
        return showTicketService.findTheatreShow(showId)
                .orElseThrow();
    }

    @GetMapping("/shows/{showId}/tickets")
    public List<Ticket> retrieveTickets(@PathVariable String showId) {
        return showTicketService.findTickets(showId);
    }

    @PostMapping("/shows/{showId}/tickets/{ticketId}/reserve")
    public TicketReservation reserveTicket(@PathVariable String showId, @PathVariable String ticketId) {
        return TicketReservation.builder()
                .reference(showTicketService.reserveTicket(showId, ticketId))
                .showId(showId)
                .ticketId(ticketId)
                .build();
    }
}
