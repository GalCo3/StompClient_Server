package bgu.spl.net.srv;

import java.util.*;


public class ConnectionsIMPL<T> implements Connections<T> {

    private int id_counter;

    Map<Integer,ConnectionHandler> clients_ConnectionHandler;


    Map<String,List<Integer>> topics;

    public ConnectionsIMPL()
    {
        id_counter=0;
        clients_ConnectionHandler = new WeakHashMap<>();
        topics = new WeakHashMap<>();
    }
    @Override
    public boolean send(int connectionId, T msg) {

        clients_ConnectionHandler.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void send(String channel, T msg) {
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

        clients_ConnectionHandler.remove(connectionId);
    }

    public void addToMap(int clientId,ConnectionHandler connectionHandler)
    {
        clients_ConnectionHandler.put(clientId,connectionHandler);
    }

    public int getId_counter() {
        return id_counter++;
    }
}
