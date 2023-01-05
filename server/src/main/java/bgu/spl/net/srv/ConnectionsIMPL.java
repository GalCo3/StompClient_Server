package bgu.spl.net.srv;

import java.awt.image.ImageProducer;
import java.util.*;


public class ConnectionsIMPL<T> implements Connections<T> {

    private int id_counter;

    private Map<Integer,ConnectionHandler> clients_ConnectionHandler;
    private Map<String,List<Integer>> topics;

    //Maps for users
    private Map<String,String> users; //<username,password>

    private Map<Integer,Boolean> users_cond; //if connected or not  //<username,isConnected>

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
        Iterator<Integer> iterator = ids.iterator();

        while (iterator.hasNext())
        {
            clients_ConnectionHandler.get(iterator.next()).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        for (List<Integer> list: topics.values())
        {
          if (list.contains(connectionId))
              list.remove(connectionId);
        }

        users_cond.put(connectionId,false);
        clients_ConnectionHandler.remove(connectionId);
//        users_cond.put(id_to_user.get(connectionId),false);

    }

    public void create_ConnectionHandler(int clientId,ConnectionHandler connectionHandler)
    {
        clients_ConnectionHandler.put(clientId,connectionHandler);
//        id_to_user.put(clientId,userName);
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
