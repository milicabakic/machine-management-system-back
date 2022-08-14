package com.example.webdomaci3.repository;

import com.example.webdomaci3.model.ErrorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {

    @Query("SELECT e FROM ErrorMessage e WHERE e.machine.createdBy.username LIKE :username")
    List<ErrorMessage> findByCreatedBy(String username);
}
