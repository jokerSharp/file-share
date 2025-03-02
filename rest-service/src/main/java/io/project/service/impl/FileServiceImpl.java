package io.project.service.impl;

import io.project.dao.AppDocumentDAO;
import io.project.dao.AppPhotoDAO;
import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.entity.BinaryContent;
import io.project.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Log4j2
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    @Override
    public AppDocument getAppDocument(String documentId) {
        //todo implement decoding
        Long id = Long.parseLong(documentId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getAppPhoto(String photoId) {
        //todo implement decoding
        Long id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //todo add tmp file name generation
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getBinaryContent());
            return new FileSystemResource(temp);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
