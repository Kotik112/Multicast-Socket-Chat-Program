//TODO: # Add username to chat window to see who typed the message.

package ChatServer;

import javax.swing.*;
import java.io.IOException;
import java.net.*;

public class Traffic extends JDialog {
    private JPanel contentPane;
    private JTextArea trafficArea;

    public Traffic() {
        setContentPane(contentPane);
        setModal(true);
    }

    public static void main(String[] args) throws IOException {
        Traffic dialog = new Traffic();
        dialog.trafficArea.setEditable(false);
        dialog.trafficArea.setColumns(50);
        dialog.trafficArea.setRows(40);
        dialog.pack();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);    // Required?
        dialog.setVisible(true);

        int port = 55555;
        String ip = "239.0.1.2";

        InetAddress iadr = InetAddress.getByName(ip);
        InetSocketAddress group = new InetSocketAddress(iadr,port);
        NetworkInterface netIf = NetworkInterface.getByName("eth3");
        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(group, netIf);

        byte[] data = new byte[256];
        while (true) {
            DatagramPacket packetReceived = new DatagramPacket(data,data.length);
            socket.receive(packetReceived);
            String message = new String(packetReceived.getData(), 0, packetReceived.getLength());
            dialog.trafficArea.append(message);

        }

    }
}