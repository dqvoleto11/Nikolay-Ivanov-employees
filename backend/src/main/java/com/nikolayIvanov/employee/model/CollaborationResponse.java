package com.nikolayIvanov.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CollaborationResponse {
    private PairResult summary;
    private List<DetailedResult> details;
}
