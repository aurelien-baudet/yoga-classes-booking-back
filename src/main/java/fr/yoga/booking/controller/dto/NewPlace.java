package fr.yoga.booking.controller.dto;

import java.util.List;

import fr.yoga.booking.domain.reservation.Image;
import lombok.Data;

@Data
public class NewPlace {
	private String name;
	private String address;
	private List<Image> maps;
}
