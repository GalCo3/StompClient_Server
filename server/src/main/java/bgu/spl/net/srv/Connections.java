package bgu.spl.net.srv;

//import com.sun.jdi.request.ThreadStartRequest;

import java.io.IOException;

public interface Connections<T> {

    String send(int connectionId, T msg);

    String send(String channel, T msg);

    String disconnect(int connectionId);

    String connect(String user_name, String password, int connectionId);

    String subscribe(String channel,int connectionId,int subId);
    String unsubscribe(int connectionId,int subId);

    int getId();

    void create_ConnectionHandler(int clientId,ConnectionHandler<T> connectionHandler);
}
