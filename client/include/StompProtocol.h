#pragma once

#include "../include/ConnectionHandler.h"
#include <string>
// TODO: implement the STOMP protocol
class StompProtocol
{
private:
    int subId;
    int receiptId;
    std::string connect(std::string frame);
    std::string subcscribe(std::string frame);
    std::string unsubscribe(std::string frame);
    std::string send(std::string frame);
    std::string summery(std::string frame);
    std::string disconnect(std::string frame);
    std::string errorMSG(std::string frame);
public:
    StompProtocol();
    std::string execute(std::string frame);
    

};
