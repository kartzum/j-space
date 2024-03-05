package io.rdlab.j17t.departments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class DepartmentsTest {
    @Test
    public void findAllEmployeesWithSecondHighestSalaryUsingStreams() {
        var dep1 = new Department().setName("HR")
                .setEmployees(
                        List.of(
                                new Employee().setFullName("Employee 11").setSalary(new BigDecimal("1000.30")),
                                new Employee().setFullName("Employee 12").setSalary(new BigDecimal("1500.30")),
                                new Employee().setFullName("Employee 13").setSalary(new BigDecimal("1800.30")),
                                new Employee().setFullName("Employee 14").setSalary(new BigDecimal("1700.30")),
                                new Employee().setFullName("Employee 15").setSalary(new BigDecimal("1900.30"))
                        ));


        var dep2 = new Department().setName("Payroll")
                .setEmployees(
                        List.of(
                                new Employee().setFullName("Employee 21").setSalary(new BigDecimal("1000.30")),
                                new Employee().setFullName("Employee 22").setSalary(new BigDecimal("1500.30")),
                                new Employee().setFullName("Employee 23").setSalary(new BigDecimal("1500.30"))
                        ));


        var dep3 = new Department().setName("IT")
                .setEmployees(
                        List.of(
                                new Employee().setFullName("Employee 31").setSalary(new BigDecimal("1800")),
                                new Employee().setFullName("Employee 33").setSalary(new BigDecimal("1800")),
                                new Employee().setFullName("Employee 34").setSalary(new BigDecimal("1800"))
                        ));

        var departments = List.of(dep1, dep2, dep3);

        List<Employee> res = departments.stream().map(department ->
                        department.employees.stream()
                                .collect( // Remove duplicates by salary.
                                        Collectors.collectingAndThen(
                                                Collectors.toCollection(
                                                        () -> new TreeSet<>(Comparator.comparingDouble(Employee::getSalaryAsDouble))),
                                                Collection::stream
                                        )
                                )
                                .sorted(Comparator.comparing(Employee::getSalary).reversed()) // Sort and get second element.
                                .skip(1)
                                .limit(1)
                                .findFirst()
                )
                .filter(Optional::isPresent) // Extract values from stream of optionals.
                .map(Optional::get)
                .toList();

        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals(new BigDecimal("1800.30"), res.get(0).getSalary());
        Assertions.assertEquals(new BigDecimal("1000.30"), res.get(1).getSalary());
    }
}
