package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.yoga.booking.controller.dto.LessonDto;
import fr.yoga.booking.domain.reservation.Lesson;

@Mapper(uses = {TeacherMapper.class})
public abstract class LessonMapper {
	@Mapping(target = "title", source = "info.title")
	@Mapping(target = "description", source = "info.description")
	@Mapping(target = "maxStudents", source = "info.maxStudents")
	@Mapping(target = "photos", source = "info.photos")
	@Mapping(target = "difficulty", source = "info.difficulty")
	@Mapping(target = "subscriptionPack", source = "info.subscriptionPack")
	public abstract LessonDto toDto(Lesson lesson);

	public abstract List<LessonDto> toDto(List<Lesson> lessons);
}
