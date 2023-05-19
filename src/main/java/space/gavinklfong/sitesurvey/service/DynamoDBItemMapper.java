package space.gavinklfong.sitesurvey.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import space.gavinklfong.sitesurvey.dto.Show;
import space.gavinklfong.sitesurvey.dto.Ticket;
import space.gavinklfong.sitesurvey.model.ShowItem;
import space.gavinklfong.sitesurvey.model.TicketItem;

@Mapper
public interface DynamoDBItemMapper {

    DynamoDBItemMapper INSTANCE = Mappers.getMapper(DynamoDBItemMapper.class);

    Show mapFromItem(ShowItem item);

    @Mapping(target = "id", source = "sortKey")
    @Mapping(target = "reference", source = "ticketRef")
    Ticket mapFromItem(TicketItem item);
}
