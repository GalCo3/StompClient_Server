package bgu.spl.net.impl.stomp;

public class StompServer {

    public static void main(String[] args) {
        // TODO: implement this
        StompMessagingProtocolIMPL stomp = new StompMessagingProtocolIMPL();
        String msg = "CONNECT\n" +
                "accept - version :1.2\n" +
                "host : stomp . cs . bgu . ac . il\n" +
                "login : meni\n" +
                "passcode : films\n" +
                "^ @";
        stomp.process(msg);
    }
}
