package com.bookportal.backend.mapper;

import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.entity.BookEntity;

public class BookMapper {

    public static BookDto toDto(BookEntity entity) {
        if (entity == null) return null;

        return new BookDto(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getReview()
        );
    }

    public static BookEntity toEntity(BookDto dto) {
        if (dto == null) return null;

        BookEntity entity = new BookEntity();
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setReview(dto.getReview());
        return entity;
    }
}
