package org.semenov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.semenov.dao.AppDocumentDao;
import org.semenov.dao.AppPhotoDao;
import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
/*
 * Сервис для работы с файлами
 */
@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{
    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    @Override
    public AppDocument getDocument(String documentId) {
        // TODO добавить хеш
        var id = Long.parseLong(documentId);
        return appDocumentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        // TODO добавить хеш
        var id = Long.parseLong(photoId);
        return appPhotoDao.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try{
            // TODO добавить генерацию временных файлов
            File temp = File.createTempFile("tempFile", "bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e){
            log.error(e);
            return null;
        }
    }
}
