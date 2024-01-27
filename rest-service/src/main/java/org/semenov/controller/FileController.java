package org.semenov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.semenov.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Контроллер для обработки запросов со стороны пользователя
 */
@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDocument(@RequestParam("id") String id) {
        var document = fileService.getDocument(id);
        if (document == null) {
            log.error("Document with id " + id + " not found");
            return ResponseEntity.notFound().build();
        }
        var binaryContent = document.getBinaryContent();

        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResources == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(document.getMimeType())
                )
                .header("Content-disposition", "attachment; filename=" + document.getDocName())
                .body(fileSystemResources);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var photo = fileService.getPhoto(id);
        if (photo == null) {
            log.error("Photo with id " + id + " not found");
            return ResponseEntity.notFound().build();
        }
        var binaryContent = photo.getBinaryContent();

        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResources == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment; filename=")
                .body(fileSystemResources);
    }
}


