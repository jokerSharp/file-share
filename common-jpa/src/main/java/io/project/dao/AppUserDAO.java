package io.project.dao;

import io.project.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByTelegramUserId(long telegramUserId);

    Optional<AppUser> findByEmail(String email);
}
