package pl.gajewski.zad5.philosopher;

/**
 * @author Gajo
 *         20/04/2015
 */

public class Fork {

    private int id;
    private Object requester;
    private Object holder;

    public Fork(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Fork #"+id;
    }

    public void hold(Object object) {
        if(holder != null) throw new IllegalStateException("[Fork] Can't hold() fork, it's currently held");
        if(requester != object) throw new IllegalStateException("[Fork] Have to request for fork first");
        holder = object;
    }

    public void release() {
        if(holder == null) throw new IllegalStateException("[Fork] Can't release() fork, it's currently released");
        holder = null;
        requester = null;
    }

    public void request(Object object) {
        if(requester != null) throw new IllegalStateException("[Fork] Can't request(), it's currently requested");
        this.requester = object;
    }

    public boolean isRequested() {
        return requester != null;
    }

    public void deleteReq() {
        requester = null;
    }
}
