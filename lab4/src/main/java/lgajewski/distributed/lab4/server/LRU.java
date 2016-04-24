package lgajewski.distributed.lab4.server;

import lgajewski.distributed.lab4.impl.UserI;

public class LRU {

    public static int getIndexLRU(UserI[] servants) {
        int index = 0;
        long lruTimestamp = Long.MAX_VALUE;
        for (int i = 0; i < servants.length; i++) {
            if (servants[i] != null) {
                long timestamp = servants[i].getTimestamp();
                if (lruTimestamp > timestamp) {
                    index = i;
                    lruTimestamp = timestamp;
                }
            } else {
                index = i;
                lruTimestamp = 0;
            }
        }

        return index;
    }

}
