package com.example.webdomaci3.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @NotBlank(message = "Status is mandatory")
    private String status;

    @ManyToOne()
    @NotNull(message = "Creator is mandatory")
    private User createdBy;

    @Column
    @NotNull(message = "Activity status is mandatory")
    private boolean active;

    @Column
    private Date dateFrom;

    @Column(nullable = false)
    private boolean inUse = false;

    /*
    @Version
    private Integer version = 0;
     */

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                '}';
    }
}
