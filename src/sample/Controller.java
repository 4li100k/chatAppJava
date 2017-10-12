package sample;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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
        inputField.setText("JOIN username, localhost:4444");

    }

    public void connect() throws IOException{
        String input = inputField.getText();
        String[] inputAr = input.split(" ");
        if (inputAr.length>2){
            if (inputAr[0].equals("JOIN") && inputAr[1].length()>0 && inputAr[2].length()>0){
                        String username = "";
                        String ip = "";
                        int port = 0;
                        if (inputAr[2].length()>0){
                            try {
                                boolean portFound = false;
                                for (int i = 0; i<inputAr[2].length();i++){
                                    if (inputAr[2].substring(i,i+1).equals(":")) portFound = true;
                                        else if (!portFound) ip = ip + inputAr[2].substring(i,i+1); else {
                                        port = Integer.parseInt(inputAr[2].substring(i));
                                        break;
                                        }
                                    }
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
                            try {Thread.sleep(1000 );} catch (InterruptedException e) {e.printStackTrace();}
                            connection.sendMessage(input);
                            inputField.clear();
                        }
                    }
                    else outputField.appendText("wrong command format\n");
        }else outputField.appendText("something is missing\n");
    }

    public void sendMessage() throws IOException {
        if(connection.isEstablished){
            String msg = inputField.getText();
            connection.sendMessage(msg);
        }
    }

    public void end(){
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }






}
