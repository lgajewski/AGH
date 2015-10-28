package pl.gajewski;

/**
 *
 * @author Lukasz Gajewski
 * @version 1.0
 *
 * @see AddressBook
 * @see Human
 *
 */

import java.util.*;

public class Main {

    public static void main(String[] args) {

        /** VARIABLES */
        final String SOURCE = "src/CSVFiles";
        final String DESTINATION = "src/CSVResult/book.csv";
        final char SEPARATOR = ';';

        try {

            /** loading book */
            Map<Integer, Human> bookWithKeys = AddressBook.load(SOURCE, SEPARATOR);

            /** sorting humans */
            ArrayList<Human> book = new ArrayList(bookWithKeys.values());
            Collections.sort(book);

            /** saving merged to file */
            AddressBook.save(book, DESTINATION, SEPARATOR);

            /** interactive part */
            Scanner input = new Scanner(System.in);
            System.out.println("\n[AddressBook] Want to know how many people have you in book? (Y/N)");
            String check = input.nextLine();
            if(check.equalsIgnoreCase("Y")) {
                System.out.println("[AddressBook] Enter your name: ");
                String name = input.nextLine();
                System.out.println("[AddressBook] Enter you surname: ");
                String surname = input.nextLine();
                AddressBook.count(book, name, surname);
            } else {
                System.out.println("[AddressBook] Exit ...");
            }

            /** catching exceptions and printing a message*/
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
