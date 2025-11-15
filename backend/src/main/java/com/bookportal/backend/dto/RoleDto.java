package com.bookportal.backend.dto;

public class RoleDto {

    private String name;

    public RoleDto() {}

    public RoleDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
