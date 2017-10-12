package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class EchoClientHandler implements Runnable {

    private Socket clientSocket;
    public String status = "fresh";
    public ArrayList<String> userlist;
    public ArrayList<EchoClientHandler> echoClientHandlers;
    public boolean connected = false;
    public String username;
    public PrintStream os;
    public BufferedReader is;

    public EchoClientHandler(Socket clientSocket, ArrayList<String> userlist, ArrayList<EchoClientHandler> echoClientHandlers) {
        this.clientSocket = clientSocket;
        this.userlist = userlist;
        this.echoClientHandlers = echoClientHandlers;
        echoClientHandlers.add(this);
    }

    @Override
    public void run() {
        try {
            String input;

            // Open input and output streams
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            System.out.println(clientSocket.getLocalPort() + " " + clientSocket.getPort());
            os.println("<Server> Connected. use JOIN <<username>>, <<ip:port>> Type 'exit' to close.");
            status = "fresh";
            do {
                input = is.readLine();
                if (input != null) {
                    switch (status) {
                        case "echo": {
                            os.println("<Server> " + input);
                            break;
                        }
                        case "fresh": {
                            String ip = "";
                            int port = 0;
                            String[] inputAr = input.split(" ");
                            if (inputAr.length>2) {
                                if (inputAr[0].equals("JOIN")) {
                                    if (inputAr[1].length() > 0 && inputAr[2].length() > 2 && inputAr[2].contains(":")) {
                                        boolean portFound = false;
                                        boolean portOk = false;
                                        for (int i = 0; i < inputAr[2].length(); i++) {
                                            if (inputAr[2].substring(i, i + 1).equals(":")) portFound = true;
                                            else if (!portFound) ip = ip + inputAr[2].substring(i, i + 1);
                                            else if (!portOk){
                                                try {
                                                    port = Integer.parseInt(inputAr[2].substring(i));
                                                    portOk = true;
                                                } catch (Exception e) {
                                                    os.println("J_ER 10: bad port format");
                                                    break;
                                                }
                                            }
                                        }
                                        username = inputAr[1].substring(0, inputAr[1].length() - 1);
                                        System.out.println(port);
                                        if (clientSocket.getLocalPort() == port) {
                                            if (!userlist.contains(username))
                                                userlist.add(username);
                                            else {
                                                os.println("J_ER 20: username taken");
                                                break;
                                            }
                                            os.println("J_OK");
                                            connected = true;
                                            status = "broadcast";
                                        }else os.println("J_ER 50: port is wrong");
                                    }else os.println("J_ER 40: username or ip+port is missing");
                                }else os.println("J_ER 404: command not found");
                            }else os.println("J_ER 30: something is missing");
                            break;
                        }
                        case "broadcast":{
                            for (EchoClientHandler handler: echoClientHandlers) {
                                handler.os.println("<Server> <Broadcast> "+input);

                            }
                            break;
                        }


                    }
                }
            } while ( !input.trim().equals("QUIT") );
            if (connected){userlist.remove(userlist.indexOf(username));}
            clientSocket.close();
        } catch (IOException ex) {

        }
    }

}