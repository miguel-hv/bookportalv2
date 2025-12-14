package com.bookportal.backend.repository;

import com.bookportal.backend.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findByUserId(Long userId);
}
