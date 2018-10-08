package pl.gajewski.dirEvent.factory;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa InfoFile obsluguje pliki .info, tworzy folder uzytkownia
 * oraz serializuje obiekty do dirToSerialize
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

import pl.gajewski.dirEvent.Person;
import pl.gajewski.WrongFileException;
import pl.gajewski.Main;
import pl.gajewski.Useful;

public class InfoFile implements MyFiles {

    private String path;

    public InfoFile(String path) {
        this.path = path;
    }

@Override
public void useFile() throws WrongFileException {

    final int TEMPLATE_ROWS = 5;        // liczba linii we wzorcu
    String bin = Main.bin;
    String temp = Main.dirToSerial;

    String fileName = Useful.getFileName(path);

    File fFile = new File(path);        // tworzy obiekt file do obecnego pliku w celu odczytu

    // deklaracja zmiennych do serializacji obiektu Person
    String name = "", surname = "";
    boolean imageFile = false, descriptionFile = false;

    try {
        Scanner scan = new Scanner(fFile);
        String toToken = "";
        int rows = 0;

        // wczytanie calej zawartosci pliku do toToken
        while(scan.hasNextLine()) {
            toToken += scan.nextLine();
            rows++;
        }

        scan.close();

        // rzuca wyjatkiem w przypadku niepoprawnej ilosci linii w pliku
        if(rows != TEMPLATE_ROWS) throw new WrongFileException("Can't load file: " +
            path + ". Incorrect number of rows.");

        // tokenize string, wczytywanie kluczy i wartosci oraz przyporzadkowanie do zmiennych
        StringTokenizer st = new StringTokenizer(toToken, "=;");
        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            String value = st.nextToken();

            if (key.equals("template") && value.equals("MyTemplate"))
                System.out.println("Loading file " + path + " based on 'MyTemplate'.");
            else if(key.equals("name")) name = value;
            else if(key.equals("surname")) surname = value;
            else if(key.equals("imageFile")) imageFile = Boolean.valueOf(value);
            else if(key.equals("descriptionFile")) descriptionFile = Boolean.valueOf(value);
            else throw new WrongFileException("Can't load file: " + path +
                ". Wrong key in the file.");

            rows--;
        }

        // rzuca wyjatkiem w przypadku gdy wczytano niepoprawna ilosc danych
        if(rows != 0) throw new WrongFileException("Can't load file: " + path
                + ". Wrong number of keys in the file.");

        File fDir = new File(bin + fileName);

        // sprawdzenie czy katalog uzytkownika istnieje
        // gdy nie istnieje to go tworzymy
        if (!fDir.exists() && fDir.mkdir()) {

            System.out.println("Directory " + bin + fileName + " created!");

            Person person = new Person(name, surname, bin + fileName, imageFile, descriptionFile);

            // serializacja obiektu Person do dirToSerialize
            try {
                FileOutputStream fileOut = new FileOutputStream(temp + fileName + ".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(person);
                out.close();
                fileOut.close();
                System.out.println("Serialized data is saved in " + temp + fileName + ".ser");
            } catch(IOException e) {
                e.printStackTrace();
            }

        } else {
            // rzuca wyjatkiem gdy istnieje juz taki plik
            if(fDir.isDirectory())
                System.out.println("Directory already exists: " + bin + fileName);
            else
                throw new WrongFileException("Failed to create directory: " + bin + fileName +
                        ". Error occurred. There is another file named like InfoFile.");
        }

    // lapie wyjatek w przypadku gdy nie znajdzie scanner nie znajdzie pliku
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }


}
}