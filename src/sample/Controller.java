package sample;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class Controller {

    public TextArea outputField;
    public TextField inputField;
    public Button sendButton, connectButton;
    public TableView<String> userList;
    public TableColumn userListCol;


    public boolean connected;
    public Connection connection = null;

    public void initialize(){
        sendButton.setVisible(false);
        connected = false;
        //inputField.setText("172.16.17.151:4545");
        inputField.setText("localhost:4444");

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


        if (connection == null || !connection.isEstablished) {
            connection = new Connection(ip, port, this);
            Thread receiver = new Thread(connection);
            receiver.setDaemon(true);
            receiver.start();
            sendButton.setVisible(true);
            connectButton.setVisible(false);
            inputField.clear();
            inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.ENTER)  {
                        try {
                            sendMessage();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void sendMessage() throws IOException {
        if(connection.isEstablished){
            String msg = inputField.getText();
            connection.sendMessage(msg);
        }
    }







}
