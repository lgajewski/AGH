package pl.gajewski.zad4.b;

/**
 * @author Gajo
 *         18/03/2015
 */

public class Producer extends Thread {

    private int multiple;
    private Buffer buffer;
    private final int M;

    public Producer(Buffer buffer, int multiple, final int M) throws IllegalArgumentException {
        if(multiple <= 0 || M <= 0) {
            throw new IllegalArgumentException("Multiple / M have to be positive");
        }

        this.M = M;
        this.buffer = buffer;
        this.multiple = multiple;
    }

    @Override
    public void run() {
        for(int i=0; i<multiple; i++) {
            try {
                double rand = Math.random();
                int val = (int) (rand * 100);
                int times = (int) (rand * M + 1);
                buffer.put(val, times);

                StringBuilder s = new StringBuilder();
                s.append("Producer put: ");
                for(int j=0; j<times; j++) s.append("(").append(val).append(") ");

                System.out.println(s);

                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
