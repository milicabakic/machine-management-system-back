package com.example.webdomaci3.requests;

import lombok.Data;

import java.util.List;

@Data
public class AddPermissionsRequest {

    private String username;
    private List<String> permissions;

}
