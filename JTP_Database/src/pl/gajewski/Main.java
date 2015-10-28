package pl.gajewski;

/**
 * Created by Lukasz on 08.04.14.
 *
 * Klasa Person - zawiera informacje o uzytkowniku.
 *
 */

import pl.gajewski.dirEvent.MonitorDirectory;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static String dirToFollow = ".\\src\\dirFollowed\\";
    public static String dirToSerial = ".\\src\\serFiles\\";
    public static String bin = ".\\src\\bin\\";
    public static String tempDir = ".\\src\\bin_temp\\";
    public static String indexSource = ".\\src\\pl\\html\\index.html";
    public static String indexTarget = ".\\src\\index.html";

    public static void main(String[] args) {

        MonitorDirectory monitor = new MonitorDirectory(dirToFollow);

        System.out.println("[Database] There are actions possible:");
        System.out.println("\t(1) Stop following directory");
        System.out.println("\t(2) Stop to follow, deserialize and generate HTML");
        System.out.println("\t(3) Stop and clear: bin, following dir, ser files, HTML doc");
        System.out.println("\tPlease enter integer value.");

        try {
            monitor.checkDir(dirToFollow);
        } catch (WrongFileException e) {
            e.printStackTrace();
        }

        new Thread(monitor).start();

        Scanner scan = new Scanner(System.in);
        int num = 1;
        try {
            num = scan.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("[Database] Input failed. Setting to default (1).");
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        // wykonujemy akcje w zaleznosci od tego ktora cyfre wybralismy
        switch (num) {
            case 2:
                monitor.setRunning(false);
                try {
                    monitor.checkDir(tempDir);
                    monitor.checkDir(dirToSerial);
                } catch (WrongFileException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                monitor.setRunning(false);
                monitor.clearDirs();
                break;
            default:
                monitor.setRunning(false);
        }

        // czyszczenie zawartosci tempDira, przygotowujemy go pod nastepne uruchomienie
        try {
            Useful.deleteDir(tempDir, false);
        } catch (WrongFileException e) {
            e.printStackTrace();
        }


    }
}
