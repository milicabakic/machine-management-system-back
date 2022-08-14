package com.example.webdomaci3.requests;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FilterMachineRequest {

    String name;
    List<String> status;
    Date dateFrom;
    Date dateTo;

}
