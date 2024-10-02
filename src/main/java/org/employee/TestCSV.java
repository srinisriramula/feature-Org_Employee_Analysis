package org.employee;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class TestCSV {
    public static void main(String[] args) throws CsvException {
        //CSVReader reader = null;
        ClassLoader classLoader = TestCSV .class.getClassLoader();
            try (CSVReader reader = new CSVReader(new InputStreamReader(classLoader.getResourceAsStream("employees.csv")))) {
                // Skip the header
                List<String[]> allLines = reader.readAll();

                // Iterate through the lines (skipping the first one)
                for (int i = 1; i < allLines.size(); i++) {
                    String[] data = allLines.get(i);
                    System.out.println(data.length + " ");
                    Arrays.asList(data).stream().forEach(System.out :: println);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
