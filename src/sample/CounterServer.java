package sample;

public class CounterServer implements Runnable{
    private int counter = 0;
    EchoClientHandler handler;

    public CounterServer(EchoClientHandler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }while(counter < 65);
        handler.setDed(true);
        handler.os.println("QUIT");
    }

    public void reset() {
        this.counter = 0;
    }

    public void end() {
        this.counter = 666;
    }
}
