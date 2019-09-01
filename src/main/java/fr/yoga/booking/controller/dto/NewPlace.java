package fr.yoga.booking.controller.dto;

import java.net.URL;

import lombok.Data;

@Data
public class NewPlace {
	private String name;
	private String address;
	private URL plan;
}
