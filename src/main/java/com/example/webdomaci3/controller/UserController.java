package com.example.webdomaci3.controller;

import com.example.webdomaci3.dto.UserDto;
import com.example.webdomaci3.model.Permission;
import com.example.webdomaci3.model.User;
import com.example.webdomaci3.requests.AddPermissionRequest;
import com.example.webdomaci3.requests.AddPermissionsRequest;
import com.example.webdomaci3.service.PermissionService;
import com.example.webdomaci3.service.UserService;
import com.example.webdomaci3.utils.JWTUtil;
import com.example.webdomaci3.utils.PermissionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTMLDocument;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    public UserController(UserService userService, JWTUtil jwtUtil, PasswordEncoder passwordEncoder,
                          PermissionService permissionService){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.permissionService = permissionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody User userToAdd) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
/*
        !
        User user = userService.findUserByUsername(username);
        List<User> allowedUsers = userService.findUsersByPermission(PermissionUtil.CREATE_USER);
*/
        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.CREATE_USER)) {
            userToAdd.setPassword(passwordEncoder.encode(userToAdd.getPassword()));
            return ResponseEntity.ok(userService.addUser(userToAdd));
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.READ_USER)) {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users.stream().map(this::convertToDto).collect(Collectors.toList()));
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> find(@PathVariable Long id){
        User user = userService.getUser(id);
        if(user != null){
            return ResponseEntity.ok(convertToDto(user));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody UserDto userToUpdate){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.UPDATE_USER)){
            try {
                User user = convertToEntity(userToUpdate);
                userService.save(user);
                return ResponseEntity.ok().build();
            }catch (ParseException e){
                return ResponseEntity.status(400).build();
            }
        }

        return ResponseEntity.status(403).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userToDelete = userService.getUser(id);

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.DELETE_USER) && userToDelete != null){
            userService.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping("/{id}/{permissionName}")
    public ResponseEntity<?> hasPerm(@PathVariable Long id, @PathVariable String permissionName){
        User user = userService.getUser(id);
        List<User> allowedUsers = userService.findUsersByPermission(permissionName);

        if(user != null && allowedUsers.contains(user)){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(403).build();
    }

    @PostMapping(value = "/perm/all", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPermissions(@RequestBody AddPermissionsRequest requestBody){
        User user = userService.findUserByUsername(requestBody.getUsername());

        List<Permission> allPermissions = permissionService.findAll();
        List<Permission> usersPermissions = userService.getAllPermissionsForUser(user.getUsername());
        for(Permission permission : allPermissions) {
            if(usersPermissions.contains(permission) && !requestBody.getPermissions().contains(permission.getName())){
               permission.removeUser(user);
               userService.save(user);
            }
            else if(!usersPermissions.contains(permission) && requestBody.getPermissions().contains(permission.getName())){
                permission.addUser(user);
                userService.save(user);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/perm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPermissions(@RequestBody AddPermissionRequest requestBody){
        User user = userService.findUserByUsername(requestBody.getUserUsername());
        Permission permission = permissionService.getPermission(requestBody.getPermissionName());
        permission.addUser(user);
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/perm/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPermissionsForUser(@PathVariable String username){
        return ResponseEntity.ok(userService.getAllPermissionsForUser(username));
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }

    private User convertToEntity(UserDto userDto) throws ParseException {
        User user = modelMapper.map(userDto, User.class);

        User oldUser = userService.getUser(userDto.getId());
        user.setPassword(oldUser.getPassword());
        if(userDto.getPermissions() == null){
            user.setPermissions(oldUser.getPermissions());
        }

        return user;
    }

}
