package pl.gajewski.zad4.a;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Producer extends Thread {

    private Buffer buffer;
    private int multiple;

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

                System.out.println("Producer put: " + val);
//                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
