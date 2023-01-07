package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.*;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StompServer {

    public static void main(String[] args) {

        // TODO: implement this
        StompMessagingProtocolIMPL stomp = new StompMessagingProtocolIMPL();
        Connections <String> connections = new ConnectionsIMPL();
        BlockingConnectionHandler blockingConnectionHandler = new BlockingConnectionHandler(new Socket(),new StompEncoderDecoder(),stomp,connections);


        String msg = "CONNECT\n" +
                "accept - version :1.2\n" +
                "host : stomp . cs . bgu . ac . il\n" +
                "login : meni\n" +
                "passcode : films\n" +
                "\n" +
                "^ @";

        stomp.process(msg);

        msg = "SEND\n" +
                "destination :/ germany_japan\n" +
                "\n" +
                "user : meni\n" +
                "event name : goal !!!!\n" +
                "time : 1980\n" +
                "general game updates :\n" +
                "team a updates :\n" +
                "goals : 1\n" +
                "possession : 90%\n" +
                "team b updates :\n" +
                "possession : 10%\n" +
                "description :\n" +
                "\" GOOOAAALLL !!! Germany lead !!! Gundogan finally has\n" +
                "success in the box as he steps up to take the\n" +
                "penalty , sends Gonda the wrong way , and slots the\n" +
                "ball into the left - hand corner to put Germany 1 -0\n" +
                "up ! A needless penalty to concede from Japan ’ s point\n" +
                "of view , and after a bright start , the Samurai Blues\n" +
                "trail !\"\n" +
                "^ @";
        stomp.process(msg);

        msg = "SUBSCRIBE\n" +
                "id : 17\n" +
                "destination : / dest\n" +
                "\n" +
                "^ @";

        stomp.process(msg);

        msg = "UNSUBSCRIBE\n" +
                "id :17\n" +
                "receipt :913\n" +
                "\n"+
                "^ @";

        stomp.process(msg);

    }
}
