package io.project.service.impl;

import io.project.dao.AppUserDAO;
import io.project.dto.MailParams;
import io.project.entity.AppUser;
import io.project.entity.UserState;
import io.project.service.AppUserService;
import io.project.utils.CryptoUtils;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    @Value("${service.mail.uri}")
    private String serviceMailUri;

    private final AppUserDAO appUserDAO;
    private final CryptoUtils cryptoUtils;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Your account is already active";
        } else if (appUser.getEmail() != null) {
            return "Your have already received an activation email";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Please input your email address";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Invalid email address. Input /cancel";
        }
        Optional<AppUser> optionalAppUser = appUserDAO.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUserDAO.save(appUser);

            String cryptoUserId = cryptoUtils.hashOf(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String message = "Sending email to %s failed".formatted(email);
                log.error(message);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return message;
            }
            return "You will receive an activation email. Follow the link to finish the registration";
        } else {
            return "This email already is in use. Please type /cancel and choose another email";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(serviceMailUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
