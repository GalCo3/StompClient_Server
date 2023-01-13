package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.echo.EchoProtocol;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.*;

import javax.lang.model.util.ElementScanner6;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StompServer {

    public static void main(String[] args) {
        if(args.length == 0)
        {
            Server.reactor(
                         Runtime.getRuntime().availableProcessors(),
                         7777, //port
                         () -> new StompMessagingProtocolIMPL(), //protocol factory
                         StompEncoderDecoder::new, new ConnectionsIMPL<>() //message encoder decoder factory
                 ).serve();
        }
        else if(args.length == 2)
        {
            if(args[1].equals("tpc"))
            {
               Server.threadPerClient(
                Integer.parseInt(args[0]), //port
               () -> new StompMessagingProtocolIMPL(), //protocol factory
               StompEncoderDecoder::new ,new ConnectionsIMPL<>()//message encoder decoder factory
               ).serve();
            }
            else if(args[1].equals("reactor"))
            {
                Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    Integer.parseInt(args[0]), //port
                    () -> new StompMessagingProtocolIMPL(), //protocol factory
                    StompEncoderDecoder::new, new ConnectionsIMPL<>() //message encoder decoder factory
                ).serve(); 
            }
        }

    }
}
