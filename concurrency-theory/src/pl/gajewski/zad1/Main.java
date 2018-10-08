package pl.gajewski.zad1;

public class Main {

    private static int MULTIPLE = 20;
    private static int INITIAL_VALUE = 0;

    public static void main(String[] args) throws InterruptedException {

        // compare unsafe and safe execution time
        TimeCompare timeCompare = new TimeCompare(MULTIPLE, INITIAL_VALUE);
        timeCompare.compare();

    }
}
