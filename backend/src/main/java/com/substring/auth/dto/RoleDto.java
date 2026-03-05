package com.substring.auth.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.util.UUID;

public class RoleDto {

    private UUID id = UUID.randomUUID();

    private String name;

    public RoleDto(){

    }
    public RoleDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
