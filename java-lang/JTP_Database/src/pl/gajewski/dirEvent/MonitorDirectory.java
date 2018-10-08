package pl.gajewski.dirEvent;

/**
 * Created by Lukasz on 07.04.14.
 *
 * Serce kodu - monitor directory. Obserwuje plik na podstawie eventow, usypia
 * co 1.5 sekundy, aby inne programy mialy szanse sie wykonac.
 * Tworzy obiekt FilesFactory, dzieki czemu mozemy przetwarzac pliki.
 * Posiada dwie inne opcje:
 * checkDir - wykonuje akcje na plikach w directory
 * clearDirs - czysci katalogi z plikow, przywraca HTML to pierwotnego stanu
 *
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import pl.gajewski.Main;
import pl.gajewski.WrongFileException;
import pl.gajewski.Useful;
import pl.gajewski.dirEvent.factory.MyFiles;


public class MonitorDirectory implements Runnable {

    private String dirPath;
    private boolean running = true;

    // zmienna odpowiedzialna za glowna petle (obserwacja)
    public void setRunning(boolean running) {
        this.running = running;
    }

    public MonitorDirectory(String dir) {
        this.dirPath = dir;
    }

    @Override
    public void run() {

        // sprawdzamy czy sciezka do obserwacji jest poprawna
        if (!Useful.isDir(dirPath))
            throw new IllegalArgumentException("[Database] Directory " + dirPath + " not found");

        // odpalamy WatchService w celu przechwytywania eventow
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Paths.get(dirPath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("[Database] Following directory: " + dirPath);

            do {

                // ponownie sprawdzamy poprawnosc katalogu
                if (!Useful.isDir(dirPath))
                    throw new IllegalArgumentException("[Database] Directory " + dirPath + " not found");

                // pobieramy eventy
                WatchKey watchKey = watchService.poll();

                if(watchKey != null) {

                    // listujemy po wszystkich pobranych eventach
                    for (WatchEvent<?> event : watchKey.pollEvents()) {

                        WatchEvent.Kind<?> kind = event.kind();

                        // zaczynamy przetwarzanie pliku, gdy powstanie
                        if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                            String fileName = event.context().toString();
                            String filePath = dirPath + fileName;

                            // przetwarzanie pliku, lapiemy wyjatek gdyby cos poszlo nie tak
                            // pozwala nam to na dalsza prace programu, mimo tego ze plik byl niepoprawny
                            try {
                                MyFiles file = FilesFactory.createFile(filePath);
                                file.useFile();
                            } catch (WrongFileException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    watchKey.reset();       // reset eventow, przejscie w tryb ready
                }
                Thread.sleep(1500);     // uspienie watku, pozwalamy na prace innym

            } while(running);

            watchService.close();

            System.out.println("[Database] Stopped to follow " + dirPath);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // metoda listuje pliki z katalogu i wykonuje na nich akcje
    public void checkDir(String dir) throws WrongFileException {

        File fDir = new File(dir);
        if(!Useful.isDir(dir)) throw new WrongFileException("No directory " + dir);

        File[] listOfFiles = fDir.listFiles();

        if (listOfFiles != null) {

            for (File fileFromDir : listOfFiles) {
                MyFiles file = FilesFactory.createFile(fileFromDir.toString());
                file.useFile();
            }

        }
    }

    // metoda usuwa zbedne pliki oraz 'generuje' defaultowy plik HTML
    public void clearDirs() {
        try {
            Useful.deleteDir(Main.bin, false);
            Useful.deleteDir(Main.tempDir, false);
            Useful.deleteDir(Main.dirToSerial, false);
            Useful.deleteDir(Main.dirToFollow, false);

        } catch (WrongFileException e) {
            e.printStackTrace();
        }

        try {
            Files.copy(Paths.get(Main.indexSource), Paths.get(Main.indexTarget), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
