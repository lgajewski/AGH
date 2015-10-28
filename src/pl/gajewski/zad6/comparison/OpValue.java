package pl.gajewski.zad6.comparison;

/**
 * @author Gajo
 *         05/05/2015
 */

public class OpValue {

    private Long time = 0l;
    private int count = 0;

    public void addTime(Long time) {
        this.time += time;
        this.count++;
    }

    public void addTime(Long time, int count) {
        this.time += time;
        this.count += count;
    }

    public Long getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }
}
