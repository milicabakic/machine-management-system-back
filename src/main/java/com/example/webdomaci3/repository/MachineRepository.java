package com.example.webdomaci3.repository;

import com.example.webdomaci3.model.Machine;
import com.example.webdomaci3.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface MachineRepository extends JpaRepository<Machine,Long>, JpaSpecificationExecutor<Machine> {

    Boolean existsByIdAndStatusAndCreatedBy(Long id, String status, User user);

    @Modifying
    @Query("UPDATE Machine m SET m.active = 0 WHERE m.id = :id")
    void destroy(Long id);

    Page<Machine> findByCreatedByAndActive(User user, boolean active, Pageable pageable);

    @Query("SELECT m FROM Machine m WHERE m.createdBy = :user AND m.active = TRUE AND " +
            "(:name IS NUL OR LOWER(m.name) LIKE CONCAT('%', :name)) AND " +
            "m.status IN :status AND (m.dateFrom BETWEEN :dateFrom AND :dateTo)")
    List<Machine> searchMachines(User user, String name, List<String> status, Date dateFrom, Date dateTo);

    @Query("SELECT m FROM Machine m WHERE m.createdBy = :user AND m.active = TRUE AND " +
            "(:name IS NULL OR LOWER(m.name) LIKE CONCAT('%', :name)) AND " +
            "m.status IN :status")
    List<Machine> searchMachines(User user, String name, List<String> status);
}
