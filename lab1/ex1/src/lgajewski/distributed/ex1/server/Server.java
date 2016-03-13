package lgajewski.distributed.ex1.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

    private ServerSocket serverSocket;
    private boolean running = false;

    private Bpp bpp;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.running = true;

        this.bpp = new Bpp();

        System.out.println("[server] running...");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void accept() throws IOException {
        try (Socket socket = serverSocket.accept()) {
            socket.setSoTimeout(500);
            System.out.println("[server] new incoming connection");
            final DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            OutputStream outputStream = socket.getOutputStream();

            int bytes[] = new int[8];
            int in, i = 0;
            try {
                while ((in = inputStream.read()) != -1) {
                    bytes[i++] = in;
                }
            } catch (SocketTimeoutException e) {}

            ByteInputStream byteInputStream = new ByteInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(byteInputStream);

            long n = 0;

            switch (i) {
                case 1:
                    n = byteInputStream.read();
                    break;
                case 2:
                    n = dataInputStream.readUnsignedShort();
                    break;
                case 4:
                    n = dataInputStream.readInt();
                    break;
                case 8:
                    n = dataInputStream.readLong();
                    break;
                default:
                    throw new IllegalArgumentException("Wrong number of given bytes");
            }

            System.out.println("\t-> received number: " + n);


            // calculate
            System.out.println("\t-> calculating " + n + "-th decimal of PI");
//            int decimal = (int) (bpp.getDecimal(n) / Math.pow(10, 8));
            int decimal = (int) (n % 10);

            // send
            System.out.println("\t-> sending to the client.. result=" + decimal);
            outputStream.write(decimal);

            outputStream.close();
            inputStream.close();
            socket.close();
        }
    }

    class ByteInputStream extends InputStream {

        private int[] bytes;
        private int it;

        public ByteInputStream(int[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public int read() throws IOException {
            if (it < bytes.length) {
                return bytes[it++];
            } else {
                return -1;
            }
        }
    }

}


