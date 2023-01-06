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

//    private Map<Integer,String> id_to_user;  //<id user,user_name>




    public ConnectionsIMPL()
    {
        id_counter=0;
        clients_ConnectionHandler = new WeakHashMap<>();
        topics = new WeakHashMap<>();
        users = new WeakHashMap<>();
        users_cond = new WeakHashMap<>();
//        id_to_user = new WeakHashMap<>();
    }

    public void create_ConnectionHandler(int clientId,ConnectionHandler connectionHandler)
    {
        clients_ConnectionHandler.put(clientId,connectionHandler);
//        id_to_user.put(clientId,userName);
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if (clients_ConnectionHandler.containsKey(connectionId))
        {
            clients_ConnectionHandler.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void send(String channel, T msg) {

        if (!topics.containsKey(channel))
            return;

        List ids = topics.get(channel);
        Iterator<Point> iterator = ids.iterator();

        while (iterator.hasNext())
        {
            clients_ConnectionHandler.get(iterator.next().x).send(msg);
        }
    }

    public boolean subscribe(String channel,int connectionId,int subId)
    {
        if (!users_cond.containsKey(connectionId))
            // user with such connectionId does not exist
            return false;

        if (!users_cond.get(connectionId))
            // user is disconnected
            return false;

        if (!topics.containsKey(channel))
            // topic does not exist
            topics.put(channel,new ArrayList<>());

        List <Point> check = topics.get(channel);

        if (isContainsX(check,connectionId))
            // already subscribes to this chanel
            return false;

        check.add(new Point(connectionId,subId));
        return true;
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
    public void disconnect(int connectionId) {
        for (List<Point> list: topics.values())
        {
            for (Point point:list)
            {
                if (point.x == (connectionId))
                    list.remove(point);
            }
        }

        users_cond.put(connectionId,false);
        clients_ConnectionHandler.remove(connectionId);
//        users_cond.put(id_to_user.get(connectionId),false);
    }



    public int getId() {
        return id_counter++;
    }


    @Override
    public boolean connect(String user_name, String password,int connectionId) {
        if (users_cond.containsKey(connectionId) &&users_cond.get(connectionId))
            //user is already connected
            return false;

        if (users.containsKey(user_name))
            //user already exist
            if (users.get(user_name).equals(password))
                {
                    users_cond.put(connectionId,true);
                    return true;
                }
            else
                //wrong password
                return false;

        else
        {
            //new user
            users.put(user_name,password);
            users_cond.put(connectionId,true);
            return true;
        }
    }
}
