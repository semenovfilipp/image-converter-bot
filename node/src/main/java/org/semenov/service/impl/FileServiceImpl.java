package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.semenov.dao.AppDocumentDao;
import org.semenov.dao.AppPhotoDao;
import org.semenov.dao.BinaryContentDao;
import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.entity.BinaryContent;
import org.semenov.exception.UploadFileException;
import org.semenov.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/*
 * Сервис получает message из Telegram
 * Выполняет действия для скачивания файла
 * Сохраняет файл в БД
 */

@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.file_info.uri}")
    private String fileInfoUri;

    @Value("${telegram.bot.file_storage.uri}")
    private String fileStorageUri;

    private final AppPhotoDao appPhotoDao;

    private final AppDocumentDao appDocumentDao;
    private final BinaryContentDao binaryContentDao;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDocument = telegramMessage.getDocument();
        String fileId = telegramDocument.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);

        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDocument = buildTransientAppDocument(
                    telegramDocument, persistentBinaryContent);

            return appDocumentDao.save(transientAppDocument);
        } else {
            throw new UploadFileException("Error downloading file" + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        // TODO выбирает из фото какие нужны
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);

        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(
                    telegramPhoto, persistentBinaryContent);

            return appPhotoDao.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Error downloading file" + response);
        }
    }


    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);

        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDao.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
        return filePath;
    }



    private AppDocument buildTransientAppDocument(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
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
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);

        return  restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}",token)
                .replace("{filePath}",filePath);

        URL urlObject = null;
        try{
            urlObject = new URL(fullUri);
        }catch (MalformedURLException e){
            throw new UploadFileException(e);
        }

        // TODO исравить
        try(InputStream is = urlObject.openStream()){
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObject.toExternalForm(),e);
        }
    }


}
