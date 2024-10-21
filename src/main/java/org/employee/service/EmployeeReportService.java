package org.employee.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.ObjectUtils;
import org.employee.model.Employee;
import org.employee.model.EmployeeManagerMaps;
import org.employee.model.EmployeeReportList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeReportService {

    private static final double CON_20_PERCENT_FACTOR = 1.2;
    private static final double CON_50_PERCENT_FACTOR = 1.5;
    private static final int CON_MAX_REPORTING_LINES = 4;

    // Read the CSV file and construct the Employee objects map and Manager Map who is reporting to them
    public EmployeeManagerMaps readAndPrepareEmployeesMap(String fileName) throws IOException, CsvException {
        EmployeeManagerMaps employeeManagerMaps = null;
        Map<Integer, Employee> employeeMap = new HashMap<>();
        Map<Integer, List<Employee>> managerToEmployeeMap = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        try (CSVReader reader = new CSVReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)))) {
            List<String[]> allLines = reader.readAll();
            if (allLines.size() < 1000) {
                for (int i = 1; i < allLines.size(); i++) {
                    String[] data = allLines.get(i);

                    int id = Integer.parseInt(data[0]);
                    String firstName = data[1];
                    String lastName = data[2];
                    double salary = Double.parseDouble(data[3]);
                    Integer managerId = data[4].isEmpty() ? null : Integer.parseInt(data[4]);

                    Employee employee = new Employee(id, firstName, lastName, salary, managerId);
                    employeeMap.put(id, employee); // Employee Map

                    // Managers map with reporting employees
                    if (managerId != null) {
                        if (managerToEmployeeMap.containsKey(managerId)) {
                            List<Employee> employeeList = managerToEmployeeMap.get(managerId);
                            employeeList.add(employee);
                        } else {
                            List<Employee> employeeList = new ArrayList<>(0);
                            employeeList.add(employee);
                            managerToEmployeeMap.put(managerId, employeeList);
                        }
                    }
                }
                employeeManagerMaps = new EmployeeManagerMaps(employeeMap, managerToEmployeeMap);
            } else {
                System.out.println("Number of rows can't exceed 1000");
            }
        }

        return employeeManagerMaps;
    }

    public EmployeeReportList calcManagerSalaries(EmployeeManagerMaps employeeManagerMaps) {
        double minSalary = 0.0;
        double maxSalary = 0.0;
        Map<Employee, Double> lessEarnedEmployeesList = new HashMap<>(0);
        Map<Employee, Double> moreEarnedEmployeesList = new HashMap<>(0);
        EmployeeReportList employeeReportList = new EmployeeReportList();
        if (ObjectUtils.isNotEmpty(employeeManagerMaps) && null != employeeManagerMaps.getEmployeeMap() && employeeManagerMaps.getEmployeeMap().size() > 0
                && null != employeeManagerMaps.getManagerToEmployeeMap() && employeeManagerMaps.getManagerToEmployeeMap().size() > 0) {

            for (Map.Entry<Integer, List<Employee>> managerEntry : employeeManagerMaps.getManagerToEmployeeMap().entrySet()) {
                Employee manager = employeeManagerMaps.getEmployeeMap().get(managerEntry.getKey());
                List<Employee> reportingEmployees = managerEntry.getValue();

                double avgSalary = reportingEmployees.stream()
                        .mapToDouble(Employee::getSalary)
                        .average()
                        .orElse(0);

                //System.out.println("avgSalary :: " + avgSalary);
                minSalary = 0.0;
                maxSalary = 0.0;
                minSalary = avgSalary * CON_20_PERCENT_FACTOR;
                maxSalary = avgSalary * CON_50_PERCENT_FACTOR;

                if (manager.getSalary() < minSalary || manager.getSalary() > maxSalary) {
                    //System.out.println("Manager " + manager + " earns out of range ("
                    //       + minSalary + " - " + maxSalary + ")");

                    if (manager.getSalary() <= minSalary) { //managers earn less than they should
                        //System.out.println("Manager " + manager + " earns  " + (minSalary - manager.getSalary()) + " less as supposed to 22");
                        lessEarnedEmployeesList.put(manager, (minSalary - manager.getSalary()));
                    } else if (manager.getSalary() > maxSalary) { //managers earn more than they should
                        moreEarnedEmployeesList.put(manager, (manager.getSalary() - maxSalary));
                        //System.out.println("Manager " + manager + " earns  " + (manager.getSalary() - maxSalary) + " more as supposed to 22");
                    }
                }
            }
            employeeReportList.setLessEarnedManagersList(lessEarnedEmployeesList);
            employeeReportList.setMoreEarnedManagersList(moreEarnedEmployeesList);
        }
        return employeeReportList;
    }

    //employees have a reporting line which is too long
    public Map<Employee, Integer> validateReporting(EmployeeManagerMaps employeeManagerMaps) {
        Map<Employee, Integer> longReportingEmployeesList = new HashMap<>(0);
        int reportingCount = 0;
        if (ObjectUtils.isNotEmpty(employeeManagerMaps) && null != employeeManagerMaps.getEmployeeMap()) {
            for (Employee employee : employeeManagerMaps.getEmployeeMap().values()) {
                reportingCount = countManagers(employee, employeeManagerMaps);
                if (reportingCount > CON_MAX_REPORTING_LINES) {
                    //System.out.println("Employee " + employee + " has more than 4 managers above. 22");
                    longReportingEmployeesList.put(employee, (reportingCount - CON_MAX_REPORTING_LINES));
                }
            }
        }
        return longReportingEmployeesList;
    }

    public int countManagers(Employee employee, EmployeeManagerMaps employeeManagerMaps) {
        Map<Integer, Employee> employeeMap = employeeManagerMaps.getEmployeeMap();
        int count = 0;
        Integer managerId = employee.getManagerId();

        while (managerId != null) {
            ++count;
            managerId = employeeMap.get(managerId).getManagerId();
        }
        //System.out.println("employee :: "+employee.toString()+ " :: no of reporting :: "+count);
        return count;
    }
}
