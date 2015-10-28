package pl.gajewski.server.chat;

import org.json.JSONException;
import org.json.JSONObject;
import pl.gajewski.server.chat.operations.OperationFactory;
import pl.gajewski.server.chat.operations.OperationType;
import pl.gajewski.server.chat.user.UserHandler;
import pl.gajewski.server.chat.operations.IOperation;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;


public class ClientSocket implements Runnable {

    protected Socket userSocket = null;
    private BufferedReader input;
    private BufferedWriter output;

    private UserHandler userHandler;

    public ClientSocket(UserHandler userHandler, Socket clientSocket) throws IOException {
        this.userSocket = clientSocket;
        this.userSocket.setSoTimeout(10000);
        this.userHandler = userHandler;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF8"));
    }

    public void run() {
        String jsonToSend = "Internal error";
        try {
            SocketParser parser = new SocketParser(input);
            JSONObject jsonObject = new JSONObject(parser.getJSON());

            System.out.println("received: " + jsonObject.toString());
            IOperation operation = OperationFactory.create(userHandler, jsonObject);

            // assign call result to json
            jsonToSend = operation.call();

            // TODO log input and result of operations


        } catch (Exception e) {
            e.printStackTrace();
            String msg;
            if (e instanceof InvocationTargetException || e instanceof JSONException) {
                msg = "Incorrect JSON";
            } else if (e instanceof ClassNotFoundException) {
                msg = "Incorrect operation type";
            } else {
                msg = e.getMessage();
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", OperationType.C_FAILURE);
            jsonObject.put("msg", msg);
            jsonToSend = jsonObject.toString();

        } finally {
            try {
                System.out.println("sent: " + jsonToSend);
                output.write(jsonToSend);
                output.close();
            } catch (IOException e4) {
                System.out.println("[ClientSocket] " + e4);
            }
        }

    }
}