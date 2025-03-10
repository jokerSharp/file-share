package io.project.service.impl;

import io.project.dao.AppDocumentDAO;
import io.project.dao.AppPhotoDAO;
import io.project.dao.BinaryContentDAO;
import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.entity.BinaryContent;
import io.project.exception.UploadFileException;
import io.project.service.FileService;
import io.project.service.enums.LinkType;
import io.project.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final CryptoUtils cryptoUtils;

    @Override
    public AppDocument processDocument(Message externalMessage) {
        Document telegramDocument = externalMessage.getDocument();
        String fileId = telegramDocument.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode().is2xxSuccessful()) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDocument = buildTransientAppDocument(telegramDocument, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message externalMessage) {
        int photoCount = externalMessage.getPhoto().size();
        int photoIndex = photoCount > 1 ? externalMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = externalMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode().is2xxSuccessful()) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileBytes = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder().binaryContent(fileBytes).build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
    }

    private AppDocument buildTransientAppDocument(Document telegramDocument, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDocument.getFileId())
                .fileName(telegramDocument.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDocument.getMimeType())
                .fileSize(telegramDocument.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    @Override
    public String generateLink(Long id, LinkType linkType) {
        String hash = cryptoUtils.hashOf(id);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}
