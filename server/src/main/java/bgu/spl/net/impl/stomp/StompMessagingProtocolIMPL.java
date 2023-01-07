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
            send(message);
        }

        else if (action.equals("SUBSCRIBE"))
        {
            subscribe(message);
        }

        else if (action.equals("UNSUBSCRIBE"))
        {
            unsubscribe(message);
        }

        else if (action.equals("DISCONNECT"))
        {
            disconnect(message);
        }
        else
        {
            // send error message  //#TODO
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
            
            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;
            
            if (pair.equals("^ @"))
                break;
            
            if (!pair.contains(":"))
                //return error msg //#TODO
                return;
            
            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        if (!lines.get("accept - version").equals("1.2"))
            //return error frame  //#TODO
            return;

        if (!lines.get("host").equals("stomp . cs . bgu . ac . il"))
            //return error frame //#TODO
            return;

        boolean acceptBool = false;
        boolean hostBool = false;
        boolean loginBool =false;
        boolean passBool = false;

        for (Map.Entry<String, String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("accept - version"))
            {
                if (acceptBool)
                    //error more than 1 time //#TODO
                    return;
                else
                    acceptBool =true;
            }

            else if (entry.getKey().equals("host"))
            {
                if (hostBool)
                    //error more than 1 time //#TODO
                    return;
                else
                    hostBool = true;
            }

            else if (entry.getKey().equals("login"))
            {
                if (loginBool)
                    //error more than 1 time //#TODO
                    return;
                else
                    loginBool = true;
            }

            else if (entry.getKey().equals("passcode"))
            {
                if (passBool)
                    //error more than 1 time //#TODO
                    return;
                else
                    passBool=true;
            }
            else
                //error line //#TODO
                return;
        }

        if (!acceptBool)
            //#TODO
            return;

        if (!hostBool)
            //#TODO
            return;

        if (!loginBool)
            //#TODO
            return;

        if (!passBool)
            //#TODO
            return;

        if (!connections.connect(lines.get("login"),lines.get("passcode"),connectionId))
            //return error frame #TODO
            return;


        String out_msg = "CONNECTED\n" +
                "version:1.2\n"
                +"\n"+
                "^@";

        connections.send(connectionId,out_msg);
    }

    private void send(String msg)
    {
      
    }

    public void subscribe(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            
            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;
            
            if (pair.equals("^ @"))
                break;
            
            if (!pair.contains(":"))
                //return error msg #TODO
                return;
            
            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean idBool = false;
        boolean destBool = false;
        for (Map.Entry<String, String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("id"))
            {
                if (idBool)
                    //line of ID appear more than 1 time #TODO
                    return;
                else
                    idBool = true;
            }
            else if (entry.getKey().equals("destination"))
            {
                if (destBool)
                    //line of destination appear more than 1 time #TODO
                    return;
                else
                    destBool = true;
            }
            else
                // error line - does not suppose to be there #TODO
                return;
        }

        if (!destBool)
            // destination missing #TODO
            return;

        if (!idBool)
            // id missing #TODO
            return;


        if (!connections.subscribe(lines.get("destination"),connectionId,Integer.parseInt(lines.get("id"))))
            //error in subscribe #TODO
            return;

        String msg_out = "RECEIPT\n" +
                "receipt-id:"+lines.get("id")+"\n"+"\n" +
                "^@";

        connections.send(connectionId,msg_out);
    }

    private void unsubscribe(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {

            String pair = pairs[i];

            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;

            if (pair.equals("^ @"))
                break;

            if (!pair.contains(":"))
                //return error msg #TODO
                return;

            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean idBool = false;
        boolean recBool = false;

        for (Map.Entry<String,String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("id"))
            {
                if (idBool)
                    //id line appear more than 1 time #TODO
                    return;
                else
                    idBool = true;
            }
            else if (entry.getKey().equals("receipt"))
            {
                if (recBool)
                    return;
                else
                    recBool = true;
            }
            else
                //error line #TODO
                return;
        }

        if (!idBool)
            //#TODO
            return;

        if (!recBool)
            return;

        if (!connections.unsubscribe(connectionId,Integer.parseInt(lines.get("id"))))
            //#TODO
            return;

        String msg_out =
                "RECEIPT\n" +
                        "receipt-id:"+lines.get("receipt")+"\n" +
                        "\n" +
                        "^@";
    }

    private void disconnect(String msg)
    {

    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
