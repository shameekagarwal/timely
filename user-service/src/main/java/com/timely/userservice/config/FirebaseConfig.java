package com.timely.userservice.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.timely.userservice.constant.ProfilesConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ ProfilesConstants.PROD, ProfilesConstants.DEV, ProfilesConstants.QA })
public class FirebaseConfig {

	private final String firebaseCredentials;

	public FirebaseConfig(@Value("${custom.firebase.credentials}") String firebaseCredentials) {
		this.firebaseCredentials = firebaseCredentials;
	}

	@Primary
	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			InputStream credentialsStream = new ByteArrayInputStream(firebaseCredentials.getBytes());
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(credentialsStream)).build();
			FirebaseApp.initializeApp(options);
		}
		return FirebaseApp.getInstance();
	}

	@Bean
	public FirebaseAuth firebaseAuth() throws IOException {
		return FirebaseAuth.getInstance(firebaseApp());
	}

}