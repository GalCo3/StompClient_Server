#include "../include/StompProtocol.h"

int main(int argc, char *argv[]) {
	// TODO: implement the STOMP client
	StompProtocol s;
	s.execute("login 122:12 meni pass");
	return 0;
}