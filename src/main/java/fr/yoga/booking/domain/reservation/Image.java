package fr.yoga.booking.domain.reservation;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
	private URL url;
	private String size;
	private String type;
}
