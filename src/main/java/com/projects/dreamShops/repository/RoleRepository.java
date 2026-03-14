package com.projects.dreamShops.repository;

import com.projects.dreamShops.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String role);
}
