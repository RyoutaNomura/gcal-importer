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
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleAuthorizationUtils {

  /**
   * Creates an authorized Credential object.
   *
   * @return an authorized Credential object.
   * @throws IOException will be thrown when secret cannot be accessed or HTTP access cannot be
   * done.
   */
  public static Credential authorize(final File dataStoreDir, final List<String> scopes) {

    try (InputStream in = Resources.getResource("private/client_secret.json").openStream()) {

      final FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
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
      log.info("Credentials saved to " + dataStoreDir.getAbsolutePath());

      return credential;

    } catch (final GeneralSecurityException | IOException e) {
      log.error("An error occurred during authorization", e);
      throw new RuntimeException(e);
    }
  }
}
