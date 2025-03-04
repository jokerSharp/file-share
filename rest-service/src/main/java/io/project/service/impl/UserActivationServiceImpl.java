package io.project.service.impl;

import io.project.dao.AppUserDAO;
import io.project.entity.AppUser;
import io.project.service.UserActivationService;
import io.project.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;
    private final CryptoUtils cryptoUtils;

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoUtils.idOf(cryptoUserId);
        Optional<AppUser> optionalAppUser = appUserDAO.findById(userId);
        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            appUser.setIsActive(true);
            appUserDAO.save(appUser);
            return true;
        }
        return false;
    }
}
