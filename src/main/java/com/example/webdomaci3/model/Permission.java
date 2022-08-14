package com.example.webdomaci3.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "permissions")
   /* @JoinTable(
            name = "USER_PERMISSION",
            joinColumns = @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    )*/
   // @JsonBackReference //iskljuci iz serijalizacije
    @JsonIgnore
    private List<User> users = new ArrayList<>();


    public void addUser(User user){
        users.add(user);
        user.getPermissions().add(this);
    }

    public void removeUser(User user){
        users.remove(user);
        user.getPermissions().remove(this);
    }

}
