package io.project.service;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    AppDocument getAppDocument(String id);
    AppPhoto getAppPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
