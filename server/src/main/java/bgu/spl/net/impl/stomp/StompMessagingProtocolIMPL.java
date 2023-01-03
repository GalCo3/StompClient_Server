package bgu.spl.net.impl.stomp;
import  bgu.spl.net.*;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;

public class StompMessagingProtocolIMPL implements StompMessagingProtocol<String> {

    private int connectionId;
    private Connections<String> connections;

    private boolean shouldTerminate = false;


    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(String message) {
        int index = message.indexOf('\n');
        String action = message.substring(0,index);
        message = message.substring(index);

        if(action.equals("CONNECT"))
        {

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

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
