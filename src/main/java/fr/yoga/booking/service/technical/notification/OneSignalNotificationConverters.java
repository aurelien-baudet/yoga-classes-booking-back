package fr.yoga.booking.service.technical.notification;

import static fr.yoga.booking.util.ExpressionParser.evaluate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.currencyfair.onesignal.model.notification.Button;

import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.reservation.Place;

public class OneSignalNotificationConverters {
	
	public static OneSignalNotificationConverter placeChangedConverter() {
		return (notification, builder) -> {
			try {
				Place newPlace = ((PlaceChangedNotification) notification).getNewPlace();
				return builder
					.withHeading("en", evaluate("🔀 Changement de lieu - ${newPlace.name}", notification))
					.withContent("en", evaluate("📆 ${T(fr.yoga.booking.util.DateRangeUtil).format(scheduledClass)}\n"
							+ "📍 ${newPlace.name}\n"
							+ "\n"
							+ "Le cours du ${T(fr.yoga.booking.util.DateRangeUtil).format(scheduledClass)} aura lieu à ${newPlace.name}\n"
							+ "\n"
							+ "${newPlace.address}", notification))
					.withLargeIcon(newPlace.getMaps()
							.stream()
							.filter(m -> "STATIC_MAP".equals(m.getType()))
							.filter(m -> "SMALL".equals(m.getSize()))
							.map(m -> m.getUrl().toString())
							.findFirst()
							.orElse(null))
					.withButton(new Button("show-directions", "Itinéraire", "https://flyfresno.com/wp-content/uploads/2016/12/directions-icon.png", "https://www.google.com/maps/dir/?api=1&destination="+URLEncoder.encode(newPlace.getAddress(), "UTF-8")))
//					.withUrl("")
					.withDataElement("classId", ((PlaceChangedNotification) notification).getScheduledClass().getId())
					.withDataElement("newPlaceId", newPlace.getId())
					.build();
			} catch(UnsupportedEncodingException e) {
				throw new IllegalStateException("Failed to convert push notification", e);
			}
		};
	}
	
	public static OneSignalNotificationConverter classCanceledConverter() {
		return (notification, builder) -> {
			return builder
				.withHeading("en", evaluate("❌ Cours annulé", notification))
				.withContent("en", evaluate("📆 ${T(fr.yoga.booking.util.DateRangeUtil).format(canceledClass)}\n"
						+ "\n"
						+ "Le cours du ${T(fr.yoga.booking.util.DateRangeUtil).format(canceledClass)} est annulé\n"
						+ "\n"
						+ "Message de ${canceledClass.lesson.teacher.displayName} :\n"
						+ "${additionalInfo.message}", notification))
				.withButton(new Button("remove-from-calendar", "Retirer du calendrier", "http://simpleicon.com/wp-content/uploads/Calendar-Remove.png", null))
				.withDataElement("classId", ((ClassCanceledNotification) notification).getCanceledClass().getId())
				.build();
		};
	}
	
	public static OneSignalNotificationConverter freePlaceAutomaticallyBookedConverter() {
		return (notification, builder) -> {
			return builder
				.withHeading("en", evaluate("🙏 Place libre", notification))
				.withContent("en", evaluate("📆 ${T(fr.yoga.booking.util.DateRangeUtil).format(bookedClass)}\n"
						+ "\n"
						+ "Une place vient de se libérer pour le cours du ${T(fr.yoga.booking.util.DateRangeUtil).format(bookedClass)}.\n"
						+ "\n"
						+ "Etant permier sur liste d'attente, tu as été automatiquement inscrit.\n"
						+ "Si tu ne peux pas être présent, pense à te désinscrire pour laisser la place au suivant dans la liste d'attente.", notification))
				.withButton(new Button("confirm-presence", "Confirmer présence", null, null))
				.withButton(new Button("unbook", "Pas disponible", null, null))
				.withDataElement("classId", ((FreePlaceBookedNotification) notification).getBookedClass().getId())
				.build();
		};
	}
	
	public static OneSignalNotificationConverter availablePlaceConverter() {
		return (notification, builder) -> {
			return builder
				.withHeading("en", evaluate("🙏 Place libre", notification))
				.withContent("en", evaluate("📆 ${T(fr.yoga.booking.util.DateRangeUtil).format(bookedClass)}\n"
						+ "📍 ${bookedClass.lesson.place.name}\n"
						+ "\n"
						+ "Une place est libre pour le cours du ${T(fr.yoga.booking.util.DateRangeUtil).format(bookedClass)}.\n"
						+ "\n"
						+ "Merci de confirmer ta présence pour prendre la place.", notification))
				.withButton(new Button("confirm-presence", "Confirmer présence", null, null))
				.withButton(new Button("unbook", "Pas disponible", null, null))
				.withDataElement("classId", ((AvailablePlaceNotification) notification).getBookedClass().getId())
				.build();
		};
	}
	
	public static OneSignalNotificationConverter reminderConverter() {
		return (notification, builder) -> {
			return builder
				.withHeading("en", evaluate("⏰ Rappel", notification))
				.withContent("en", evaluate("📆 ${T(fr.yoga.booking.util.DateRangeUtil).format(nextClass)}\n"
						+ "📍 ${nextClass.lesson.place.name}\n"
						+ "\n"
						+ "Tu es inscris au cours du ${T(fr.yoga.booking.util.DateRangeUtil).format(nextClass)}.\n"
						+ "\n"
						+ "Pense à arriver 10 minutes avant pour que le cours commence à l'heure.\n"
						+ "Si tu ne peux pas être présent, pense à te désinscrire pour laisser la place à une autre personne.", notification))
				.withButton(new Button("unbook", "Me désinscrire", null, null))
				.withDataElement("classId", ((ReminderNotification) notification).getNextClass().getId())
				.build();
		};
	}
}
