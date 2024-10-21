package org.employee.service;

import com.opencsv.exceptions.CsvException;
import org.employee.model.Employee;
import org.employee.model.EmployeeManagerMaps;
import org.employee.model.EmployeeReportList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeReportServiceTest {

    private EmployeeReportService employeeReportService;
    private EmployeeManagerMaps employeeManagerMaps;

    @BeforeEach
    void setUp() {
        employeeReportService = new EmployeeReportService();
        String fileName = "test-employees.csv";
        try {
            employeeManagerMaps = employeeReportService.readAndPrepareEmployeesMap(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadAndPrepareEmployeesMap() throws IOException, CsvException {

        assertNotNull(employeeManagerMaps);
        assertNotNull(employeeManagerMaps.getEmployeeMap());
        assertNotNull(employeeManagerMaps.getManagerToEmployeeMap());
        assertFalse(employeeManagerMaps.getEmployeeMap().isEmpty());
        assertFalse(employeeManagerMaps.getManagerToEmployeeMap().isEmpty());
    }

    @Test
    void testCalcManagerSalaries() {

        EmployeeReportList reportList = employeeReportService.calcManagerSalaries(employeeManagerMaps);
        assertNotNull(reportList);
        assertFalse(reportList.getLessEarnedManagersList().isEmpty());
        assertTrue(reportList.getMoreEarnedManagersList().isEmpty());
    }

    @Test
    void testValidateReporting() {

        Map<Employee, Integer> longReportingList = employeeReportService.validateReporting(employeeManagerMaps);

        assertNotNull(longReportingList);
        assertFalse(longReportingList.isEmpty());
        assertEquals(1, longReportingList.size());
    }

    @Test
    void testCountManagers() {
        int managerCount = employeeReportService.countManagers(employeeManagerMaps.getEmployeeMap().get(305), employeeManagerMaps);
        assertEquals(5, managerCount);
    }
}
