package com.example.webdomaci3.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorMessageDto {

    private Long id;
    private Date date;
    private Long machine_id;
    private String scheduledAction;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getMachine_id() {
        return machine_id;
    }

    public void setMachine_id(Long machine_id) {
        this.machine_id = machine_id;
    }

    public String getScheduledAction() {
        return scheduledAction;
    }

    public void setScheduledAction(String scheduledAction) {
        this.scheduledAction = scheduledAction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
