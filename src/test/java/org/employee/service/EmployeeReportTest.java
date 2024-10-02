package org.employee.service;

import com.opencsv.exceptions.CsvException;
import org.employee.model.Employee;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import java.util.List;

public class EmployeeReportTest {
    private EmployeeReport employeeReport;

    @Before
    public void setUp() throws IOException, CsvException {
        employeeReport = new EmployeeReport();
        // Load test CSV file from classpath
        employeeReport.readAndPrepareEmployeesMap("test-employees.csv");
    }

    @Test
    public void testEmployeeMapInitialization() {
        Employee manager = employeeReport.employeeMap.get(123);
        assertNotNull("Manager should not be null", manager);
        assertEquals("Joe", manager.getFirstName());

        Employee subordinate = employeeReport.employeeMap.get(124);
        assertEquals("Martin", subordinate.getFirstName());
        assertEquals("Chekov", subordinate.getLastName());
        assertEquals(45000, subordinate.getSalary(), 0.0);
    }

    @Test
    public void testManagerToEmployeeMapInitialization() {
        List<Employee> subordinates = employeeReport.managerToEmployeeMap.get(123);
        assertNotNull("Subordinates should not be null", subordinates);
        assertEquals(2, subordinates.size());
        
        assertEquals("Martin", subordinates.get(0).getFirstName());
        assertEquals("Bob", subordinates.get(1).getFirstName());
    }

    @Test
    public void testCalcManagerSalaries() {
        employeeReport.calcManagerSalaries();
    }

    @Test
    public void testValidateReporting() {
        employeeReport.validateReporting();
    }

    @Test
    public void testCountManagers() {
        // Test helper method countManagers
        Employee employee = employeeReport.employeeMap.get(305); // Brett Hardleaf
        int managerCount = employeeReport.countManagers(employee);
        assertEquals("Brett Hardleaf should have 3 managers", 3, managerCount);
    }
}
