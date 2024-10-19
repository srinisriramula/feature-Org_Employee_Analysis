package org.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class EmployeeManagerMaps {
    private Map<Integer, Employee> employeeMap;
    private Map<Integer, List<Employee>> managerToEmployeeMap;
}
