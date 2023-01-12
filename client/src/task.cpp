#include "../include/task.h"
#include <vector>
#include <iostream>
#include <fstream>

Task::Task(std::mutex &mutex,StompProtocol& stomp):_mutex(mutex),s(stomp)
{}

void Task::keyboard()
{
	while(1)
	{
	if (!s.getWait())
	{
		
	const short bufsize = 1024;
	char buf[bufsize];
	std::cin.getline(buf, bufsize);
	std::string line(buf);

	std::vector<std::string> ans = s.execute(line);
	
	unsigned int vecSize = ans.size();
	bool stop = false;
    for(unsigned int i = 0; i < vecSize && stop != true; i++)
	{
        
		std::string::size_type pos = ans[i].find(' ');
		if(ans[i].substr(0,pos).find("error")!=std::string::npos)
		{
			stop = true;
			std::cout<<ans[i]+"\n" <<std::endl;
			s.reset();
		}
		else
		{
			if (!s.getConnection()->sendLine(ans[i])) 
			{
               std::cout << "Disconnected. Exiting...\n" << std::endl;
			   s.reset();
               break;
			}
        }
		}
	}
	}
}

void Task::socket()
{
	while(1)
	{
		if (s.is_Connected())
		{
			std::string answer;
        	// Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
     		// We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end

        	if (!s.getConnection()->getLine(answer)) {
				std::cout << "Disconnected. Exiting...\n" << std::endl;
				s.reset();
        	}
        	// std::cout <<answer;
			std::string::size_type pos = answer.find('\n');
			std::string action= answer.substr(0,pos);
			std::string last;
			pos = last.find(' ');
			if(action == "CONNECTED")
				std::cout << "Login successful\n" << std::endl;
			else if (action =="RECEIPT") 
			{
				last = s.getLastCommand();
				pos = last.find(' ');
				if (last.substr(0,pos) == "join")
				{
					std::cout << "Joined channel " + last.substr(pos+1)+"\n" << std::endl;
				}
				else if (last.substr(0,pos)=="exit")
				{					
					std::cout << "Exited channel " + last.substr(pos+1)+"\n" << std::endl;
				}
				else if (last.substr(0,pos) == "logout")
				{
					s.recived();
					std::cout << "logged out\n" << std::endl;
					s.reset();
				}
			}
			else if (action == "MESSAGE")
			{
				std::string::size_type pos1 = answer.find("destination:");
				std::string gameDest = answer.substr(pos1);
				pos1 = gameDest.find(':');
				gameDest = gameDest.substr(pos1+1);
				pos1 = gameDest.find('\n');
				gameDest = gameDest.substr(0,pos1);

				pos1 = answer.find("user:");
				std::string user = answer.substr(pos1+1);
				pos1 = user.find(':');
				user = user.substr(pos1+1);
				pos1 = user.find('\n');
				user = user.substr(0,pos1);

				///build event//
				pos1 = answer.find("user:");
				std::string toEvent = answer.substr(pos1);
				pos1 = toEvent.find('\n');
				toEvent = toEvent.substr(pos1+1);

				s.addMessage(std::pair<std::string,std::string>(user,gameDest),Event(toEvent));
				////////to sync/////
				std::cout<<answer <<std::endl;
			}
			else if (action == "ERROR")
			{
				s.reset();
				std::cout << answer << std::endl;
				std::cout << "forced logged out\n" << std::endl;
			}
			
			else
				std::cout << answer << std::endl;
			
			std::cout << "---------------------------------" << std::endl;
		}
		
		
	}
}