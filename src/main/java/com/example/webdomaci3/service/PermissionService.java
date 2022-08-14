package com.example.webdomaci3.service;

import com.example.webdomaci3.model.Permission;
import com.example.webdomaci3.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    private PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }


    public Permission getPermission(String name){
        return permissionRepository.findByName(name);
    }

    public List<Permission> findAll(){
        return permissionRepository.findAll();
    }

}
