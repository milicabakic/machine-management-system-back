package com.example.webdomaci3.controller;

import com.example.webdomaci3.dto.ErrorMessageDto;
import com.example.webdomaci3.dto.MachineDto;
import com.example.webdomaci3.model.ErrorMessage;
import com.example.webdomaci3.model.Machine;
import com.example.webdomaci3.model.User;
import com.example.webdomaci3.service.ErrorMessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/errors")
public class ErrorMessageController {

    private ErrorMessageService errorMessageService;
    private ModelMapper modelMapper;

    @Autowired
    public ErrorMessageController(ErrorMessageService errorMessageService, ModelMapper modelMapper){
        this.errorMessageService = errorMessageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ErrorMessage> errors = errorMessageService.findAll(username, page, size);
        return ResponseEntity.ok(errors.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private ErrorMessageDto convertToDto(ErrorMessage error) {
        ErrorMessageDto errorDto = modelMapper.map(error, ErrorMessageDto.class);
        errorDto.setMachine_id(error.getMachine().getId());
        return errorDto;
    }

}
