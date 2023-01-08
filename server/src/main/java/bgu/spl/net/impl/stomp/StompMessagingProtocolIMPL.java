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

        switch (action) {
            case "CONNECT":
                connect(message);
                break;
            case "SEND":
                send(message);
                break;
            case "SUBSCRIBE":
                subscribe(message);
                break;
            case "UNSUBSCRIBE":
                unsubscribe(message);
                break;
            case "DISCONNECT":
                disconnect(message);
                break;
            default:
                errorMSG("", "malformed action received", message, "Action is not a part of the protocol actions");// send error message
                break;
        }


    }

    private void connect(String msg) //user try to connect
    {

        Map<String, String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i = 0; i < pairs.length; i++) {

            String pair = pairs[i];

            if (pair.equals("") && (i == pairs.length - 2 | i == 0))
                continue;

            if (pair.equals("^@"))
                break;

            if (!pair.contains(":"))
            {
                errorMSG("", "malformed frame received", msg, "contains a illegal statement");
                return;
            }

            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        if (!lines.get("accept - version").equals("1.2"))
        {
            errorMSG("", "malformed frame received", msg, "accept version should be 1.2");
            return;
        }

        if (!lines.get("host").equals("stomp . cs . bgu . ac . il"))
        {
            errorMSG("","malformed frame received",msg,"host should be 'stomp . cs . bgu . ac . il'");
            return;
        }


        boolean acceptBool = false;
        boolean hostBool = false;
        boolean loginBool =false;
        boolean passBool = false;

        for (Map.Entry<String, String> entry:lines.entrySet())
        {
            switch (entry.getKey()) {
                case "accept - version":
                    if (acceptBool)
                    {  //error more than 1 time
                        errorMSG("", "malformed frame received", msg, "accept version line appears more than one time");
                        return;
                    } else
                        acceptBool = true;
                    break;
                case "host":
                    if (hostBool) {
                        //error more than 1 time
                        errorMSG("", "malformed frame received", msg, "host line appears more than one time");
                        return;
                    } else
                        hostBool = true;
                    break;
                case "login":
                    if (loginBool) {
                        //error more than 1 time
                        errorMSG("", "malformed frame received", msg, "login line appears more than one time");
                        return;
                    } else
                        loginBool = true;
                    break;
                case "passcode":
                    if (passBool) {
                        //error more than 1 time
                        errorMSG("", "malformed frame received", msg, "passcode line appears more than one time");
                        return;
                    } else
                        passBool = true;
                    break;
                default:
                    //error line
                    errorMSG("", "malformed frame received", msg, "line" + entry + "should not be here");
                    return;
            }
        }

        if (!acceptBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains accept version line");
            return;
        }

        if (!hostBool) {
            errorMSG("","malformed frame received",msg,"does not contains host line");
            return;
        }

        if (!loginBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains login line");
            return;
        }


        if (!passBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains passcode line");
            return;
        }


        String ch = connections.connect(lines.get("login"),lines.get("passcode"),connectionId);
        if (!ch.equals("GOOD"))
        {
            errorMSG("","connect problem",msg,ch);
            return;
        }


        String out_msg = "CONNECTED\n" +
                "version:1.2\n"
                +
                "WHAT";

        ch = connections.send(connectionId,out_msg);
        if(!ch.equals("GOOD"))
            errorMSG("","send problem",msg,ch);
    }

    private void send(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");
        String lastIndent = "";
        boolean desc = false;
        for (int i=0;i<pairs.length;i++) {

            String pair = pairs[i];

            if (pair.equals("") && (i==0||i==2))
                continue;

            if (pair.equals("^@"))
                break;


            if (pair.charAt(0)=='\t' | desc)
            {
                lines.put(lastIndent,lines.get(lastIndent)+"\n"+pair);
                continue;
            }

            if (!pair.contains(":")&& pair.charAt(0)!='"')
            {
                errorMSG("", "malformed frame received", msg, "contains a illegal statement");
                return;
            }

            String[] keyValue = pair.split(":");
            if (keyValue.length==1)
                {
                    lastIndent = keyValue[0].trim();
                    desc = lastIndent.equals("description");

                    keyValue = new String[2];
                    keyValue[0]=lastIndent;
                    keyValue[1]="";
                }
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean destBool = false;
        boolean userBool = false;
        boolean eventBool = false;
        boolean timeBool = false;
        boolean generalGameBool = false;
        boolean teamAupBool = false;
        boolean teamBupBool = false;

        for (Map.Entry<String,String> entry : lines.entrySet()) {
            String temp= entry.getKey();
            switch (temp) {
                case "destination":
                    if (destBool) {
                        errorMSG("", "malformed frame received", msg, "user line appears more than one time");
                        return;
                    } else
                        destBool = true;
                    break;

                case "user":
                    if (userBool) {
                        errorMSG("", "malformed frame received", msg, "user line appears more than one time");
                        return;
                    } else
                        userBool = true;
                    break;

                case "event name":
                    if (eventBool) {
                        errorMSG("", "malformed frame received", msg, "event line appears more than one time");
                        return;
                    } else
                        eventBool = true;
                    break;

                case "time":
                    if (timeBool) {
                        errorMSG("", "malformed frame received", msg, "time line appears more than one time");
                        return;
                    } else
                        timeBool = true;
                    break;

                case "general game updates":
                    if (generalGameBool) {
                        errorMSG("", "malformed frame received", msg, "generalGameBool line appears more than one time");
                        return;
                    } else
                        generalGameBool = true;
                    break;

                case "team a updates":
                    if (teamAupBool) {
                        errorMSG("", "malformed frame received", msg, "team a updates line appears more than one time");
                        return;
                    } else
                        teamAupBool = true;
                    break;

                case "team b updates":
                    if (teamBupBool) {
                        errorMSG("", "malformed frame received", msg, "team b updated line appears more than one time");
                        return;
                    } else
                        teamBupBool = true;
                    break;

                case "description":
                    break;

                default:
                    errorMSG("", "malformed frame received", msg, "line - " + entry + "should not be here");
                    return;

            }
        }


        if (!destBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains destination line");
            return;
        }

        if (!userBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains user line");
            return;
        }

        if (!eventBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains event line");
            return;
        }

        if (!timeBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains time line");
            return;
        }

        if (!generalGameBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains general game updates line");
            return;
        }

        if (!teamAupBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains team a updates line");
            return;
        }

        if (!teamBupBool) {
            errorMSG("", "malformed frame received", msg, "does not contains team b updates line");
            return;
        }

        int i1 = msg.indexOf("user");
        int i2 = msg.indexOf("^@") -1;
        String msg_out = msg.substring(i1,i2);
        String ch = connections.send(lines.get("destination"),msg_out);

        if (!ch.equals("GOOD"))
            errorMSG("","send problem",msg,ch);
    }

    public void subscribe(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            
            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;
            
            if (pair.equals("^@"))
                break;

            if (!pair.contains(":"))
            {
                errorMSG("", "malformed frame received", msg, "contains a illegal statement");
                return;
            }
            
            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean idBool = false;
        boolean destBool = false;
        for (Map.Entry<String, String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("id"))
            {
                if (idBool) {//line of ID appear more than 1 time
                    errorMSG("","malformed frame received",msg,"id line appears more than one time");
                    return;
                }
                else
                    idBool = true;
            }
            else if (entry.getKey().equals("destination"))
            {
                if (destBool) {//line of destination appear more than 1 time
                    errorMSG("","malformed frame received",msg,"destination line appears more than one time");
                    return;
                }
                else
                    destBool = true;
            }
            else
            {// error line - does not suppose to be there
                errorMSG("","malformed frame received",msg,"line - "+ entry+"should not be here");
                return;
            }
        }

        if (!destBool) {// destination missing
            errorMSG("","malformed frame received",msg,"does not contains destination line");
            return;
        }

        if (!idBool) {// id missing
            errorMSG("","malformed frame received",msg,"does not contains id line");
            return;
        }

        String ch =connections.subscribe(lines.get("destination"),connectionId,Integer.parseInt(lines.get("id")));
        if (!ch.equals("GOOD")) {//error in subscribe
            errorMSG("","subscribe problem",msg,ch);
            return;
        }

        String msg_out = "RECEIPT\n" +
                "receipt-id:"+lines.get("id")+"\n"+"\n" +
                "^@";

        ch =connections.send(connectionId,msg_out);
        if(!ch.equals("GOOD"))
            errorMSG("","send problem",msg,ch);
    }

    private void unsubscribe(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {

            String pair = pairs[i];

            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;

            if (pair.equals("^@"))
                break;

            if (!pair.contains(":"))
            {
                errorMSG("", "malformed frame received", msg, "contains a illegal statement");
                return;
            }

            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean idBool = false;
        boolean recBool = false;

        for (Map.Entry<String,String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("id"))
            {
                if (idBool) {//id line appear more than 1 time
                    errorMSG("","malformed frame received",msg,"id line appears more than one time");
                    return;
                }
                else
                    idBool = true;
            }
            else if (entry.getKey().equals("receipt"))
            {
                if (recBool) {
                    errorMSG("","malformed frame received",msg,"receipt line appears more than one time");
                    return;
                }
                else
                    recBool = true;
            }
            else {//error line
                errorMSG("","malformed frame received",msg,"line"+entry+" - should not be here");
                return;
            }
        }

        if (!idBool) {
            errorMSG("","malformed frame received",msg,"does not contains id line");
            return;
        }

        if (!recBool)
        {
            errorMSG("","malformed frame received",msg,"does not contains receipt line");
            return;
        }

        String ch =connections.unsubscribe(connectionId,Integer.parseInt(lines.get("id")));
        if (!connections.unsubscribe(connectionId,Integer.parseInt(lines.get("id"))).equals("GOOD"))
        {
            errorMSG("","unsubscribe problem",msg,ch);
            return;
        }

        String msg_out =
                "RECEIPT\n" +
                        "receipt-id:"+lines.get("receipt")+"\n" +
                        "\n" +
                        "^@";

        ch = connections.send(connectionId,msg_out);
        if (!ch.equals("GOOD"))
            errorMSG("","send message problem",msg,ch);
    }

    private void disconnect(String msg)
    {
        Map<String,String> lines = new WeakHashMap<>();

        String[] pairs = msg.split("\n");

        for (int i=0;i<pairs.length;i++) {

            String pair = pairs[i];

            if (pair.equals("") && (i == pairs.length-2 | i==0))
                continue;

            if (pair.equals("^@"))
                break;

            if (!pair.contains(":"))
            {
                errorMSG("", "malformed frame received", msg, "contains a illegal statement");
                return;
            }

            String[] keyValue = pair.split(":");
            lines.put(keyValue[0].trim(), keyValue[1].trim());
        }

        boolean recBool = false;

        for (Map.Entry<String,String> entry:lines.entrySet())
        {
            if (entry.getKey().equals("receipt"))
            {
                if (recBool) {
                    errorMSG("","malformed frame received",msg,"receipt line appears more than one time");
                    return;
                }
                else
                    recBool = true;
            }
            else
                //error line
            {
                errorMSG("","malformed frame received",msg,"line"+entry+"  - should not be here");
                return;
            }
        }


        if (!recBool) {
            errorMSG("","malformed frame received",msg,"does not contains receipt line");
            return;
        }

        String ch =connections.disconnect(connectionId);
        if (!ch.equals("GOOD"))
        {
            errorMSG("","disconnect problem",msg,ch);
            return;
        }


        String msg_out = "RECEIPT\n" +
                "receipt - id :"+lines.get("receipt")+"\n" +
                "\n" +
                "^ @";

        ch = connections.send(connectionId,msg_out);
        if (ch.equals("GOOD"))
            shouldTerminate = true;
        else
            errorMSG("","send problem",msg,ch);

    }

    public void errorMSG(String receipt,String errorMSG,String message,String detail)
    {
        String msg_out ="ERROR\n";
        if(!receipt.equals("")) {
            msg_out = msg_out + "receipt-id: " + receipt + "\n";
        }
            msg_out= msg_out+ "message: " + errorMSG + "\n"+"\n";
        if(!message.equals("")){
            msg_out= msg_out+ "The message:\n" +"----\n"+ message+"+"+"----\n";
        }
        if(!detail.equals("")){
            msg_out= msg_out+ detail+"\n";
        }
        msg_out= msg_out+"^@";

        connections.send(connectionId,msg_out);
        shouldTerminate= true;
        connections.forceDisconnect(connectionId);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
