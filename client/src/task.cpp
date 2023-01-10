#include "../include/task.h"
#include <vector>
#include <iostream>
#include <fstream>

void Task::keyboard(StompProtocol &s)
{
    int counter = 0;
	while(1)
	{

    if(counter ==1)
        s.connect();

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
			s.reset();
			stop = true;
		}
		else
		{
			if (!s.getConnection()->sendLine(ans[i])) 
			{
               std::cout << "Disconnected. Exiting...\n" << std::endl;
               break;
			}
        }
		}
        //////////////////////////////////////// not suppose to be here

        std::string answer;
        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end

        if (!s.getConnection()->getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std::cout <<answer;

        ++counter;
	}
}