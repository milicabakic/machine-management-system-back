package com.example.webdomaci3.requests;

import java.util.Date;

public class ScheduleActionRequest {

    String action;
    Date date;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
