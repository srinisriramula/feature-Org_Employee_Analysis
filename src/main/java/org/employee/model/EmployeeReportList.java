package org.employee.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmployeeReportList {
    Map<Employee, Double> lessEarnedManagersList;
    Map<Employee, Double>  moreEarnedManagersList;
    Map<Employee, Integer> longReportingEmployeesList;
}
