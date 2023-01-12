#include "../include/StompProtocol.h"
#include "../include/task.h"
#include <vector>
#include <iostream>
#include <fstream>

int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client
	StompProtocol s;
	std::mutex mutex;
	Task t(mutex,s);

	std::thread th1(&Task::keyboard,&t);
	std::thread th2(&Task::socket,&t);

	th1.join();
	th2.join();
	
	
	
	return 0;
}
