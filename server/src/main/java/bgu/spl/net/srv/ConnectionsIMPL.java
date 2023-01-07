package bgu.spl.net.srv;

import java.awt.*;
import java.util.*;
import java.util.List;


public class ConnectionsIMPL<T> implements Connections<T> {

    private int id_counter;

    private Map<Integer,ConnectionHandler> clients_ConnectionHandler; // connectionId --> ConnectionHandler
    private Map<String,List<Point>> topics; //topic ---> list of connectionId's that subscribed to this chanel <string chanel,list<connectionId,subId>>

    //Maps for users
    private Map<String,String> users; //<username,password>
    private Map<Integer,Boolean> users_cond; //if connected or not  //<connectionId,isConnected>



    public ConnectionsIMPL()
    {
        id_counter=0;
        clients_ConnectionHandler = new WeakHashMap<>();
        topics = new WeakHashMap<>();
        users = new WeakHashMap<>();
        users_cond = new WeakHashMap<>();
    }

    public void create_ConnectionHandler(int clientId,ConnectionHandler connectionHandler)
    {
        clients_ConnectionHandler.put(clientId,connectionHandler);
    }

    @Override
    public String send(int connectionId, T msg) {
        if (clients_ConnectionHandler.containsKey(connectionId))
        {
            clients_ConnectionHandler.get(connectionId).send(msg);
            return "GOOD";
        }
        return "User with such connectionID does not exist";
    }

    @Override
    public String send(String channel, T msg) {

        if (!topics.containsKey(channel))
            return "Channel does not exist";

        List ids = topics.get(channel);
        Iterator<Point> iterator = ids.iterator();

        while (iterator.hasNext())
        {
            clients_ConnectionHandler.get(iterator.next().x).send(msg);
        }
        return "GOOD";
    }
    @Override
    public String subscribe(String channel,int connectionId,int subId)
    {
        if (!users_cond.containsKey(connectionId))
            // user with such connectionId does not exist
            return "User with such connection id does not exist";

        if (!users_cond.get(connectionId))
            // user is disconnected
            return "User is disconnected already";

        if (!topics.containsKey(channel))
            // topic does not exist
            topics.put(channel,new ArrayList<>());

        List <Point> check = topics.get(channel);

        if (isContainsX(check,connectionId))
            // already subscribes to this chanel
            return "User is subscribes to this chanel already";

        check.add(new Point(connectionId,subId));
        return "GOOD";
    }

    @Override
    public String unsubscribe(int connectionId,int subId)
    {
        if (!users_cond.containsKey(connectionId))
            return "User with such connection id does not exist";

        if (!users_cond.get(connectionId))
            return "User is disconnected already";

        Point check = new Point(connectionId,subId);
        boolean deleted = false;

        for (List <Point> list: topics.values())
        {
            if (list.contains(check))
            {
                list.remove(check);
                return "GOOD";
            }
        }

        // error, does not subscribe to any channel
        return "Can't unsubscribe because user is not subscribe to any channel";
    }

    private boolean isContainsX(List<Point> p ,int connectionId)
    {
        for (Point point :p)
        {
         if (point.x == connectionId)
             return true;
        }
        return false;
    }


    @Override
    public String disconnect(int connectionId) {

        if (users_cond.containsKey(connectionId))
            return "User with such connection id does not exist";

        if (!users_cond.get(connectionId))
            return "User is disconnected already";

        for (List<Point> list: topics.values())
        {
            list.removeIf(point -> point.x == (connectionId));
        }

        users_cond.put(connectionId,false);
        clients_ConnectionHandler.remove(connectionId);
        return "GOOD";
    }

    public int getId() {
        return id_counter++;
    }

    @Override
    public String connect(String user_name, String password,int connectionId) {
        if (users_cond.containsKey(connectionId) &&users_cond.get(connectionId))
            //user is already connected
            return "User is already connected";

        if (users.containsKey(user_name))
            //user already exist
            if (users.get(user_name).equals(password))
                {
                    users_cond.put(connectionId,true);
                    return "GOOD";
                }
            else
                //wrong password
                return "Wrong password";

        else
        {
            //new user
            users.put(user_name,password);
            users_cond.put(connectionId,true);
            return "GOOD";
        }
    }
}
