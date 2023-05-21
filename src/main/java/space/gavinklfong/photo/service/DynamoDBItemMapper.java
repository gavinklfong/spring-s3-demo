package space.gavinklfong.photo.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import space.gavinklfong.photo.dto.Show;
import space.gavinklfong.photo.dto.Ticket;
import space.gavinklfong.photo.model.ShowItem;
import space.gavinklfong.photo.model.TicketItem;

@Mapper
public interface DynamoDBItemMapper {

    DynamoDBItemMapper INSTANCE = Mappers.getMapper(DynamoDBItemMapper.class);

    Show mapFromItem(ShowItem item);

    @Mapping(target = "id", source = "sortKey")
    @Mapping(target = "reference", source = "ticketRef")
    Ticket mapFromItem(TicketItem item);
}
