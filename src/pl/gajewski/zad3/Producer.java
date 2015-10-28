package pl.gajewski.zad3;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Producer extends Thread {

    private int multiple;
    private Buffer buffer;

    public Producer(Buffer buffer, int multiple) throws IllegalArgumentException {
        if(multiple <= 0) {
            throw new IllegalArgumentException("Multiple has to be positive");
        }

        this.buffer = buffer;
        this.multiple = multiple;
    }

    @Override
    public void run() {
        for(int i=0; i<multiple; i++) {
            try {
                int val = (int) (Math.random() * 100);
                buffer.put(val);
//                System.out.println("Producer put: " + val);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
