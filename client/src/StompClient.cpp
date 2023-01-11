#include "../include/StompProtocol.h"
#include "../include/task.h"


int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client
	StompProtocol s;
	std::mutex mutex;
	Task t(mutex,s);

	std::thread th1(&Task::keyboard,&t);
	std::thread th2(&Task::socket,&t);

	th1.join();
	th2.join();
	
	// std::string frame_body = "team a: Germany\nteam b: Japan\nevent name: kickoff\ntime: 0\ngeneral game updates:\n\tactive: true\nteam a updates:\nteam b updates:\ndescription:\nThe game has started! What an exciting evening!\n";

	// std::string::size_type pos = frame_body.find(':');
    // std::string team_a_name = frame_body.substr(pos+2);
    // std::string::size_type pos1 = team_a_name.find('\n');
    // team_a_name = team_a_name.substr(0,pos1);

	// pos1 = frame_body.find('\n');
    // frame_body = frame_body.substr(pos1+1);
	return 0;
}
