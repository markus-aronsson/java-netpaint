package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import messagepacket.BrushMessage;
import messagepacket.DataMessage;
import messagepacket.IdCode;
import messagepacket.MessagePacket;


public class ClientRunnable implements Runnable {
    
    private Socket clientSocket = null;
    private ObjectInputStream input = null;
    private ObjectOutputStream output = null;
    private ClientRoom room;
    private InetAddress clientIP;
    private byte player_id;
    private boolean isConnected;    
    
    public ClientRunnable(Socket clientSocket) {
    //Let each ClientRunnable object keep track of which room it is part of
    //by handing it a room reference at creation or later on using the setRoom method. 
    //It will also let the ClientRunnable remove 
    //itself from the room if the client disconnects.    	

        this.clientSocket = clientSocket;          
        clientIP = clientSocket.getInetAddress();       
        isConnected = true;        

        try {
            clientSocket.setSoLinger( true, 0 );
            input = new ObjectInputStream( clientSocket.getInputStream() );
            output = new ObjectOutputStream( clientSocket.getOutputStream() );
        }
        catch( StreamCorruptedException e ) {
            System.out.println("StreamCorruptedException in clientRunnable constructor");
        }
        catch( IOException e ) {
            System.out.println("IOException in clientRunnable constructor");
        }
    }

    @Override
    public void run() {
        this.sendDefaultMessages();

        //Broadcast data messages to all players in a room
        while( !clientSocket.isClosed() && isConnected ) { 
            MessagePacket msg = null;

            try{
                msg = (MessagePacket) input.readObject();
                this.interpretMessage( msg );
            } 
            catch ( IOException e ) {
                System.out.println("IOException in clientRunnable");
                break;
            } 
            catch ( ClassNotFoundException e ) {
                System.out.println("ClassNotFoundException in clientRunnable");
                break;
            }

        }
				
        this.closeClientConnection();

        //If there are no players in room, delete room from list
        if(room.getMembers() == 0){
            MultiThreadedServer.clientRoomList.remove(room); 
            MultiThreadedServer.decrementRoomID();
        }
    }


    private void interpretMessage( MessagePacket msg ) {
        
        switch( msg.getMsgId() ){
            case IdCode.QUIT_MESSAGE:
                isConnected = false;
                break;
            case IdCode.DATA_MESSAGE:
            case IdCode.BRUSH_MESSAGE:
            case IdCode.UNDO_PATH:
            case IdCode.REDO_PATH:
                room.broadcast( msg, this );
                break;
        }
       // TODO: implement history handling      
    }
   
    
    /**
    * Send all the default messages when a player enters a room
    */    
    private void sendDefaultMessages() {      
        try{    
            //Broadcast a message that a new player has joined 
            if(room.getMembers() > 0)     
                room.broadcast( new MessagePacket( 
                        IdCode.PLAYER_JOINED, 
                        getPlayerId(), 
                        room.getMembers() ), this );
                        //Send message to yourself that you successfully joined a room
            
            output.writeObject( new MessagePacket( 
                IdCode.JOIN_SUCCESS, 
                getPlayerId(), 
                room.getMembers() ) );
        
            output.flush();
            
            
        }
        catch( Exception e ){ 
            System.out.println("Failed to send default messages"); 
        }
    }
    
    /**
    * Close down all the clients connections such as output/input stream etc
    */    
    private void closeClientConnection() {
        //Remove client from room and make him able to connect to server again
        room.removeClient(this);

        //Broadcast message to tell a member has disconnected
        if(room.getMembers() > 0) 
            room.broadcast( new MessagePacket( 
                    IdCode.PLAYER_QUIT, 
                    getPlayerId(), 
                    room.getMembers() ), this);        

        try {
            output.close();
            input.close();
            clientSocket.close(); 
            System.out.println("Client disconnected " + clientIP);	
        }
        catch( IOException e ) { 
            System.out.println("Error while closing down connection"); 
        }
        
        MultiThreadedServer.connectedClients.removeInetAddress( clientIP );
    }

    
    protected ObjectOutputStream getOutputStream() {
        return output;
    }

    protected void setRoom(ClientRoom room) {
        this.room = room;
    }

    protected void setPlayerId() {
        player_id = room.createPlayerId();
    }

    protected byte getPlayerId() {
        return player_id;
    }

/* In case of debugging
    private static void printCoords( DataMessage msg ) {
    
        System.out.println("\n/////////////////////////////////////////////////////////////////");
       // System.out.println("Received message:" + clientSocket.getInetAddress().toString());
       // System.out.println("Room: " + room.getId());    

        short[] tmp = msg.getCoordinates();
        int temp = 0;
        System.out.println("Server points: " + tmp.length);

        for( int i = 0; i < tmp.length; i+=2 ) {
            System.out.print( "(" + tmp[i] + "," + tmp[i+1] + ")  " );
            temp++;
            if( temp%5 == 0 )
                System.out.println("");   	
        }
        System.out.println("");
    }

    private static void printBrushValues( BrushMessage msg ) {
        System.out.println( "" );
        System.out.println( "R: " + msg.getColorR() );
        System.out.println( "G: " + msg.getColorG() );
        System.out.println( "B: " + msg.getColorB() );
        System.out.println( "Alpha: " + msg.getAlphaValue() );
        System.out.println( "Brush size: " + msg.getBrushSize() );
        System.out.println( "" );    
    }
*/
    
}
