package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
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
    public Boolean isDed = false;
    public CounterServer counter;
    private Thread counterThread;


    public EchoClientHandler(Socket clientSocket, ArrayList<String> userlist, ArrayList<EchoClientHandler> echoClientHandlers) {
        this.clientSocket = clientSocket;
        this.userlist = userlist;
        this.echoClientHandlers = echoClientHandlers;
        echoClientHandlers.add(this);
    }

    @Override
    public void run() {
        try {
            String input = "";
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            System.out.println(clientSocket.getLocalPort() + " " + clientSocket.getPort());
            os.println("DATA Server: Connected. use JOIN <<username>>, <<ip:port>> Type 'exit' to close.");
            status = "fresh";
            do {
                if (!isDed)
                input = is.readLine();
                if (input != null && input.length()>0 && !input.equals("QUIT")) {
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
                                        if (clientSocket.getLocalPort() == port && Inet4Address.getLocalHost().getHostAddress().equals(ip)) {
                                            if (!userlist.contains(username)) {
                                                if (username.equals(username.replaceAll("[^a-zA-Z_0-9-]","")) && username.length()<13 ){
                                                    userlist.add(username);
                                                    os.println("J_OK");
                                                    updateList();
                                                    connected = true;
                                                    status = "broadcast";
                                                    counter = new CounterServer(this);
                                                    counterThread = new Thread(counter);
                                                    counterThread.setDaemon(true);
                                                    counterThread.start();
                                                }else {
                                                    os.println("J_ER 21: username not allowed");
                                                    break;
                                                }
                                            }else {
                                                os.println("J_ER 20: username taken");
                                                break;
                                            }
                                        }else os.println("J_ER 50: ip or port is wrong, expected ip: " + Inet4Address.getLocalHost().getHostAddress()+ ", received ip: " + ip);
                                    }else os.println("J_ER 40: username or ip+port is missing");
                                }else os.println("J_ER 404: command not found");
                            }else os.println("J_ER 30: something is missing");
                            break;
                        }
                        case "broadcast":{
                            protocol (input);
                        }
                    }
                }
            } while ( !input.trim().equals("QUIT") && !isDed);
            if (connected){userlist.remove(userlist.indexOf(username));}
            updateList();
            counter.end();
            counterThread.join();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateList(){
        String output = "LIST";
        for (String name: userlist) {
            output = output + " " + name;
        }
        for (EchoClientHandler handler: echoClientHandlers) {
            handler.os.println(output);
        }

    }

    public void protocol(String line) {
        String[] lineAr = line.split(" ");
        switch (lineAr[0]){
            case "DATA":{
                String source = lineAr[1].substring(0, lineAr[1].length() - 1);
                if (source.equals(username)){
                    for (EchoClientHandler handler: echoClientHandlers) {
                        if (handler != this)
                            handler.os.println(line);
                    }
                } else {
                    os.println("J_ER 1024: wrong sourcename");
                }

                break;
            }
            case "IMAV":{
                counter.reset();
                break;
            }
            default:{
                os.println("J_ER 666: command not recognised");
                break;
            }
        }
    }

    public void setDed(Boolean ded) {
        isDed = ded;
    }
}