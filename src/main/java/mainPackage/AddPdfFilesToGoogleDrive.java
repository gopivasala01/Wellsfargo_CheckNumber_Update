package mainPackage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AddPdfFilesToGoogleDrive {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/google-credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String sharedDriveId ="0AERYvHQmbzjIUk9PVA";
    private static final String PdfFilesFolder ="C:\\Users\\gopi\\Documents\\Target Rent Files\\PDFs";

    private static Credential getCredentials(final HttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = AddPdfFilesToGoogleDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void addingPDFToDrive() throws IOException, GeneralSecurityException {
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport(); 
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        uploadFiles(service, PdfFilesFolder,sharedDriveId);
    }

    private static void uploadFiles(Drive service, String folderPath, String sharedDriveId) throws IOException {
    	 // Create folder with current date
        String currentDateFolderName = "PaymentFiles - " + new Date().toString();
        com.google.api.services.drive.model.File folderMetadata = new com.google.api.services.drive.model.File();
        folderMetadata.setName(currentDateFolderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        folderMetadata.setParents(Collections.singletonList(sharedDriveId)); // Set Shared Drive ID
        com.google.api.services.drive.model.File createdFolder = service.files().create(folderMetadata)
                .setSupportsAllDrives(true) // Make sure this is set to true for Shared Drives
                .setFields("id")
                .execute();
        String folderId = createdFolder.getId();

        // Upload files to the created folder
        java.io.File folder = new java.io.File(folderPath); // Replace with actual folder path
        java.io.File[] files = folder.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                if (file.isFile()) {
                    com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                    fileMetadata.setName(file.getName());
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    FileContent mediaContent = new FileContent("application/pdf", file);
                    com.google.api.services.drive.model.File uploadedFile = service.files().create(fileMetadata, mediaContent)
                            .setSupportsAllDrives(true) // Make sure this is set to true for Shared Drives
                            .setFields("id")
                            .execute();
                    System.out.println("Uploaded File: " + file.getName() + ", ID: " + uploadedFile.getId());
                }
            }
        }
    }
}