package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.echo.EchoProtocol;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.*;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StompServer {

    public static void main(String[] args) {
        Server.reactor(
                         Runtime.getRuntime().availableProcessors(),
                         7777, //port
                         () -> new StompMessagingProtocolIMPL(), //protocol factory
                         StompEncoderDecoder::new, new ConnectionsIMPL<>() //message encoder decoder factory
                 ).serve();

    }
}
