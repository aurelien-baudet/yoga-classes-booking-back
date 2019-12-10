package fr.yoga.booking.domain.reservation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Document
@AllArgsConstructor
public class Place {
	@Id
	private String id;
	private String name;
	private String address;
	private List<Image> maps;
	
	public Place() {
		this(null, null, null, new ArrayList<>());
	}
	
	public Place(String name, String address, List<Image> maps) {
		this(null, name, address, maps);
	}
	
	public boolean isSame(Place other) {
		if (other == null) {
			return false;
		}
		if (id == null) {
			return false;
		}
		return id.equals(other.getId());
	}

	public void addMap(Image image) {
		if (maps == null) {
			maps = new ArrayList<>();
		}
		if (maps.stream().anyMatch(i -> areIdenticalImages(image, i))) {
			return;
		}
		maps.add(image);
	}

	private boolean areIdenticalImages(Image image, Image other) {
		return other.getSize().equals(image.getSize()) && other.getType().equals(image.getType()) && other.getUrl().equals(image.getUrl());
	}
}
