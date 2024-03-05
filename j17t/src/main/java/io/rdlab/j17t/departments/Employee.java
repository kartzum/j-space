package io.rdlab.j17t.departments;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class Employee {

    String fullName;
    BigDecimal salary;

    public String getFullName() {
        return fullName;
    }

    public Employee setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Employee setSalary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }

    public Double getSalaryAsDouble() {
        return salary.doubleValue();
    }
}
