package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection{
    public Socket socket = null;
    public DataInputStream console = null;
    public DataOutputStream streamOut = null;
    public boolean isEstablished = false;

    public Connection(String ip, int port){
        try {
            System.out.println("Establishing connection. Please wait ...");
            socket = new Socket(ip, port);
            System.out.println("Connected: " + socket);
            this.isEstablished = true;
            start();
        } catch (IOException e ) {
            System.out.println("host "+ip+":"+port+" doesn't exist");
            return;
        }
    }

    public void start() throws IOException
    {  console   = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException
    {
        streamOut.writeUTF(msg);
    }


    public void stop()
    {  try
    {  if (console   != null)  console.close();
        if (streamOut != null)  streamOut.close();
        if (socket    != null)  socket.close();
    }
    catch(IOException ioe)
    {  System.out.println("Error closing ...");
    }
    }

}
