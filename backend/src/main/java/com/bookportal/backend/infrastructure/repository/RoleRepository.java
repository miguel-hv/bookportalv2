package com.bookportal.backend.infrastructure.repository;

import com.bookportal.backend.domain.model.enums.ERole;
import com.bookportal.backend.domain.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);
}
