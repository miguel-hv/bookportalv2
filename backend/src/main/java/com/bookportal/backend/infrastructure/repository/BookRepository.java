package com.bookportal.backend.infrastructure.repository;

import com.bookportal.backend.domain.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findByUserId(Long userId);
}
