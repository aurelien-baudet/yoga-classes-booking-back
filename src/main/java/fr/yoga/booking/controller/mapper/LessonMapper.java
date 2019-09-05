package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.yoga.booking.controller.dto.LessonDto;
import fr.yoga.booking.domain.reservation.Lesson;

@Mapper
public abstract class LessonMapper {
	@Mapping(target = "title", source = "info.title")
	@Mapping(target = "description", source = "info.description")
	@Mapping(target = "maxStudents", source = "info.maxStudents")
	@Mapping(target = "photos", source = "info.photos")
	public abstract LessonDto toDto(Lesson lesson);

	public abstract List<LessonDto> toDto(List<Lesson> lessons);
}
