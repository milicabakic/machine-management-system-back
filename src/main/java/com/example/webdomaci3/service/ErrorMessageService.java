package com.example.webdomaci3.service;

import com.example.webdomaci3.model.ErrorMessage;
import com.example.webdomaci3.model.Machine;
import com.example.webdomaci3.repository.ErrorMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ErrorMessageService {

    private ErrorMessageRepository errorMessageRepository;

    @Autowired
    public ErrorMessageService(ErrorMessageRepository errorMessageRepository){
        this.errorMessageRepository = errorMessageRepository;
    }

    public void saveError(Machine machine, Date date, String action, String message){
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDate(date);
        errorMessage.setMachine(machine);
        errorMessage.setScheduledAction(action);
        errorMessage.setMessage(message);
        this.errorMessageRepository.save(errorMessage);
    }

    public List<ErrorMessage> findAll(String username, Integer page, Integer size){
        return this.errorMessageRepository.findByCreatedBy(username);
    }
}
