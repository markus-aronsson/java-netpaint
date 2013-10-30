package server;

public class Server {
	
    private static final int SERVER_PORT = 5163;

    public static void main(String args[]){

        MultiThreadedServer server = new MultiThreadedServer(SERVER_PORT);
        new Thread(server).start();

        System.out.println("Server is running.");
        // The server will run for 24 hours
        try {
            //Days_Hours_Min_Sec_Milisec
            Thread.sleep(1 * 24 * 60 * 60 * 1000);
        }
        catch( InterruptedException e ) {
          System.out.println("Crash in ServerStart");
        }

        System.out.println("Stopping Server");
        server.stop();
        System.exit(0); // Make a clean exit
    }
}
