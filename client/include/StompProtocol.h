#pragma once

#include "../include/ConnectionHandler.h"
#include "../include/event.h"
#include <string>
#include <vector>
#include <queue>
// TODO: implement the STOMP protocol
class StompProtocol
{
private:
    int subId;
    int receiptId;
    bool isConnected;
    std::string userName;
    std::map<std::string,int> game_subId;
    std::map<std::pair<std::string,std::string>,std::vector<Event>> & summeryMap; //<userName,gameName> -- > summery
    std::queue<std::string> queue;
    ConnectionHandler con;
    std::string connect(std::string frame);
    std::string subcscribe(std::string frame);
    std::string unsubscribe(std::string frame);
    std::vector<std::string> send(std::string frame);
    void summary(std::string frame);
    std::string disconnect(std::string frame);
    std::string errorMSG(std::string frame);
public:
    StompProtocol(std::map<std::pair<std::string,std::string>,std::vector<Event>>* vmap);
    std::vector<std::string> execute(std::string frame);
    void connect();
    void disconnect();
    std::string getLastCommand(); 
    void reset();
    ConnectionHandler* getConnection();
    bool is_Connected();
    void addMessage(std::pair<std::string,std::string> user_game);
};
