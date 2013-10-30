package server;

import java.net.InetAddress;
import java.util.HashSet;

public class ConnectedClients {

    private static HashSet<InetAddress> connectedClients;
    
    public ConnectedClients(){
        connectedClients = new HashSet<InetAddress>();
    }
    
    protected synchronized void addInetAddress( InetAddress inet_address ) {
        connectedClients.add( inet_address );
    }    
    
    protected synchronized void removeInetAddress( InetAddress inet_address ) {
        connectedClients.remove( inet_address );
    }
    
    protected synchronized boolean isEmpty(){
        return connectedClients.isEmpty();
    }
    
    protected synchronized boolean containsInetAddress( InetAddress inet_address ){
        return connectedClients.contains( inet_address );
    }
    
}