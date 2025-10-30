package com.bookportal.backend.repository;

import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);
}
