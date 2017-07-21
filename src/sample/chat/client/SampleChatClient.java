/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author bhans
 */
public class SampleChatClient {

    private Socket mSocket;

    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    public SampleChatClient() {
        // TODO code application logic here bitch
    }

    public void connect(String ipAddress, int port) {
        try {
            mSocket = new Socket(ipAddress, port);
            if (mSocket.isConnected()) {
                writer = new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream()));
                reader = new BufferedReader(
                        new InputStreamReader(mSocket.getInputStream()));
                System.out.println("Connected to server");

                writer.write("HELLO Server!\r\n");
                writer.flush();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println("From Server: " + line);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Runnable() {
            @Override
            public void run() {
                SampleChatClient scc = new SampleChatClient();
                scc.connect("127.0.0.1", 9001);
            }
        }.run();
    }

}
