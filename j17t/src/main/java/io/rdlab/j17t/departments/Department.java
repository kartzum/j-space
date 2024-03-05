package io.rdlab.j17t.departments;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Department {
    String name;
    List<Employee> employees;

    public String getName() {
        return name;
    }

    public Department setName(String name) {
        this.name = name;
        return this;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Department setEmployees(List<Employee> employees) {
        this.employees = employees;
        return this;
    }
}
