package lgajewski.distributed.ex2.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private File dir;
    private FileDuplicatePolicy policy;

    private ServerSocket serverSocket;

    private boolean running = false;

    public Server(String dirPath, int port, FileDuplicatePolicy policy) throws IOException {
        this.policy = policy;

        this.serverSocket = new ServerSocket(port);

        // save directory
        this.dir = initDirectory(dirPath);
        this.dir.deleteOnExit();

        this.running = true;

        System.out.println("[server] running...");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private File initDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Can't create directory");
            }
        } else {
            if (!dir.isDirectory()) {
                throw new IOException("Given dirPath refers to non-directory entity");
            }
        }

        return dir;
    }

    public void accept() throws IOException {
        try (Socket socket = serverSocket.accept()) {
            System.out.println("[server] new incoming connection");
            final DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            String filename = readFileName(inputStream);
            File fileCheck = new File(dir, filename);

            System.out.println("[server] filename request: '" + filename + "'");

            if (fileCheck.exists()) {
                switch (policy) {
                    case IGNORE:
                        System.out.println("\t-> file already exist, ignoring..");
                        filename = null;
                        break;
                    case REPLACE:
                        System.out.println("\t-> file already exist, replacing!");
                        if (fileCheck.delete()) {
                            System.out.println("\t-> old file deleted");
                        } else {
                            System.out.println("\t-> unable to delete old file");
                            filename = getAvailableFilename(filename);
                            System.out.println("\t-> saving new file under name: '" + filename + "'");
                        }
                        break;
                    case KEEP_BOTH:
                        filename = getAvailableFilename(filename);
                        System.out.println("\t-> saving new file under name: '" + filename + "'");
                        break;
                }
            }

            if (filename != null) {
                File file = new File(dir, filename);
                file.deleteOnExit();

                OutputStream outputStream = new FileOutputStream(file);

                readWriteBytes(outputStream, inputStream);

                outputStream.close();
                inputStream.close();

                System.out.println("\t-> successfully saved new file: " + filename);
            }
        }
    }

    private void readWriteBytes(OutputStream outputStream, DataInputStream dataInputStream) throws IOException {
        final int fileLength = dataInputStream.readInt();
        final byte[] buffer = new byte[fileLength];

        int totalRead = 0;
        while (totalRead < fileLength) {
            final int bytesRead = readWriteChunk(outputStream, dataInputStream, buffer);
            if (bytesRead == -1) {
                break;
            }

            totalRead += bytesRead;
        }
    }

    private int readWriteChunk(OutputStream outputStream, DataInputStream dataInputStream, byte[] buffer) throws IOException {
        final int bytesRead = dataInputStream.read(buffer);
        outputStream.write(buffer, 0, bytesRead);
        return bytesRead;
    }

    private String getAvailableFilename(String filename) {
        int length = filename.length();
        int it = 0;

        File file = null;

        filename += "_" + it;
        while (file == null || file.exists()) {
            filename = filename.substring(0, length) + "_" + (++it);
            file = new File(dir, filename);
        }
        return filename;
    }

    private String readFileName(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();

        if (length > 255) {
            throw new IllegalArgumentException("Filename length cannot be greater than 255");
        }

        final byte[] filename = new byte[length];
        if (inputStream.read(filename) < 0) {
            throw new IOException("Cannot read filename from socket");
        }

        return new String(filename, "UTF-8");
    }

    public enum FileDuplicatePolicy {
        IGNORE,
        REPLACE,
        KEEP_BOTH
    }

}
