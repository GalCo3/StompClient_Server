#pragma once
#include "StompProtocol.h"

class Task
{
    private:
        std::mutex &_mutex;
        StompProtocol &s;
    public:
        Task(std::mutex &mutex,StompProtocol &s);
        void keyboard();
        void socket();

};