package bgu.spl.net.srv;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void send(String channel, T msg);

    void disconnect(int connectionId);

    boolean connect(String user_name,String password,int connectionId);

    int getId();

    void create_ConnectionHandler(int clientId,ConnectionHandler connectionHandler);
}
