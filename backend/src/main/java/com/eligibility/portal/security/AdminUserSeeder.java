package com.eligibility.portal.security;

import com.eligibility.portal.entity.AppUser;
import com.eligibility.portal.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Idempotently seeds the single staff login - there is no self-registration screen. */
@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final String seedUsername;
    private final String seedPassword;

    public AdminUserSeeder(AppUserRepository appUserRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.username}") String seedUsername,
                            @Value("${app.admin.password}") String seedPassword) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedUsername = seedUsername;
        this.seedPassword = seedPassword;
    }

    @Override
    public void run(String... args) {
        if (appUserRepository.findByUsername(seedUsername).isPresent()) {
            return;
        }
        AppUser appUser = new AppUser();
        appUser.setUsername(seedUsername);
        appUser.setPasswordHash(passwordEncoder.encode(seedPassword));
        appUser.setRole("ROLE_ADMIN");
        appUserRepository.save(appUser);
    }
}
