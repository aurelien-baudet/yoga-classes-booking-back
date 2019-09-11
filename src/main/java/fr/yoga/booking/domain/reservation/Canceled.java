package fr.yoga.booking.domain.reservation;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("Canceled")
public class Canceled implements ClassState {
	private String message;
}
