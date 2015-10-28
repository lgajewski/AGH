package pl.gajewski.mutable;

import org.apache.log4j.Logger;
import pl.gajewski.Language;
import pl.gajewski.Main;

/**
 * Created by Lukasz on 16.04.14.
 */

public class Mutability {

    static Logger log = Logger.getLogger(Mutability.class.getName());

    public static void classInfo() {

        Employee employee = new Employee("Jan", "Kowalski", 2100);
        Employee tempEmployee = employee;
        System.out.println(Language.Lang.MSG_CREATEOBJ.getMsg() + employee + ", " + employee.getSalary());
        employee = employee.setSalary(1700);
        System.out.println(Language.Lang.MSG_CHANGEOBJ.getMsg() + employee + ", " + employee.getSalary());
        log.info(isEqual(employee, tempEmployee));
        System.out.println(Language.Lang.MSG_EMPLOYEE_EQUAL.getMsg());

        System.out.println();
        Boss boss = new Boss("Maciek", "Szef", 4900, 1100);
        Boss tempBoss = boss;
        System.out.println(Language.Lang.MSG_CREATEOBJ.getMsg() + boss + ", " + boss.getSalary() + ", " + boss.getBonus());
        boss = boss.setSalary(4400);
        System.out.println(Language.Lang.MSG_CHANGEOBJ.getMsg() + boss + ", " + boss.getSalary() + ", " + boss.getBonus());
        log.info(isEqual(boss, tempBoss));
        System.out.println(Language.Lang.MSG_BOSS_EQUAL.getMsg() + "\n");

    }

    public static void objectInfo() {

        String myString = new String( "example" );
        String myCache = myString;
        System.out.println(Language.Lang.MSG_CREATEOBJ.getMsg() + myString);
        log.info(isEqual(myCache, myString));
        myString = myString.toUpperCase();
        System.out.println(Language.Lang.MSG_CHANGEOBJ.getMsg() + myString);
        log.info(isEqual(myCache, myString));
        System.out.println(Language.Lang.MSG_STRING_EQUAL.getMsg() + "\n");


    }

    public static String isEqual(Object obj1, Object obj2) {
        boolean equal = obj1.equals(obj2);
        boolean same = ( obj1 == obj2 );
        return Language.Lang.MSG_COMPARE.getMsg() + "'" + obj1.toString() + "' and '"
                + obj2.toString() + "'...\r\n\t\t\t\t" +
                Language.Lang.MSG_EQUAL.getMsg() + " " + equal + "\r\n\t\t\t\t" +
                Language.Lang.MSG_SAME.getMsg() + " " + same;
    }

}
