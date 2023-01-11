package bgu.spl.net.srv;

//import com.sun.jdi.request.ThreadStartRequest;

import java.io.IOException;
import java.util.Iterator;
import java.awt.*;
import java.util.*;

public interface Connections<T> {

    String send(int connectionId, T msg);

    String send(String channel, int connectionId);

    String disconnect(int connectionId,T msg);

    String connect(String user_name, String password, int connectionId);

    String subscribe(String channel,int connectionId,int subId);
    String unsubscribe(int connectionId,int subId);

    int getId();

    void create_ConnectionHandler(int clientId,ConnectionHandler<T> connectionHandler);
    Iterator<Point> getLisIterator(String channel);
}
