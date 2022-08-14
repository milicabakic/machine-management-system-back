package com.example.webdomaci3.requests;

import lombok.Data;

@Data
public class AddPermissionRequest {

    private String userUsername;
    private String permissionName;

}
