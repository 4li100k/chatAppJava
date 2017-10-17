package sample;

import java.io.IOException;

public class CounterClient implements Runnable{
    private int counter = 0;
    Connection connection;

    public CounterClient(Connection connection){
        this.connection = connection;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (connection.getStatus().equals("connected"))
            connection.os.println("IMAV");
        }while(!connection.isDed());
        connection.setDed(true);
        connection.os.println("QUIT");
    }
}
