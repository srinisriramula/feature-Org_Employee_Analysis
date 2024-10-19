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

        System.out.println(" ################################################################### ");
        System.out.println(" managers earn less than they should ");
        System.out.println(" ################################################################### ");
        employeeReportList.getLessEarnedManagersList().forEach((key, value) ->  System.out.println(" Manager "+key+" earns less amount :: "+value)) ;

        System.out.println(" \n \n ################################################################### ");
        System.out.println(" managers earn more than they should ");
        System.out.println(" ################################################################### ");
        employeeReportList.getMoreEarnedManagersList().forEach((key, value) ->  System.out.println(" Manager "+key+" earns less amount :: "+value))  ;

        Map<Employee, Integer> longReportingEmployeesList = employeeReportService.validateReporting(employeeManagerMaps);

        System.out.println(" \n \n ################################################################### ");
        System.out.println("  employees have a reporting line which is too long ");
        System.out.println(" ################################################################### ");
        longReportingEmployeesList.forEach((key, value) ->  System.out.println(" Manager "+key+" is long reporting, with reporting count :: "+value))  ;

        if (null == longReportingEmployeesList || 0 == longReportingEmployeesList.size()){
            System.out.println(" No employees are reporting line is too long ");
        }
    }
}
