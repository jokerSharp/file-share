package io.project.dao;

import io.project.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    AppUser findByTelegramUserId(long telegramUserId);
}
