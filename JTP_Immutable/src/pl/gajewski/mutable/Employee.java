package pl.gajewski.mutable;

/**
 * Created by Lukasz on 2014-04-16.
 */

public class Employee {

    private String name;
    private String surname;
    private int salary;

    public Employee() {
        this.name = "NAME";
        this.surname = "SURNAME";
        this.salary = 0;
    }

    public Employee(String name, String surname, int salary) {
        this.name = name;
        this.surname = surname;
        this.salary = salary;
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public int getSalary() { return salary; }

    public Employee setSalary(int salary) {
        this.salary = salary;
        return this;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }

}
