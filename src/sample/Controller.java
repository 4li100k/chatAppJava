package sample;


import javafx.scene.control.*;

import java.io.IOException;

public class Controller {

    public TextArea outputField;
    public TextField inputField;
    public Button sendButton, connectButton;
    public TableView<String> userList;
    public TableColumn userListCol;


    public boolean connected;
    public static Connection connection = null;

    public void initialize(){
        sendButton.setVisible(false);
        connected = false;
        inputField.setText("172.16.17.151:4545");
    }

    public void connect() throws IOException{
        System.out.println("connect()");
        String input = inputField.getText();
        String ip = "";
        int port = 0;
        if (input.length()>0){
            try {
                boolean portFound = false;
                for (int i = 0; i<input.length();i++){
                    if (input.substring(i,i+1).equals(":")) portFound = true;
                        else if (!portFound) ip = ip + input.substring(i,i+1); else {
                        port = Integer.parseInt(input.substring(i));
                        break;
                        }
                    }
                System.out.println(ip);
                System.out.println(port);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
        }


        if (connection == null || !connection.isEstablished){
            connection = new Connection(ip, port);
        }
        sendButton.setVisible(true);
        connectButton.setVisible(false);
        inputField.clear();
    }

    public void sendMessage() throws IOException {
        String input = inputField.getText();
        if(connection.isEstablished && input.length()>0){
            connection.sendMessage(input);
        }
    }





}
