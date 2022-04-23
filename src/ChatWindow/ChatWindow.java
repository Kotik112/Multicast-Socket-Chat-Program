/*TODO: # Add usermame to sent string.
        # Remove and look over static variables.
        # Where to close() the  socket ??
        #
        */

package ChatWindow;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class ChatWindow extends JDialog {

    private JPanel contentPane;
    private JButton sendButton;
    private JPanel chatWindow;
    private JTextField userText;
    private JTextArea chatArea;
    private JTextArea connectedUsers;
    private JButton connectUser;
    private JButton disconnectUser;

    static String username;

    //For network sockets/communication
    private InetAddress iadr;
    private InetSocketAddress group;
    private NetworkInterface netIf;
    static protected MulticastSocket socket;
    String ip = "239.0.1.2";
    int port = 20480;

    //Thread
    static Thread listenerThread;
    static MulticastListener receiver;

    //For sending and receiving data
    DatagramPacket packet;
    static byte[] data = new byte[256];
    String message;

    public ChatWindow() throws IOException {

        setContentPane(contentPane);
        setModal(true);
        // Setting up the network connection
        iadr = InetAddress.getByName(ip);
        group = new InetSocketAddress(iadr, port);
        netIf = NetworkInterface.getByName("eth4");
        socket = new MulticastSocket(port);
        receiver = new MulticastListener(socket, this.getChatArea());

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onSend();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        userText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onSend();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        connectUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onConnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


        disconnectUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onDisconnect();
                } catch (IOException ex) {

                }
            }
        });
    }

    //Getter
    public JTextArea getChatArea() {
        return chatArea;
    }

    //Setter
    public void setChatArea(JTextArea chatArea) {
        this.chatArea = chatArea;
    }

    private void onSend() throws SocketException, IOException{
        message = username + ": " + userText.getText();
        if(message.equals("") || message.equals(" ")) { } //Do nothing
        data = message.getBytes();
        packet = new DatagramPacket(data, data.length,iadr, port);
        socket.send(packet);

        this.userText.setText("");
        System.out.println("Package sent.");                // For debugging purposes
    }


    private void onConnect() throws IOException {
        socket.joinGroup(group, netIf);
        chatArea.setText("Connected.\n");
        connectedUsers.append(username);
    }

    private void onDisconnect() throws IOException {
        listenerThread.interrupt();
        socket.leaveGroup(group, netIf);
        //socket.close();
        connectedUsers.setText("");
        chatArea.append("Disconnected.\n");
    }


    public static void main(String[] args) throws IOException{

        username = JOptionPane.showInputDialog("Enter your username: ");  //Use for username later

        ChatWindow dialog = new ChatWindow();
        listenerThread = new Thread(receiver);
        listenerThread.start();

        dialog.chatArea.setEditable(false);
        dialog.connectedUsers.setEditable(false);
        dialog.pack();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
