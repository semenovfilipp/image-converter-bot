package org.semenov.service;

import org.semenov.entity.AppDocument;
import org.semenov.entity.AppPhoto;
import org.semenov.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
