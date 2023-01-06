package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.*;

import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StompServer {

    public static void main(String[] args) {

        // TODO: implement this
        StompMessagingProtocolIMPL stomp = new StompMessagingProtocolIMPL();
        Connections connections = new ConnectionsIMPL();
        BlockingConnectionHandler blockingConnectionHandler = new BlockingConnectionHandler(new Socket(),new StompEncoderDecoder(),stomp,connections);


        String msg = "CONNECT\n" +
                "accept - version :1.2\n" +
                "host : stomp . cs . bgu . ac . il\n" +
                "login : meni\n" +
                "passcode : films\n" +
                "\n" +
                "^ @";

        stomp.process(msg);

        msg = "SUBSCRIBE\n" +
                "id : 1\n" +
                "destination : / dest\n" +
                "\n" +
                "^ @";

        stomp.process(msg);


    }
}
