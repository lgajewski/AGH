package pl.gajewski.zad2;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Consumer extends Thread {

    private int multiple;
    private Buffer box;

    public Consumer(Buffer box, int multiple) throws IllegalArgumentException {
        if(multiple <= 0) {
            throw new IllegalArgumentException("Multiple has to be positive");
        }

        this.box = box;
        this.multiple = multiple;
    }

    @Override
    public void run() {
        for(int i=0; i<multiple; i++) {
            try {
                int number = box.get();
//                System.out.println("Consumer get: " + number);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
