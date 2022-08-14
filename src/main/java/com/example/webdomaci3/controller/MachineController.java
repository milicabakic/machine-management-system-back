package com.example.webdomaci3.controller;

import com.example.webdomaci3.dto.MachineDto;
import com.example.webdomaci3.dto.UserDto;
import com.example.webdomaci3.model.Machine;
import com.example.webdomaci3.model.User;
import com.example.webdomaci3.requests.AddMachineRequest;
import com.example.webdomaci3.requests.FilterMachineRequest;
import com.example.webdomaci3.requests.ScheduleActionRequest;
import com.example.webdomaci3.service.MachineService;
import com.example.webdomaci3.service.UserService;
import com.example.webdomaci3.utils.JWTUtil;
import com.example.webdomaci3.utils.PermissionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/machine")
public class MachineController {

    private final MachineService machineService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;


    public MachineController(MachineService machineService, UserService userService, JWTUtil jwtUtil){
        this.machineService = machineService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addMachine(@RequestBody AddMachineRequest machine) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.CREATE_MACHINES)) {
            return ResponseEntity.ok(machineService.addMachine(machine.getName(),user));
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> destroyMachine(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.DESTROY_MACHINES)){
            return machineService.destroyMachine(id, user);
        }

        return ResponseEntity.status(403).build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);

        Page<Machine> machines = machineService.findAll(user, page, size);
        return ResponseEntity.ok(machines.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @PostMapping(value = "/search" , consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@RequestBody FilterMachineRequest filterMachineRequest, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);

        List<Machine> machines = machineService.search(user, filterMachineRequest, page, size);
        return ResponseEntity.ok(machines.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @GetMapping(value = "/start/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> start(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.START_MACHINES)){
            try {
                machineService.startMachine(id, username, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok().body("OK");
        }
        return ResponseEntity.status(403).body("User does not have required permission!");
    }

    @GetMapping(value = "/stop/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stop(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.STOP_MACHINES)){
            machineService.stopMachine(id, username, false);
            return ResponseEntity.ok().body("OK");
        }
        return ResponseEntity.status(403).body("User does not have required permission!");
    }

    @GetMapping(value = "/restart/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restart(@PathVariable Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(userService.existsByUsernameAndPermissionName(username, PermissionUtil.RESTART_MACHINES)){
            machineService.restartMachine(id, username, false);
            return ResponseEntity.ok().body("OK");
        }
        return ResponseEntity.status(403).body("User does not have required permission!");
    }

    @PostMapping(value = "/schedule/{id}" , consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> schedule(@PathVariable Long id, @RequestBody ScheduleActionRequest scheduleActionRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        machineService.scheduleAction(id, username, scheduleActionRequest.getAction(), scheduleActionRequest.getDate());
        return ResponseEntity.ok().body("OK");
    }

    private MachineDto convertToDto(Machine machine) {
        MachineDto machineDto = modelMapper.map(machine, MachineDto.class);
        machineDto.setCreator(machine.getCreatedBy().getUsername());
        return machineDto;
    }

}
