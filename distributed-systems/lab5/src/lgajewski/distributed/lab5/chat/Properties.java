package lgajewski.distributed.lab5.chat;

import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SuppressWarnings("WeakerAccess")
public class Properties {

    private static final int FD_TIMEOUT = 12000;
    private static final int FD_INTERVAL = 3000;

    public static final String DEFAULT_HOST_PREFIX = "230.0.0.";

    public static String getHost(int roomId) {
        if (roomId < 1 || roomId > 200) {
            throw new IllegalArgumentException("roomId should be between 1-200");
        }

        return DEFAULT_HOST_PREFIX + roomId;
    }

    public static ProtocolStack getProtocolStack(int roomId) throws UnknownHostException {
        InetAddress host = InetAddress.getByName(getHost(roomId));

        return getProtocolStack(host);
    }

    public static ProtocolStack getProtocolStack(InetAddress host) {
        UDP udp = new UDP();

        if (host != null) {
            udp.setValue("mcast_group_addr", host);
        }

        ProtocolStack stack = new ProtocolStack();
        stack.addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE2())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", FD_TIMEOUT).setValue("interval", FD_INTERVAL))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK())
                .addProtocol(new UNICAST2())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());

        return stack;
    }


}
