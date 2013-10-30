package server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ClientRoom {
    
    private static final boolean DEBUG = false;
    
    private ArrayList<ClientRunnable> clients;
    protected History history;
    private int roomId;
    private byte members = 0;
    private byte player_id = -1;

    public ClientRoom() {
        clients = new ArrayList<ClientRunnable>();
        history = new History();
    }
	
    public ClientRoom( int roomId ) {
        clients = new ArrayList<ClientRunnable>();
        this.roomId = roomId;
        history = new History();
    }

    protected void addClient( ClientRunnable client ) {
        clients.add(client);
        members++;
    }

    protected int getId() {
        return this.roomId;
    }

    protected byte getMembers() {
        return members;
    }
	
    protected boolean isEmpty() {
        if(members == 0) 
            return true;
    
        return false;
    }

    // If a client disconnects, call removeClient to remove it from the room
    // this will allow newly connected clients to join an existing room and help
    // MultiThreadedServer to avoid creating more rooms than necessary
    protected void removeClient( ClientRunnable client ) {
        clients.remove(client);
        members--;
    }

    protected ArrayList<ClientRunnable> getClientList() {
        return this.clients;    
    }

    protected synchronized byte createPlayerId() {
        player_id = (byte)( (player_id + 1) % 128 );
        return (byte)player_id;  
    }    
    
    protected synchronized void broadcast( Object msg, ClientRunnable currentClient ) {
        if(msg != null) {
            ObjectOutputStream writeToClient = null;
            for(ClientRunnable client : clients) {
                if(client.equals(currentClient))
                    continue;
                try {
                    writeToClient = client.getOutputStream();                     
                    writeToClient.writeObject(msg); // Get the ClientRunnables ObjectOutputStream
                    writeToClient.flush();
                    if(DEBUG)
                        System.out.println("Broadcast successfully sent message");
                } 
                catch( Exception e ) {
                    System.out.println("Crash in the broadcast function");
                }
            }
        }
    }
}
