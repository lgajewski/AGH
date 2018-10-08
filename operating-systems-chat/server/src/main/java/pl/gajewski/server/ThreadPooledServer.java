package pl.gajewski.server;

import pl.gajewski.server.chat.ClientSocket;
import pl.gajewski.server.chat.user.UserHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPooledServer implements Runnable {

    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    protected ServerSocket serverSocket;

    protected final int serverPort;
    protected boolean isStopped = false;

    private UserHandler userHandler;

    public ThreadPooledServer(int port) {
        this.serverPort = port;
        this.userHandler = new UserHandler();
    }

    @Override
    public void run() {
        System.out.println("Opening server socket...");
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket;
            try {
                System.out.println("Waiting for client..");
                clientSocket = serverSocket.accept();
                threadPool.execute(new ClientSocket(userHandler, clientSocket));
                System.out.println("Added new client socket");
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                } else {
                    throw new RuntimeException("Error accepting client connection", e);
                }
            }
        }

        threadPool.shutdown();
        System.out.println("Shutting down...");
    }


    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }

    public synchronized void stop() {
        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

}