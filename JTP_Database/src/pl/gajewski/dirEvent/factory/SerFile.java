package pl.gajewski.dirEvent.factory;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa SerFile obsluguje pliki z rozszerzeniem .ser
 * Deserializuje je, sprawdza czy istnieja katalogi uzytkownikow
 * oraz generuje plik HTML.
 *
 */

import pl.gajewski.Main;
import pl.gajewski.WrongFileException;
import pl.gajewski.dirEvent.Person;

import java.io.*;

public class SerFile implements MyFiles {

    private String path;

    public SerFile(String path) {
        this.path = path;
    }

    @Override
    public void useFile() throws WrongFileException {

        Person person = null;
        String indexHTML = Main.indexTarget;

        try {
            // deserializacja, wczytywanie obiektow typu Person
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            person = (Person) in.readObject();

            in.close();
            fileIn.close();

            System.out.println("[Person] Object deserialized successfully: " + person.getName() + " " + person.getSurname());

            // wczytywanie folderu zdeserializowanego obiektu
            File fDir = new File(person.getBaseDir());
            int i = person.getBaseDir().lastIndexOf('\\');

            String fileName = person.getBaseDir().substring(i+1);
            String jpgPath = "./pl/html/images/noimg.jpg";
            String txt = "";

            // sprawdzanie poprawnosci katalogu uzytkownika
            if(fDir.exists() && fDir.isDirectory()) {
                // sprawdzanie poprawnosci istnienia pliku jpg
                if(person.getImageFile()) {
                    File jpgFile = new File(person.getBaseDir() + "\\" + fileName + ".jpg");
                    if(!jpgFile.exists())
                        throw new WrongFileException("[Person] There is no JPG file in directory " + person.getBaseDir());
                    jpgPath = person.getBaseDir().substring(6) + "\\" + fileName + ".jpg";
                    jpgPath = jpgPath.replace('\\', '/');
                }
                // sprawdzanie poprawnosci istnienia pliku txt
                if(person.getDescriptionFile()) {
                    File descrFile = new File(person.getBaseDir() + "\\" + fileName + ".txt");
                if(!descrFile.exists())
                    throw new WrongFileException("[Person] There is no TXT file in directory " + person.getBaseDir());

                // wczytujemy zawartosc pliku txt
                BufferedReader readerTXT = new BufferedReader(new FileReader(descrFile.toString()));
                String lineTXT = "";
                while((lineTXT = readerTXT.readLine()) != null) txt += lineTXT;
                readerTXT.close();
                }
            } else {
                throw new WrongFileException("[Person] There is no directory " + person.getBaseDir());
            }

            // wczytujemy plik HTML
            BufferedReader reader = new BufferedReader(new FileReader(indexHTML));
            String line = "", oldtext = "";
            while((line = reader.readLine()) != null)
            {
                oldtext += line + "\r\n";
            }
            reader.close();

            // dodajemy do pliku nowa linie
            String toReplace = "<li><a href=\"javascript: change('" + jpgPath + "', '" + person.getName() + " " + person.getSurname() +
                    "', '" + txt + "');\">" + fileName + "</a></li>\r\n\t\t\t\t\t\t<!-- MENU_BAR -->";

            String newtext = oldtext.replaceAll("<!-- MENU_BAR -->", toReplace);

            // zapis do pliku HTML
            FileWriter writer = new FileWriter(indexHTML);
            writer.write(newtext);
            writer.close();

            System.out.println("[Database] Created HTML Document in " + indexHTML);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
