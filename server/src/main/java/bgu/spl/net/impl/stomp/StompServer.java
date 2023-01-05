package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.*;

import java.net.Socket;

public class StompServer {

    public static void main(String[] args) {

        // TODO: implement this
        StompMessagingProtocolIMPL stomp = new StompMessagingProtocolIMPL();
        Connections connections = new ConnectionsIMPL();
//        int id = connections.getId();
        BlockingConnectionHandler blockingConnectionHandler = new BlockingConnectionHandler(new Socket(),new StompEncoderDecoder(),stomp,connections);



//        stomp.start(blockingConnectionHandler.getConnectionId(), connections);

        String msg = "CONNECT\n" +
                "accept - version :1.2\n" +
                "host : stomp . cs . bgu . ac . il\n" +
                "login : meni\n" +
                "passcode : films\n" +
                "^ @";
        stomp.process(msg);
    }
}
