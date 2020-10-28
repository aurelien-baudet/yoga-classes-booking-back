package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.yoga.booking.controller.dto.TeacherDto;
import fr.yoga.booking.domain.account.Teacher;

@Mapper
public abstract class TeacherMapper {
	public abstract TeacherDto toDto(Teacher teacher);

	public abstract List<TeacherDto> toDto(List<Teacher> teachers);
}
