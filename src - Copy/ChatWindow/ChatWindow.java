/*TODO: # Add usermame to sent string and break it up with delimiters?
        # Remove and look over static variables.

        - När Thread antopas automatiskt?
        */

package ChatWindow;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.io.BufferedReader;

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
    private MulticastSocket socket;
    String ip = "239.0.1.2";
    int port = 20480;

    //For sending and receiving data
    DatagramPacket packet, packetReceived;
    static byte[] data = new byte[256];
    String message;

    public ChatWindow() throws IOException {

        setContentPane(contentPane);
        setModal(true);

        // Setting up the network connection
        iadr = InetAddress.getByName(ip);
        group = new InetSocketAddress(iadr, port);
        netIf = NetworkInterface.getByName("eth3");
        socket = new MulticastSocket(port);

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
            public void actionPerformed(ActionEvent e) { onDisconnect(); }
        });


    }


    private void onSend() throws SocketException, IOException{
        message = userText.getText();
        if(message.equals("") || message.equals(" ")) { } //Do nothing
        data = message.getBytes();
        packet = new DatagramPacket(data, data.length,iadr, port);
        socket.send(packet);

        this.chatArea.append(message);
        this.userText.setText("");
        System.out.println("Package sent.");                    // REMOVE LATER
    }


    private void onConnect() throws IOException {
        socket.joinGroup(group, netIf);
        chatArea.setText("Connected.\n");
        connectedUsers.append(username);
    }

    private void onDisconnect() {
        socket.close();
        chatArea.append("Disconnected.\n");
    }


    public static void main(String[] args) throws IOException{

        username = JOptionPane.showInputDialog("Enter your username: ");  //Use for username later

        ChatWindow dialog = new ChatWindow();
        dialog.chatArea.setEditable(false);
        dialog.connectedUsers.setEditable(false);
        dialog.pack();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        //Receive data code
        while (true) {
            dialog.packetReceived = new DatagramPacket(data, data.length);
            dialog.socket.receive(dialog.packetReceived);
            dialog.message = new String(dialog.packetReceived.getData(), 0, dialog.packetReceived.getLength());
            dialog.chatArea.append(dialog.message);
        }
    }
}
