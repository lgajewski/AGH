package pl.gajewski.dirEvent;

import java.io.Serializable;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa Person - zawiera informacje o uzytkowniku.
 *
 */

public class Person implements Serializable {

    private String name = "";
    private String surname = "";
    private String baseDir = "";
    private boolean imageFile = false;
    private boolean descriptionFile = false;

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getBaseDir() { return baseDir; }
    public boolean getImageFile() { return imageFile; }
    public boolean getDescriptionFile() { return descriptionFile; }

    public Person(String name, String surname, String baseDir, boolean imageFile, boolean descriptionFile) {
        this.name = name;
        this.surname = surname;
        this.baseDir = baseDir;
        this.imageFile = imageFile;
        this.descriptionFile = descriptionFile;
    }

    @Override
    public String toString() {
        String result = "[Person] My name is " + name + " " + surname + ". I live in " + baseDir;
        if (imageFile) result += ". I've got an image of my";
        if (descriptionFile) result += ". And I want to say something about me.";
        return result;
    }


}
