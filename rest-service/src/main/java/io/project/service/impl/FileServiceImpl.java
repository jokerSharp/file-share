package io.project.service.impl;

import io.project.dao.AppDocumentDAO;
import io.project.dao.AppPhotoDAO;
import io.project.entity.AppDocument;
import io.project.entity.AppPhoto;
import io.project.service.FileService;
import io.project.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoUtils cryptoUtils;

    @Override
    public AppDocument getAppDocument(String hash) {
        Long id = cryptoUtils.idOf(hash);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getAppPhoto(String hash) {
        Long id = cryptoUtils.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }
}
