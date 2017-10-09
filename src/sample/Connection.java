package sample;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.Socket;

public class Connection implements Runnable{

    public Socket socket = null;
    public BufferedReader is;
    public PrintStream os = null;
    public boolean isEstablished = false;
    public Controller kawaii = null;


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
        if (msg!= null && msg.length()>0)
            os.println(msg);
        kawaii.inputField.clear();
    }


    public void stop()
    {  try
    {  if (is != null)  is.close();
        if (os != null)  os.close();
        if (socket    != null)  socket.close();
    }
    catch(IOException ioe)
    {  System.out.println("Error closing ...");
    }
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
                if (line.length()>0)
                    //outputField.appendText("Server: Received \"" + line + "\"");
                    kawaii.outputField.appendText(line+"\n");
                //try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
            } while ( !line.trim().equalsIgnoreCase("exit") );
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
