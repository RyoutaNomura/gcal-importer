package com.rn.tool.gcalimporter.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Class of utility for Authorization of Google
 */
@UtilityClass
@Slf4j
public class GoogleAuthorizationUtils {

  /**
   * Create an authorized credential object.
   *
   * @return an authorized {@link Credential} object.
   */
  public static Credential authorize(@NonNull final Path clientSecret,
      @NonNull final Path dataStoreDir,
      final List<String> scopes) {

    try (InputStream in = Files.newInputStream(clientSecret)) {

      final FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir.toFile());
      final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

      final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

      final GoogleClientSecrets clientSecrets = GoogleClientSecrets
          .load(jsonFactory, new InputStreamReader(in));

      final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
          httpTransport, jsonFactory, clientSecrets, scopes)
          .setDataStoreFactory(dataStoreFactory)
          .setAccessType("offline")
          .build();
      final Credential credential = new AuthorizationCodeInstalledApp(
          flow, new LocalServerReceiver()).authorize("user");
      log.info("Credentials saved to " + dataStoreDir.toAbsolutePath());

      return credential;

    } catch (final GeneralSecurityException | IOException e) {
      log.error("An error occurred during authorization", e);
      throw new RuntimeException(e);
    }
  }
}
