package pl.gajewski.zad2;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Producer extends Thread {

    private int multiple;
    private Buffer box;

    public Producer(Buffer box, int multiple) throws IllegalArgumentException {
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
                int val = (int) (Math.random() * 100);
                box.put(val);
//                System.out.println("Producer put: " + val);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
