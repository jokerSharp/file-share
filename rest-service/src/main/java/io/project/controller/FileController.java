package io.project.controller;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.entity.BinaryContent;
import io.project.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileController {

    private final FileService fileService;

    @RequestMapping("/doc")
    public void getDocument(@RequestParam String id, HttpServletResponse response) {
        AppDocument appDocument = fileService.getAppDocument(id);
        if (appDocument == null) {
            log.error("File is not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.parseMediaType(appDocument.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment; filename=" + appDocument.getFileName());
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = appDocument.getBinaryContent();
        try (ServletOutputStream out = response.getOutputStream();) {
            out.write(binaryContent.getBinaryContent());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/photo")
    public void getPhoto(@RequestParam String id, HttpServletResponse response) {
        AppPhoto photo = fileService.getAppPhoto(id);
        if (photo == null) {
            log.error("Photo is not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("Content-Disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        BinaryContent binaryContent = photo.getBinaryContent();
        try (ServletOutputStream out = response.getOutputStream();) {
            out.write(binaryContent.getBinaryContent());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
