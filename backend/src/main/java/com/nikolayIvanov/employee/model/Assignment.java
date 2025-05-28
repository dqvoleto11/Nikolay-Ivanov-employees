package com.nikolayIvanov.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class Assignment {
    private int empId;
    private int projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
