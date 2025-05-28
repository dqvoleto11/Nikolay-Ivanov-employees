package com.nikolayIvanov.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PairResult {
    private int emp1;
    private int emp2;
    private long days;
}
