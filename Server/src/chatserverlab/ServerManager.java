/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverlab;

import static chatserverlab.ServerConstant.BACKLOG;
import static chatserverlab.ServerConstant.CLIENT_NUMBER;
import static chatserverlab.ServerConstant.SERVER_PORT;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author TanmoyBanik
 */
public class ServerManager implements MessageListener {

    ExecutorService serverExeCutor;
    ServerSocket server;
    Socket clientSocket;
    Clients[] client;
    int clientNumber = 0;
    static String[] clientTracker;
    String users = "";

    public ServerManager() {
        client = new Clients[CLIENT_NUMBER];
        clientTracker = new String[CLIENT_NUMBER];
        serverExeCutor = Executors.newCachedThreadPool();
    }

    public void startServer(ServerStatusListener statusListener, ClientListener clientListener) {
        try {
            server = new ServerSocket(SERVER_PORT, BACKLOG);
            statusListener.status("Server is Listening on port : " + SERVER_PORT);
            serverExeCutor.execute(new ConnectionController(statusListener, clientListener));
        } catch (IOException ex) {
            statusListener.status("IOException occured When Server start");
        }

    }

    public void stopServer(ServerStatusListener statusListener) {
        try {
            server.close();
            serverExeCutor.shutdown();
            statusListener.status("Server is stoped");
        } catch (SocketException ex) {
            //ex.printStackTrace();
            statusListener.status("SocketException Occured When Server is going to stoped");
        } catch (IOException ioe) {
            //ioe.printStackTrace();
            statusListener.status("IOException Occured When Server is going to stoped");
        }
    }

    // send all;
    public void sendNameToAll(String message) {
        for (int i = 0; i < clientNumber; i++) {
            try {
                System.out.println("Server is sending   " + message);
                client[i].output.writeObject(message);
                client[i].output.flush();
            } catch (IOException ex) {
            }
        }
    }

    public void sendFile(String sendTo, int file) {
        for (int i = 0; i < clientNumber; i++) {
            if (clientTracker[i].equalsIgnoreCase(sendTo)) {
                try {
                    client[i].output.writeInt(file);
                    client[i].output.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void controllConnection(ServerStatusListener statusListener, ClientListener clientListener) {
        while (clientNumber < CLIENT_NUMBER) {
            try {
                clientSocket = server.accept();
                client[clientNumber] = new Clients(clientListener, clientSocket, this, clientNumber);
                serverExeCutor.execute(client[clientNumber]);
                System.out.println(clientSocket);
                clientNumber++;
                System.out.println(clientNumber);
            } catch (SocketException ex) {
                ex.printStackTrace();
                break;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                statusListener.status("Some Problem Occured When connection received");
                break;
            }
        }
    }

    @Override
    public void sendInfo(String message) {
        StringTokenizer tokens = new StringTokenizer(message);
        String to = tokens.nextToken();

        for (int i = 0; i < clientNumber; i++) {
            if (clientTracker[i].equalsIgnoreCase(to)) {
                try {
                    client[i].output.writeObject(message);
                    client[i].output.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ConnectionController implements Runnable {

        ServerStatusListener statusListener;
        ClientListener clientListener;

        ConnectionController(ServerStatusListener getStatusListener, ClientListener getClientListener) {
            statusListener = getStatusListener;
            clientListener = getClientListener;
        }

        @Override
        public void run() {
            controllConnection(statusListener, clientListener);
        }
    }
}
