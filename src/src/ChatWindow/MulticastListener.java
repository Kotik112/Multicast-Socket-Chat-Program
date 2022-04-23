/*TODO: * När en tråd anropas statiskt? Vad betyder det? MyThread.run()?
        *
        * */

package ChatWindow;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastListener implements Runnable {

    DatagramPacket packet;

    JTextArea chatArea;
    //int port = 55555;
    MulticastSocket socket;

    public MulticastListener(MulticastSocket soc, JTextArea chatArea) {
        this.chatArea = chatArea;
        this.socket = soc;
    }

    @Override
    public void run() {

        String message;
        byte[] data = new byte[256];
        //Code for thread.
        //while (!Thread.interrupted()) {  //buggy
        while (true) {
            //Receive data code
            packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message = new String(packet.getData(), 0, packet.getLength());
            chatArea.append(message + "\n");
            //System.out.println(message );  //TEST REMOVE LATER
        }
    }
}
