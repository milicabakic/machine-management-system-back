package com.example.webdomaci3.service;

import com.example.webdomaci3.model.Permission;
import com.example.webdomaci3.model.User;
import com.example.webdomaci3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService{

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);

        if(user != null){
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }

        throw new UsernameNotFoundException("User with username "+username+" not found");
    }

    public User addUser(User user) throws Exception {
        if(this.userRepository.findByUsername(user.getUsername()) != null){
            throw new Exception("User with this username already exists!");
        }
        return userRepository.save(user);
    }

    public List<User> findUsersByPermission(String permission){
        return userRepository.findUsersByPermission(permission);
    }

    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public List<Permission> getAllPermissionsForUser(String username){
        return userRepository.getAllPermissionsForUser(username);
    }

    public boolean existsByUsernameAndPermissionName(String username, String permissionName){
        return userRepository.existsByUsernameAndPermissionsName(username, permissionName);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public List<User> findAll(){
        List<User> users = userRepository.fetchAll();
      //  List<User> users = userRepository.findAll();
        return users;
    }

    public User getUser(Long id){
        return userRepository.getById(id);
    }
}
