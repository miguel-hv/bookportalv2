package com.bookportal.backend.application.mapper;

import com.bookportal.backend.application.mapper.BookMapper;
import com.bookportal.backend.dto.UserBookDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.domain.model.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto toDto(User entity) {
        if (entity == null) return null;

        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getRoles()
                        .stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet())
        );
    }

    public static UserBookDto toDtoList(User entity) {
        if (entity == null) return null;

        return new UserBookDto(
                entity.getId(),
                entity.getUsername(),
                entity.getRoles()
                        .stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet()),
                entity.getBooks()
                        .stream()
                        .map(BookMapper::toDto)
                        .collect(Collectors.toSet())
        );
    }
}
