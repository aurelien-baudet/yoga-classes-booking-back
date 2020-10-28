package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.yoga.booking.controller.dto.SendReportDto;
import fr.yoga.booking.domain.notification.SendReport;

@Mapper(uses = {MessageStatusMapper.class})
public abstract class SendReportMapper {
	public abstract SendReportDto toDto(SendReport report);

	public abstract List<SendReportDto> toDto(List<SendReport> reports);
}
