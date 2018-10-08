package pl.gajewski.zad4.b;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Consumer extends Thread {

    private int multiple;
    private Buffer buffer;
    private final int M;

    public Consumer(Buffer box, int multiple, final int M) throws IllegalArgumentException {
        if(multiple <= 0 || M <= 0) {
            throw new IllegalArgumentException("Multiple / M have to be positive");
        }

        this.M = M;
        this.buffer = box;
        this.multiple = multiple;
    }

    @Override
    public void run() {
        for(int i=0; i<multiple; i++) {
            try {
                double rand = Math.random();
                int times = (int) (rand * M + 1);
                int[] numbers = buffer.get(times);

                StringBuilder s = new StringBuilder();
                s.append("Consumer get: ");
                for (int number : numbers) s.append("(").append(number).append(") ");

                System.out.println(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
