package com.example.webdomaci3.repository;

import com.example.webdomaci3.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {


    Permission findByName(String name);
}
