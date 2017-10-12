package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Controller {

    public TextArea outputField;
    public TextField inputField;
    public Button sendButton, connectButton;

    public TableView<Name> userList;
    public TableColumn<Name, String> userListCol;
    private ArrayList<Name> namesList = new ArrayList<>();
    public ObservableList<Name> names = FXCollections.observableArrayList(new ArrayList<Name>());

    public boolean connected = false;
    public Connection connection = null;

    public void initialize(){
        try {
            sendButton.setVisible(false);
            inputField.setText("JOIN username, " + Inet4Address.getLocalHost().getHostAddress() + ":4444");
            userListCol.setCellValueFactory(new PropertyValueFactory<Name, String>("name"));
            userList.getColumns().clear();
            userList.getColumns().addAll(userListCol);
            names = FXCollections.observableArrayList(namesList);
            userList.setItems(names);
        }catch (UnknownHostException e) {e.printStackTrace();}
    }

    public void connect() throws IOException{
        String input = inputField.getText();
        String[] inputAr = input.split(" ");
        String username = "";
        String ip = "";
        boolean portFound = false;
        int port = 0;
        if (inputAr.length>2){
            if (inputAr[0].equals("JOIN") && inputAr[1].length()>0 && inputAr[2].length()>0){
                if (inputAr[2].length()>0){
                    try {
                        for (int i = 0; i<inputAr[2].length();i++){
                            if (inputAr[2].substring(i,i+1).equals(":")) portFound = true;
                                else if (!portFound) ip = ip + inputAr[2].substring(i,i+1); else {
                                port = Integer.parseInt(inputAr[2].substring(i));
                                username = inputAr[1].substring(0, inputAr[1].length() - 1);
                                break;
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                }
                if (connection == null || !connection.isEstablished()) {
                    connection = new Connection(ip, port, username, this);
                    if (connection.isEstablished()) {
                        Thread receiver = new Thread(connection);
                        receiver.setDaemon(true);
                        receiver.start();
                        sendButton.setVisible(true);
                        connectButton.setVisible(false);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        connection.sendMessage(input);
                        inputField.clear();

                        inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ENTER) {
                                    try {
                                        sendMessage();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }else {//else connection itself will speak here
                        connection = null;
                    }
                }else outputField.appendText("connection has been already established\n");
            }else outputField.appendText("wrong command format\n");
        }else outputField.appendText("something is missing\n");
    }

    public void sendMessage() throws IOException {
        if(connection.isEstablished()){
            String msg = inputField.getText();
            connection.sendMessage(msg);
        }
    }

    public void end(){
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }

}
