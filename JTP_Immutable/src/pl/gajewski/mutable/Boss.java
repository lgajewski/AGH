package pl.gajewski.mutable;

/**
 * Created by Lukasz on 2014-04-16.
 */

public final class Boss extends Employee {

    private int bonus;

    public Boss(String name, String surname, int salary, int bonus) {
        super(name, surname, salary);
        this.bonus = bonus;
    }

    public int getBonus() { return bonus; }

    @Override
    public Boss setSalary(int salary) {
        String newName = new String(this.getName());
        String newSurname = new String(this.getSurname());
        int newBonus = this.bonus;
        return new Boss(newName, newSurname, salary, newBonus);
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getSurname();
    }

}
