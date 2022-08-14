package com.example.webdomaci3.repository;

import com.example.webdomaci3.model.Permission;
import com.example.webdomaci3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.permissions")
    List<User> fetchAll();

    Boolean existsByUsernameAndPermissionsName(String username, String permissionName);

    @Query("SELECT p FROM Permission p INNER JOIN FETCH p.users u WHERE u.username LIKE :username")
    List<Permission> getAllPermissionsForUser(@Param("username") String username);

    User findByUsername(String username);

    User saveAndFlush(User user);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.permissions p WHERE p.name LIKE :permission_name")
    List<User> findUsersByPermission(@Param("permission_name") String permission);

    @Query("select p from Permission p where p.name like :name")
    List<Permission> allPerm(@Param("name")String name);

}
