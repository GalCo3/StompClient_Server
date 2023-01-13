package bgu.spl.net.srv;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsIMPL<T> implements Connections<T> {

    private int id_counter;
    private int message_id;
    private Map<Integer,ConnectionHandler<T>> clients_ConnectionHandler; // connectionId --> ConnectionHandler
    private Map<String,List<Point>> topics; //topic ---> list of connectionId's that subscribed to this chanel <string chanel,list<connectionId,subId>>

    //Maps for users
    private Map<String,String> users; //<username,password>
    private Map<String,Boolean> users_cond; //if connected or not  //<username,isConnected>
    private Map<Integer,String> user_conId; // <ConnectionId,username>

    private Object lock_clients_ConnectionHandler;
    private Object lock_topics;
    private Object lock_users;
    private Object lock_users_cond;
    private Object lock_user_conId;



    public ConnectionsIMPL()
    {
        id_counter=0;
        message_id=0;
        clients_ConnectionHandler = new ConcurrentHashMap<>();
        topics = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        users_cond = new ConcurrentHashMap<>();
        user_conId = new ConcurrentHashMap<>();

        lock_clients_ConnectionHandler= new Object();
        lock_topics= new Object();
        lock_users=new Object() ;
        lock_users_cond=new Object();
        lock_user_conId=new Object();

    }

    public void create_ConnectionHandler(int clientId,ConnectionHandler<T> connectionHandler)
    {
        synchronized(clients_ConnectionHandler){
        clients_ConnectionHandler.put(clientId,connectionHandler);
    }
    }

    @Override
    public String send(int connectionId, T msg) {
        synchronized(clients_ConnectionHandler){  
        if (clients_ConnectionHandler.containsKey(connectionId))
        {
            clients_ConnectionHandler.get(connectionId).send(msg);
            return "GOOD";
        }
        return "User with such connectionID does not exist";
        }
    }

    @Override
    public String send(String channel,int connectionId) {
        synchronized(lock_topics){    
        if (!topics.containsKey(channel))
            return "Channel does not exist";

        if(!isContainsX(topics.get(channel), connectionId))
            return "User is not subscried to this channel";
            
        return "GOOD";
        }
    }

    public Iterator<Point> getLisIterator(String channel)
    {
        synchronized(lock_topics)
        {
            List<Point> ids = topics.get(channel);
            return ids.iterator();       
        }
    }
    @Override
    public String subscribe(String channel,int connectionId,int subId)
    {
        synchronized(lock_topics){ 
            synchronized(lock_users_cond){ 
        if (!users_cond.containsKey(user_conId.get(connectionId)))
            // user with such connectionId does not exist
            return "User with such connection id does not exist";

        if (!users_cond.get(user_conId.get(connectionId)))
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
        }
    }

    @Override
    public String unsubscribe(int connectionId,int subId)
    {
        synchronized(lock_topics){ 
            synchronized(lock_users_cond){ 
        if (!users_cond.containsKey(user_conId.get(connectionId)))
            return "User with such connection id does not exist";

        if (!users_cond.get(user_conId.get(connectionId)))
            return "User is disconnected already";

        Point check = new Point(connectionId,subId);

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
         }
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
    public String disconnect(int connectionId,T msg) {

        synchronized(lock_clients_ConnectionHandler){ 
            synchronized(lock_topics){ 
                synchronized(lock_users_cond){ 
                    synchronized(lock_user_conId){ 
                        if (!users_cond.containsKey(user_conId.get(connectionId)))
                            return "User with such connection id does not exist";

                        if (!users_cond.get(user_conId.get(connectionId)))
                            return "User is disconnected already";

                        for (List<Point> list: topics.values())
                        {
                            list.removeIf(point -> point.x == (connectionId));
                        }

                        String send = send(connectionId,msg);
                        if (!send.equals("GOOD"))
                            return send;
                        users_cond.put(user_conId.get(connectionId),false);
                        user_conId.remove(connectionId);
                        clients_ConnectionHandler.remove(connectionId);
                        return "GOOD";
                    }
                }
            }
         }
    }

    public void forceDisconnect(int connectionId)
    {
        synchronized(lock_clients_ConnectionHandler){ 
            synchronized(lock_topics){ 
                synchronized(lock_users_cond){ 
                    synchronized(lock_user_conId){ 
                        for (List<Point> list: topics.values())
                        {
                            list.removeIf(point -> point.x == (connectionId));
                        }
                        
                        if(user_conId.containsKey(connectionId))
                            users_cond.put(user_conId.get(connectionId),false);
                        if(user_conId.containsKey(connectionId))
                            user_conId.remove(connectionId);

                        if(clients_ConnectionHandler.containsKey(connectionId))
                            clients_ConnectionHandler.remove(connectionId);
                    }
                }
            }
        }
    }

    public int getId() {
        return id_counter++;
    }
    public int getMessageId()
    {
        return message_id++;
    }

    @Override
    public String connect(String user_name, String password,int connectionId) {
        synchronized(lock_users){
            synchronized(users_cond){
                synchronized(lock_user_conId){
                    if (users_cond.containsKey(user_name) &&users_cond.get(user_name))
                        //user is already connected
                        return "User already logged in";

                    if (users.containsKey(user_name))
                        //user already exist
                        if (users.get(user_name).equals(password))
                            {
                                users_cond.put(user_name,true);
                                user_conId.put(connectionId, user_name);
                                return "GOOD";
                            }
                        else
                            //wrong password
                            return "Wrong password";

                    else
                    {
                        //new user
                        users.put(user_name,password);
                        users_cond.put(user_name,true);
                        user_conId.put(connectionId, user_name);
                        return "GOOD";
                    }
                }
            }
        }
    }
}
