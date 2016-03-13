
package lgajewski.distributed.ex1.server;

public abstract class Config {

    public static final int BUF_SIZE = 256;

    // see: http://java.sun.com/javase/6/docs/api/java/net/MulticastSocket.html
    public static final String MULTICAST_IP = "224.0.0.1";
    public static final int MULTICAST_INBOUND_PORT = 4447;
    public static final int MULTICAST_OUTBOUND_PORT = 4448;

}