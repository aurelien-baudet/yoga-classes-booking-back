package fr.yoga.booking.config;

import static com.google.auth.oauth2.GoogleCredentials.fromStream;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import fr.yoga.booking.service.technical.notification.FcmProperties;

@Configuration
public class FcmConfig {

	@Bean
	@ConditionalOnMissingBean(FirebaseMessaging.class)
	public FirebaseMessaging fcm(FcmProperties props) throws IOException {
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(fromStream(props.getServiceAccountFile().getInputStream())).build();

		FirebaseApp app = FirebaseApp.initializeApp(options, randomAlphabetic(10));
		
		return FirebaseMessaging.getInstance(app);
	}
}
