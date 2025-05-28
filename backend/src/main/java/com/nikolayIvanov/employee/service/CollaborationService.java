package com.nikolayIvanov.employee.service;

import com.nikolayIvanov.employee.model.Assignment;
import com.nikolayIvanov.employee.model.DetailedResult;
import com.nikolayIvanov.employee.model.PairResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollaborationService {
    private static final DateTimeFormatter DATE_PARSER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            .toFormatter();

    public PairResult findMaxPair(Reader csvReader) throws IOException {
        try (CSVParser parser = prepareParser(csvReader)) {
            List<Assignment> assignments = parser.getRecords().stream().map(this::toAssignment).collect(Collectors.toList());
            Map<String, Long> totals = assignments.stream()
                    .collect(Collectors.groupingBy(Assignment::getProjectId))
                    .values().stream()
                    .flatMap(group -> group.stream().flatMap(a -> group.stream().filter(b -> b.getEmpId() > a.getEmpId())
                                    .map(b -> new DetailedResult(a.getEmpId(), b.getEmpId(), a.getProjectId(), overlapDays(a,b)))))
                    .filter(d -> d.days > 0)
                    .collect(Collectors.groupingBy(d -> d.emp1 + "-" + d.emp2, Collectors.summingLong(d -> d.days)));

            return totals.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> {String[] ids = e.getKey().split("-");
                        return new PairResult(Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), e.getValue());
                    })
                    .orElse(null);
        }
    }
    public List<DetailedResult> findAllDetails(Reader csvReader) throws IOException {
        try (CSVParser parser = prepareParser(csvReader)) {
            List<Assignment> list = parser.getRecords().stream().map(this::toAssignment).collect(Collectors.toList());
            List<DetailedResult> details = new ArrayList<>();
            Map<Integer, List<Assignment>> byProj = list.stream().collect(Collectors.groupingBy(Assignment::getProjectId));
            byProj.forEach((proj, grp) ->
                    grp.stream().flatMap(a -> grp.stream().filter(b -> b.getEmpId() > a.getEmpId())
                        .map(b -> new DetailedResult(a.getEmpId(), b.getEmpId(), proj, overlapDays(a,b)))).filter(d -> d.days > 0).forEach(details::add)
            );
            return details;
        }
    }
    private long overlapDays(Assignment a, Assignment b) {
        LocalDate start = a.getDateFrom().isAfter(b.getDateFrom()) ? a.getDateFrom() : b.getDateFrom();
        LocalDate end = a.getDateTo().isBefore(b.getDateTo()) ? a.getDateTo() : b.getDateTo();
        return start.isBefore(end) ? ChronoUnit.DAYS.between(start, end) + 1 : 0;
    }
    private LocalDate parseDate(String input) {
        if ("NULL".equalsIgnoreCase(input.trim())) {return LocalDate.now();}
        try {
            return LocalDate.parse(input.trim(), DATE_PARSER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date: '" + input + "'. Use YYYY-MM-DD, dd/MM/yyyy, MM-dd-yyyy or yyyy.MM.dd");
        }
    }
    private Assignment toAssignment(CSVRecord csvRecprd) {
        try {
            int emp = Integer.parseInt(csvRecprd.get("EmpID").trim());
            int proj = Integer.parseInt(csvRecprd.get("ProjectID").trim());
            LocalDate from = parseDate(csvRecprd.get("DateFrom"));
            LocalDate to = parseDate(csvRecprd.get("DateTo"));
            return new Assignment(emp, proj, from, to);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Line " + csvRecprd.getRecordNumber() + ": " + ex.getMessage());
        }
    }
    private CSVParser prepareParser(Reader csvReader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader("EmpID","ProjectID","DateFrom","DateTo").setSkipHeaderRecord(true).build();
        CSVParser parser = new CSVParser(csvReader, format);
        Set<String> hdrs = parser.getHeaderMap().keySet();
        if (!hdrs.containsAll(Arrays.asList("EmpID","ProjectID","DateFrom","DateTo"))) {
            parser.close();
            throw new IllegalArgumentException("CSV header must include EmpID, ProjectID, DateFrom, DateTo");
        }
        return parser;
    }
}
