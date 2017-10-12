package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class EchoServerMultipleClients {

    public static void main(String args[]) {

        ArrayList<String> userlist = new ArrayList<>();
        ArrayList<EchoClientHandler> echoClientHandlers = new ArrayList<>();

        ServerSocket echoServer = null;
        String line;
        Socket clientSocket = null;

        // Open a server socket on port 4444
        try {
            echoServer = new ServerSocket(4444);
            System.out.println("Server is up and running... ");
            while (true) {
                // Create a socket object from the ServerSocket to listen and accept connections.
                clientSocket = echoServer.accept();
                new Thread(new EchoClientHandler(clientSocket, userlist, echoClientHandlers)).start();
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
