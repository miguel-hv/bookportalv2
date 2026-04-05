package com.bookportal.backend.domain.model;

import com.bookportal.backend.domain.model.enums.ERole;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
@Access(AccessType.FIELD)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 20)
    private ERole name;

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public ERole getName() { return name; }

    @Deprecated
    public void setName(ERole name) { this.name = name; }
}