#include "../include/StompProtocol.h"

StompProtocol::StompProtocol():subId(0),receiptId(0)
{}
std::string StompProtocol::execute(std::string frame)
{
    std::string::size_type pos = frame.find(' ');
    std::string action = frame.substr(0,pos);
    frame = frame.substr(pos+1);
    if (action == "login")
    {
        connect(frame);
    }
    else if (action == "join")
    {
        /* code */
    }
    else if (action == "exit")
    {
        /* code */
    }
    else if (action == "report")
    {
        /* code */
    }
    else if (action == "summery")
    {
        /* code */
    }
    else if (action == "logout")
    {
        /* code */
    }
    
    return "";
}

std::string StompProtocol::connect(std::string frame)
{
    bool found = frame.find(' ') != std::string::npos;
    if(!found)
        //error
    return"";


    std::string::size_type pos = frame.find(' ');
    std::string host_port = frame.substr(0,pos);
    frame= frame.substr(pos+1);

    pos = host_port.find(':');
    found = pos != std::string::npos;
    
    if (found)
    {
        std::string host = host_port.substr(0,pos);
        std::string port = host_port.substr(pos+1);
        if (host.length() == 0 | port.length() == 0 )
        {
            //error
            return "";
        }
        
    }
    else
    {
        //error
    }
    pos = frame.find(' ');
    found = pos != std::string::npos;
    if(!found)
        //error
    return"";

    std::string username = frame.substr(0,pos);
    frame = frame.substr(pos+1);
    
    std::string out("CONNECT\naccept - version :1.2\nhost : stomp . cs . bgu . ac . il\nlogin:"+ username+
    "passcode:"+frame+"\n\n"+'\0');
    

    std::cout << out;
    return out;
}