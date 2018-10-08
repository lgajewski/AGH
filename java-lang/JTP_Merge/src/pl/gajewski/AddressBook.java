package pl.gajewski;

/**
 *
 * @author Lukasz Gajewski
 * @version 1.0
 *
 */

import java.io.*;
import java.util.*;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/** my exception */
class WrongFileException extends Exception {
    public WrongFileException(String message){
        super(message);
    }
}

public class AddressBook {

    public static Map<Integer, Human> load(String path, char separator) throws WrongFileException, IOException {

        Map<Integer, Human> humans = new HashMap<Integer, Human>();

        File folder = new File(path);
        if(!folder.isDirectory()) throw new WrongFileException("Wrong Directory.");

        // lista plikow w folderze path
        File[] listOfFiles = folder.listFiles();

        int key = 0;
        for (File listOfFile : listOfFiles) {

            // element listy musi byc plikiem z rozszerzeniem cv
            if (listOfFile.isFile() && listOfFile.getName().endsWith(".csv")) {

                String file = listOfFile.getName();
                CSVReader reader = new CSVReader(new FileReader(path + "/" + file), separator);
                List<String[]> myEntries = reader.readAll();

                String[] header = myEntries.get(0);
                if(header.length != 4) throw new WrongFileException("Wrong header in " + listOfFile.getName());
                String head = header[0] + ";" + header[1] + ";" + header[2] + ";" + header[3];
                if(!head.equalsIgnoreCase(Human.getHeader())) throw new WrongFileException("Wrong header in " + listOfFile.getName());

                for (String[] addObject : myEntries) {

                    Human addHuman;
                    if(addObject.length <= 1) throw new ArrayIndexOutOfBoundsException("Input error, not enough data.");
                    else if(addObject.length == 2) addHuman = new Human(addObject[0], addObject[1]);
                    else if(addObject.length == 3) addHuman = new Human(addObject[0], addObject[1], addObject[2]);
                    else if(addObject.length == 4) addHuman = new Human(addObject[0], addObject[1], addObject[2], addObject[3]);
                    else throw new ArrayIndexOutOfBoundsException("Input error. Too much data.");

                    // dodajemy do listy ludzi
                    boolean check = true;
                    for (Map.Entry<Integer, Human> compareHuman : humans.entrySet()) {
                        if (check && Human.areEqual(addHuman, compareHuman.getValue())) {
                            addHuman = Human.merge(addHuman, compareHuman.getValue());
                            humans.put(compareHuman.getKey(), addHuman);
                            check = false;
                        }
                    }

                    if(check) {
                        humans.put(key, addHuman);
                    }

                    key++;

                }

            } else throw new WrongFileException("Wrong CSV file: " + listOfFile.getName());

        }

        humans.remove(0);           // usuwamy header
        return humans;

    }


    public static void save(List<Human> book, String destination, char separator) throws IOException {

        CSVWriter writer = new CSVWriter(new FileWriter(destination), separator, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
        writer.writeNext(Human.getHeader().split(String.valueOf(separator)));         // write a header to the file

        for(Human object: book) {
            String[] entries = object.toString().split(String.valueOf(separator));
            writer.writeNext(entries);
        }
        writer.close();

        if(!writer.checkError()) {
            System.out.println("[CSVWriter] Success! Merged all CSVFiles to " + destination);
        } else {
            System.out.println("[CSVWriter] Writing to " + destination + " failed");
        }

    }

    public static void count(List<Human> book, String name, String surname) {
        Human toCompare = new Human(name, surname);

        boolean check = false;

        for (Human object : book) {
            if (Human.areEqual(object, toCompare)) {
                System.out.println("[AddressBook] " + name + " " + surname + " occured " + object.getCount() + " times.");
                check = true;
            }
        }

        if(!check) System.out.println("[AddressBook] There is no such person named " + name + " " + surname);

    }

}
