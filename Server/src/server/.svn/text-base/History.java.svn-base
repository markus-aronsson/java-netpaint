package server;

import java.util.ArrayList;
import java.util.Iterator;
import messagepacket.DataMessage;
import messagepacket.IdCode;
import messagepacket.MessagePacket;
import messagepacket.UndoMessage;



public class History {
    private ArrayList<MessagePacket> history;
    
    public History() {
        history = new ArrayList<MessagePacket>();
    }
    
    protected synchronized void addMsgToHistory(MessagePacket msg) {
        history.add(msg);
    }
   
    protected synchronized boolean undoMsgFromHistory( UndoMessage msg ) {
        Iterator<MessagePacket> it = history.iterator();
        while( it.hasNext() ){
            MessagePacket message = it.next();
            switch(message.getMsgId()){
                case IdCode.DATA_MESSAGE:
                    DataMessage temp_msg = (DataMessage) message.getMsg();
                    if( temp_msg.getPathId() == msg.getPathId() ) {
                        history.remove( message );
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
    
    /* //TODO: implement Redo from history
    protected synchronized boolean redoMsgFromHistory( RedoMessage msg ) {
    
    } 
    */
    
    protected synchronized boolean isEmpty() {
        return history.isEmpty();
    }
    
}
