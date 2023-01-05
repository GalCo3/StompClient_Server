package bgu.spl.net.impl.stomp;
import  bgu.spl.net.*;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsIMPL;

import java.net.Socket;
import java.util.Map;
import java.util.WeakHashMap;

public class StompMessagingProtocolIMPL implements StompMessagingProtocol<String> {

    private int connectionId;
    private ConnectionsIMPL<String> connections;

    private boolean shouldTerminate = false;


    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsIMPL<String>) connections;
    }

    @Override
    public void process(String message) {
        int index = message.indexOf('\n');
        String action = message.substring(0,index);
        message = message.substring(index);

        if(action.equals("CONNECT"))
        {
            connect(message);
        }
        else if(action.equals("SEND"))
        {

        }

        else if (action.equals("SUBSCRIBE"))
        {

        }

        else if (action.equals("UNSUBSCRIBE"))
        {

        }

        else if (action.equals("DISCONNECT"))
        {

        }
        else
        {
            // send error message
            //should
        }


    }

    //private
    private void connect(String msg) //user try to connect
    {

        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            if (pair.equals(""))
                continue;
            if (pair.equals("^ @"))
                break;
            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }
        if (!lines.get("accept - version").equals("1.2"))
            //return error frame
            return;

        if (!lines.get("host").equals("stomp . cs . bgu . ac . il"))
            //return error frame
            return;

        if (!connections.connect(lines.get("login"),lines.get("passcode"),connectionId))
            //return error frame
            return;


        String out_msg = "CONNECTED\n" +
                "version :1.2\n" +"\n"+
                "^ @";

        connections.send(connectionId,out_msg);
//        connectionId = connections.getId();
//        connections.create_ConnectionHandler(connectionId,lines.get("login"),);
//        connections.addToMap(connectionId,new BlockingConnectionHandler(new Socket(),new StompEncoderDecoder(),this,));
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
