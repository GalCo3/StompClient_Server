#include "../include/StompProtocol.h"
#include "../include/task.h"


int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client
	std::map<std::pair<std::string,std::string>,std::vector<Event>> user_game;
	StompProtocol s(&user_game);
	Task t;
	t.keyboard(s);
	return 0;
}
