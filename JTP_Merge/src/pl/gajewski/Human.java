package pl.gajewski;

import java.util.*;

/**
 *
 * @author Lukasz Gajewski
 * @version 1.0
 *
 */

public class Human implements Comparable<Human> {

    private String name;
    private String surname;
    private String phone;
    private String email;
    private int counter;

    public static String getHeader() { return "Name;Surname;Phone;Email"; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public int getCount() { return counter; }

    public Human (String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.counter = 1;
        this.phone = "";
        this.email = "";
    }

    public Human (String name, String surname, String phone) {
        this.name = name;
        this.surname = surname;
        this.counter = 1;
        this.phone = phone;
        this.email = "";
    }

    public Human (String name, String surname, String phone, String email) {
        this.name = name;
        this.surname = surname;
        this.counter = 1;
        this.phone = phone;
        this.email = email;
    }

    public static boolean areEqual(Human h1, Human h2) {

        if(h1.surname.equalsIgnoreCase(h2.surname) && h1.name.equalsIgnoreCase(h2.name)) {
            boolean phone = (h1.phone.equalsIgnoreCase(h2.phone) || h1.phone.equals("") || h2.phone.equals(""));
            boolean email = (h1.email.equalsIgnoreCase(h2.email) || h2.email.equals("") || h2.email.equals(""));
            return (phone && email);
        }

        return false;

    }

    public static Human merge(Human h1, Human h2) {

        if(h1.phone.equals("")) h1.phone = h2.phone;
        if(h1.email.equals("")) h1.email = h2.email;
        h1.counter += h2.counter;

        return h1;

    }

    @Override
    public String toString() { return name + ";" + surname + ";" + phone + ";" + email; }

    @Override
    public int compareTo(Human h) {
        int compareSurname = surname.compareTo(h.surname);
        if(compareSurname == 0) {
            return name.compareTo(h.name);
        } else {
            return compareSurname;
        }
    }

}
