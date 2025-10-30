package com.bookportal.backend.config;

import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new RoleEntity(ERole.ROLE_USER));
            roleRepository.save(new RoleEntity(ERole.ROLE_ADMIN));
        }
    }
}
