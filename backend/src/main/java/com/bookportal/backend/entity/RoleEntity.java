package com.bookportal.backend.entity;

import com.bookportal.backend.entity.enums.ERole;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 20)
    private ERole name;

    public RoleEntity() {}

    public RoleEntity(ERole name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public ERole getName() { return name; }

    public void setName(ERole name) { this.name = name; }
}
