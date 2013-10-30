package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class MultiThreadedServer implements Runnable {
    
    private int          serverPort   = 8080;
    private ServerSocket serverSocket = null;
    private boolean      isStopped    = false;
    private static Random randomGenerator = new Random();
	
    private static int roomID = 0; 
	
    protected static ArrayList<ClientRoom> clientRoomList = new ArrayList<ClientRoom>();
    protected static ConnectedClients connectedClients;
    
    private final static int MAX_NUMBER_CLIENTS = 3;
    
    public MultiThreadedServer(int port){
        this.serverPort = port;              
        connectedClients = new ConnectedClients();
    }
    
    @Override
    public void run(){
        openServerSocket();
        while( !isStopped() ) {        	
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            }catch (Exception e) {               
                System.out.println("Server couldn't accept incoming client connection.") ;
                continue;
            }

            //Check if client is already connected, if he is, disconnect him.
            InetAddress clientIP = clientSocket.getInetAddress();
            
            if( !connectedClients.containsInetAddress( clientIP ) ) {
                
                System.out.println( "Client accepted" + clientIP.toString() );
                connectedClients.addInetAddress( clientIP );
                // Create a new ClientRunnable with a the client socket
                ClientRunnable clientRunnable = new ClientRunnable( clientSocket );

                lookingForRoom( clientRunnable );
                clientRunnable.setPlayerId();		            

                new Thread( clientRunnable ).start();
            }
            else {
                // If client is already connected
                System.out.println("Client already connected" + clientIP.toString());
                try {
                    clientSocket.close();
                }
                catch ( Exception e ) { 
                    System.out.println( "Exception in closing client in MultiThreadedServer" );
                }
            }
        }
        System.out.println("Server Stopped.") ;
     }
   
   
    //Increment the room ID
    protected static synchronized void incrementRoomID(){
    	roomID++;
    }
    
    //Decrement the room ID
    protected static synchronized void decrementRoomID(){
    	roomID--;
    }
    
    private synchronized void lookingForRoom(ClientRunnable clientRunnable){
        // Look for a room that has less than x ClientRunnables
        
        boolean foundRoom = false;
        int tries = 0;
        
        // First try to pick a random room and add the ClientRunnable
        // but only try 3 times
        while(!foundRoom && tries < 3 && !clientRoomList.isEmpty()) {
        	tries++;
        	int index = randomGenerator.nextInt(clientRoomList.size());
        	ClientRoom room = clientRoomList.get(index);
        	if(room.getMembers() < MAX_NUMBER_CLIENTS) {
        		foundRoom = true;
        		room.addClient(clientRunnable);
        		clientRunnable.setRoom(room);
        		break;
        	}
        }
       
        // If no room was found a random then check all the rooms
        // beginning from the start of the list
        if(!foundRoom && !clientRoomList.isEmpty()) {	  	
	        for(ClientRoom room : clientRoomList) {
	            if(room.getMembers() < MAX_NUMBER_CLIENTS) {
	            	foundRoom = true;
	            	room.addClient(clientRunnable);
	            	clientRunnable.setRoom(room);
	            	break;
	            }
	        }
        }

        // If all the existing ClientRooms are full
        // create a new ClientRoom and add clientRunnable
        if(!foundRoom) {
        	incrementRoomID();
        	ClientRoom clientRoom = new ClientRoom(roomID);
        	clientRoom.addClient(clientRunnable);
        	clientRoomList.add(clientRoom);
        	clientRunnable.setRoom(clientRoom);
        }    		
    }
    
    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    protected synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 5163", e);
        }
    }
}
