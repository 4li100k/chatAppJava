package sample;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable{

    public Socket socket = null;
    public BufferedReader is;
    public PrintStream os = null;
    public boolean isEstablished = false;
    public Controller kawaii = null;
    public String status = "fresh";


    public Connection(String ip, int port, Controller kawaii){
        try {
            System.out.println("Establishing connection. Please wait ...");
            socket = new Socket(ip, port);
            this.kawaii = kawaii;
            System.out.println("Connected: " + socket);
            this.isEstablished = true;
        } catch (IOException e ) {
            System.out.println("host "+ip+":"+port+" doesn't exist");
            return;
        }
    }


    public void sendMessage(String msg) throws IOException
    {
        if (kawaii != null){
            if (msg!= null && msg.length()>0)
                        os.println(msg);
                        kawaii.outputField.appendText("<me> "+msg+"\n");
        }
        else kawaii.outputField.appendText("you are not connected\n");
        kawaii.inputField.clear();
    }

    @Override
    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintStream(socket.getOutputStream());

            String line;

            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("initializing a receiver on port"+socket.getLocalPort() + " " + socket.getPort());

            do {
                line = is.readLine();

                if (line!=null){
                    if (line.length()>0)
                                        //kawaii.outputField.appendText(line+"\n");
                                        protocol(line);
                }else {kawaii.outputField.appendText("   >DISCONNECTED<\n");
                        line = "QUIT";
                }


            } while ( !line.trim().equalsIgnoreCase("QUIT") );
            socket.close();
            is.close();
            os.close();
            System.out.println("disconnected");
            kawaii.inputField.setVisible(false);
            kawaii.sendButton.setVisible(false);
            //kawaii.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void protocol(String line) {
        kawaii.outputField.appendText(line+"\n");

    }

}
