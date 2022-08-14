package com.example.webdomaci3.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column
    private String name;

    @Column
    private String surname;

    @ManyToMany
   /* @JoinTable(
            name = "USER_PERMISSION",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "ID")
    )*/
   // @JsonManagedReference  //ukljuci u serijalizaciju
   // @JsonIgnore ;

    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    private List<Machine> machines = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
