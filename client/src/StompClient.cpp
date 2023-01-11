#include "../include/StompProtocol.h"
#include "../include/task.h"


int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client
	std::map<std::pair<std::string,std::string>,std::vector<Event>> user_game;
	StompProtocol s(&user_game);
	std::mutex mutex;
	Task t(mutex,s);

	std::thread th1(&Task::keyboard,&t);
	std::thread th2(&Task::socket,&t);

	th1.join();
	th2.join();
	
	return 0;
}
