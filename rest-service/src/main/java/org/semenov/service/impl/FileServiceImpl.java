package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.semenov.dao.AppDocumentDao;
import org.semenov.dao.AppPhotoDao;
import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.service.FileService;
import org.semenov.utils.CryptoTool;
import org.springframework.stereotype.Service;
/*
 * Сервис для работы с файлами
 */
@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    private final CryptoTool cryptoTool;
    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appDocumentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appPhotoDao.findById(id).orElse(null);
    }
}
