package fr.yoga.booking.domain.reservation;

import java.net.URL;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Place {
	@Id
	private String id;
	private String name;
	private String address;
	private URL plan;
	
	public Place(String name, String address, URL plan) {
		this(null, name, address, plan);
	}
}
