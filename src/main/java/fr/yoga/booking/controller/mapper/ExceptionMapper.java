package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.yoga.booking.controller.dto.ErrorDto;

@Mapper
public abstract class ExceptionMapper {
	public ErrorDto toDto(Throwable exception) {
		if (exception == null) {
			return null;
		}
		return new ErrorDto(exception.getClass().getSimpleName(), exception);
	}

	public abstract List<ErrorDto> toDto(List<Exception> exception);
}
