package io.project.service;

import io.project.entity.AppUser;

public interface AppUserService {

    String registerUser(AppUser appUser);

    String setEmail(AppUser appUser, String email);
}
