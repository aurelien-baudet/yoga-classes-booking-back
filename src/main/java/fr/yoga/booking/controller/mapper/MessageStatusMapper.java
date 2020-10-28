package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.yoga.booking.controller.dto.MessageStatusDto;
import fr.yoga.booking.domain.notification.SendReport.MessageStatus;

@Mapper(uses = {ExceptionMapper.class})
public abstract class MessageStatusMapper {
	public abstract MessageStatusDto toDto(MessageStatus report);

	public abstract List<MessageStatusDto> toDto(List<MessageStatus> reports);
}
