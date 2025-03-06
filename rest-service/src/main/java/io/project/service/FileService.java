package io.project.service;

import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;

public interface FileService {

    AppDocument getAppDocument(String id);

    AppPhoto getAppPhoto(String id);
}
