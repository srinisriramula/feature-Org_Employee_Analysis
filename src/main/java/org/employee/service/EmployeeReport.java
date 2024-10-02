package org.employee.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.employee.model.Employee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeReport {
    public Map<Integer, Employee> employeeMap = new HashMap<>();
    public Map<Integer, List<Employee>> managerToEmployeeMap = new HashMap<>();
    private static double CON_20_PERCENT_FACTOR = 1.2;
    private static double CON_50_PERCENT_FACTOR = 1.5;

    // read the csv file and construct the Employee objects map and Manager Map who is reporting to them
    public void readAndPrepareEmployeesMap(String fileName) throws IOException, CsvException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (CSVReader reader = new CSVReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)))) {
            // Skip the header
            List<String[]> allLines = reader.readAll();

            // Iterate through the lines
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
                        //managerToSubordinates.put(managerId, employeeList);
                    } else {
                        List<Employee> employeeList = new ArrayList<>(0);
                        employeeList.add(employee);
                        managerToEmployeeMap.put(managerId, employeeList);
                    }
                }
            }
        }
    }

    public void calcManagerSalaries() {
        for (Map.Entry<Integer, List<Employee>> managerEntry : managerToEmployeeMap.entrySet()) {
            Employee manager = employeeMap.get(managerEntry.getKey());
            List<Employee> reportingEmployees = managerEntry.getValue();

            double avgSalary = reportingEmployees.stream()
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0);

            //System.out.println("avgSalary :: " + avgSalary);
            double minSalary = avgSalary * CON_20_PERCENT_FACTOR;
            double maxSalary = avgSalary * CON_50_PERCENT_FACTOR;

            if (manager.getSalary() < minSalary || manager.getSalary() > maxSalary) {
                System.out.println("Manager " + manager + " earns out of range ("
                        + minSalary + " - " + maxSalary + ")");

                if (manager.getSalary() < minSalary) { //managers earn less than they should
                    System.out.println("Manager " + manager + " earns  " + (minSalary - manager.getSalary()) + " less as supposed to");
                } else if (manager.getSalary() > maxSalary) { //managers earn more than they should
                    System.out.println("Manager " + manager + " earns  " + (manager.getSalary() - maxSalary) + " more as supposed to");
                }
            }
        }
    }

    //employees have a reporting line which is too long
    public void validateReporting() {
        for (Employee employee : employeeMap.values()) {
            if (countManagers(employee) > 4) {
                System.out.println("Employee " + employee + " has more than 4 managers above.");
            }
        }
    }

    public int countManagers(Employee employee) {
        int count = 0;
        Integer managerId = employee.getManagerId();

        while (managerId != null) {
            ++count;
            managerId = employeeMap.get(managerId).getManagerId();
        }
        //System.out.println("employee :: "+employee.toString()+ " :: no of reporting :: "+count);
        return count;
    }

    public static void main(String[] args) throws IOException, CsvException {
        EmployeeReport employeeReport = new EmployeeReport();
        employeeReport.readAndPrepareEmployeesMap("employees.csv");

        employeeReport.calcManagerSalaries();
        employeeReport.validateReporting();
    }
}
