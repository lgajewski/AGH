package lgajewski.distributed.ex1.server;

public class Main {

    public static void main(String[] args) {
        try {
            new ServerThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}