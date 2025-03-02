package io.project.service.conteoller;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.entity.BinaryContent;
import io.project.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileController {

    private final FileService fileService;

    @RequestMapping("/doc")
    public ResponseEntity<?> getDocument(@RequestParam String id) {
        AppDocument appDocument = fileService.getAppDocument(id);
        if (appDocument == null) {
            log.error("File is not found");
            return ResponseEntity.notFound().build();
        }
        BinaryContent binaryContent = appDocument.getBinaryContent();
        FileSystemResource fsr = fileService.getFileSystemResource(binaryContent);
        if (binaryContent == null) {
            log.error("File is not processed");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appDocument.getMimeType()))
                .header("Content-Disposition", "attachment; filename=" + appDocument.getFileName())
                .body(fsr);
    }

    @RequestMapping("/photo")
    public ResponseEntity<?> getPhoto(@RequestParam String id) {
        AppPhoto photo = fileService.getAppPhoto(id);
        if (photo == null) {
            log.error("File is not found");
            return ResponseEntity.notFound().build();
        }
        BinaryContent binaryContent = photo.getBinaryContent();
        FileSystemResource fsr = fileService.getFileSystemResource(binaryContent);
        if (binaryContent == null) {
            log.error("File is not processed");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-Disposition", "attachment;")
                .body(fsr);
    }
}
