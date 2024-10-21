package org.employee;

import com.opencsv.exceptions.CsvException;
import org.employee.model.Employee;
import org.employee.model.EmployeeManagerMaps;
import org.employee.model.EmployeeReportList;
import org.employee.service.EmployeeReportService;

import java.io.IOException;
import java.util.Map;

public class ReportTest {
    public static void main(String[] args) throws IOException, CsvException {
        EmployeeReportService employeeReportService = new EmployeeReportService();
        EmployeeManagerMaps employeeManagerMaps = employeeReportService.readAndPrepareEmployeesMap("employees.csv");

        EmployeeReportList employeeReportList = employeeReportService.calcManagerSalaries(employeeManagerMaps);
        Map<Employee, Integer> longReportingEmployeesList = employeeReportService.validateReporting(employeeManagerMaps);

        System.out.println(" ######### Managers earn less than they should #########");
        if (employeeReportList.getLessEarnedManagersList().isEmpty()) {
            System.out.println(" No records found ");
        } else {
            employeeReportList.getLessEarnedManagersList().forEach((key, value) -> System.out.println(" Manager " + key + " earns less, the less amount is :: " + value));
        }

        System.out.println(" \n \n ######### Managers earn more than they should #########");
        if (employeeReportList.getMoreEarnedManagersList().isEmpty()) {
            System.out.println(" No records found");
        } else {
            employeeReportList.getMoreEarnedManagersList().forEach((key, value) -> System.out.println(" Manager " + key + " earns more, the more amount is :: " + value));
        }

        System.out.println(" \n \n ######### Employees have a reporting line which is too long #########");
        if (longReportingEmployeesList.isEmpty()) {
            System.out.println(" No records found");
        } else {
            longReportingEmployeesList.forEach((key, value) ->  System.out.println(" Manager "+key+" is long reporting, with more reporting count is :: "+value))  ;
        }
    }
}
