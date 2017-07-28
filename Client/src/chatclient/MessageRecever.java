/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import static chatclient.ClientConstant.DISCONNECT_STRING;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author TanmoyBanik
 */
class MessageRecever implements Runnable {

    ObjectInputStream input;
    boolean keepListening = true;
    ClientListListener clientListListener;
    ClientWindowListener clientWindowListener;
    ClientManager clientManager;
    Socket clientSocket;
    ExecutorService clientExecutor;

    MessageRecever(Socket getClientSocket, ClientListListener getClientListListener, ClientWindowListener getClientWindowListener, ClientManager getClientManager) {
        clientExecutor = Executors.newCachedThreadPool();
        clientManager = getClientManager;
        clientSocket = getClientSocket;
        try {
            input = new ObjectInputStream(getClientSocket.getInputStream());
        } catch (IOException ex) {
        }
        clientListListener = getClientListListener;
        clientWindowListener = getClientWindowListener;
    }

    @Override
    public void run() {
        String message, name = "", ips = "";
        while (keepListening) {
            try {
                message = (String) input.readObject();
                System.out.println("user is receiving " + message);
                StringTokenizer tokens = new StringTokenizer(message);

                String header = tokens.nextToken();
                if (tokens.hasMoreTokens()) {
                    name = tokens.nextToken();
                }

                if (header.equalsIgnoreCase("login")) {
                    clientListListener.addToList(name);

                } else if (header.equalsIgnoreCase(DISCONNECT_STRING)) {
                    clientListListener.removeFromList(name);
                } else if (header.equalsIgnoreCase("server")) {
//                    clientWindowListener.closeWindow(message);
                } // Video 
                else if (name.equalsIgnoreCase("video") || name.equalsIgnoreCase("video1")) {
//                    VideoConference videoConference = new VideoConference(message);
//                    clientExecutor.execute(videoConference);
                    System.out.println("Video Message Receiver");
                    System.out.println(message);
                    System.out.println("VIDEO CHAT Thread started :)");
//                } else if (name.equalsIgnoreCase("sendfile") || name.equalsIgnoreCase("sendfile1")) {
                } else if (name.equalsIgnoreCase("sendfile")) {
                    String address = tokens.nextToken();
                    System.out.println(address);
                    clientExecutor.execute(new FileReceiver(address));
                    System.out.println("File Receiver");
                    System.out.println(message);
                } else {
                    clientWindowListener.openWindow(message);
                    System.out.println(message);
                }
            } catch (IOException ex) {
                clientListListener.removeFromList(name);
            } catch (ClassNotFoundException ex) {

            }
        }
    }

    void stopListening() {
        keepListening = false;
    }

}
