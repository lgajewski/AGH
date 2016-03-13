package lgajewski.distributed.ex1.server;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * krc.chat.client.ClientForm - A wee GUI for a wee chat app.
 *
 * @author Keith
 */
public class ClientForm extends javax.swing.JFrame {

    private static final long serialVersionUID = 1675435212L;

    // <awful-netbeans-generated-code>
    private javax.swing.JTextArea displayTextArea;
    private javax.swing.JTextField inputTextField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton sendButton;
    // </awful-netbeans-generated-code>

    private Sender sender = null;

    private class Listener extends SwingWorker<Void, String>
            // The 1st arg is the type of the threads return value.
            // The 2nd arg is the type of intermediate results to be "published".
    {

        // doInBackground is invoked in the background thread, it publishes messages
        // as they arive to a List of Strings which is then picked up and displayed
        // by the process method which runs periodically on the EDT.
        @Override
        public Void doInBackground() {
            MulticastSocket socket = null;
            InetAddress address = null;
            try {
                socket = new MulticastSocket(Config.MULTICAST_OUTBOUND_PORT);
                address = InetAddress.getByName(Config.MULTICAST_IP);
                socket.joinGroup(address);
                while (!isCancelled()) {
                    byte[] buf = new byte[Config.BUF_SIZE];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    System.out.println("DEBUG: Listener.doInBackground: receiving on " + address);
                    socket.receive(packet); //thread blocks here
                    //String response = new String(packet.getData(), 0, packet.getLength());
                    String response = new String(packet.getData()).trim();
                    System.out.println("DEBUG: Listener.doInBackground: Received: " + packet.getAddress() + " " + packet.getPort() + " " + response);
                    publish(response);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (socket != null) try {
                    socket.leaveGroup(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.close();
                socket = null;
            }
            return null;
        }

        // process is invoked in the event dispatch thread whenever swing feels like
        // it so it could process several messages at once, but it's safe to mutate
        // the swing controls on the EDT.
        @Override
        protected void process(List<String> messages) {
            System.out.println("DEBUG: Listener.process: processing: " + messages.size() + " messages");
            for (String message : messages) {
                System.out.println("DEBUG: Listener.process: displayTextArea.append(" + message + ")");
                displayTextArea.append(message + "\n");
            }
        }
    }

    private class Sender {

        private String username = null;
        private DatagramSocket socket = null;
        private InetAddress address = null;

        Sender(String username) throws SocketException, UnknownHostException {
            this.username = username.toLowerCase();
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName("localhost");
        }

        public void send(String message) throws IOException {
            // send the message
            message = username + ": " + message;
            byte[] bytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, Config.MULTICAST_INBOUND_PORT);
            String data = new String(packet.getData(), 0, packet.getLength());
            System.out.println("DEBUG: Sender.send: sending " + packet.getAddress() + " " + packet.getPort() + " " + data);
            this.socket.send(packet);
            System.out.println("DEBUG: Sender.send: sent.");
        }

        public void close() {
            if (socket != null) socket.close();
        }
    }

    /**
     * ClientForm constructor
     */
    public ClientForm(String username) {

        this.setTitle("Chat - " + username);

        // build the GUI
        initComponents();

        // select all the text in the input field and give it focus
        inputTextField.setSelectionStart(0);
        inputTextField.setSelectionEnd(inputTextField.getText().length());
        inputTextField.requestFocus();

        try {
            sender = new Sender(username);
            sender.send(username + " joins the chat.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // setup a background thread which recieves messages from the server
        // and appends them to the TextArea on the form.
        new Listener().execute();


    }

    /**
     * initComponents is called by the constructor to initialize the form.
     */
    // <awful-netbeans-generated-code>
    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        displayTextArea = new javax.swing.JTextArea();
        inputTextField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        displayTextArea.setColumns(20);
        displayTextArea.setFont(new java.awt.Font("Arial", 0, 10));
        displayTextArea.setRows(5);
        displayTextArea.setTabSize(4);
        displayTextArea.setFocusable(false);
        scrollPane.setViewportView(displayTextArea);

        inputTextField.setText("... type your message here ...");
        inputTextField.setName("messageText");
        inputTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputTextFieldActionPerformed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.setName("sendButton");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(inputTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pack();
    }// </awful-netbeans-generated-code>

    private void inputTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTextFieldActionPerformed
        sendButtonActionPerformed(evt);
    }

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        try {
            String message = inputTextField.getText();
            inputTextField.setText("");
            if ("".equals(message.trim())) return;
            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(this, e.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

}