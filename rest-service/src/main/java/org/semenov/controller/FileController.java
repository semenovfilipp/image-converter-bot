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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Контроллер для обработки запросов со стороны пользователя
 * Забирает данные из БД
 */
@Log4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public void getDocument(
            @RequestParam("id") String id,
            HttpServletResponse response) {
        var document = fileService.getDocument(id);
        if (document == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.parseMediaType(document.getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment; filename=" + document.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = document.getBinaryContent();
        try{
            var out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes());
            out.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public void getPhoto(
            @RequestParam("id") String id,
            HttpServletResponse response) {
        var photo = fileService.getPhoto(id);
        if (photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment; filename=");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = photo.getBinaryContent();
        try {
            var out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes());
            out.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}


