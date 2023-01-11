#include "../include/event.h"
#include "../include/json.hpp"
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <sstream>
using json = nlohmann::json;

Event::Event(std::string team_a_name, std::string team_b_name, std::string name, int time,
             std::map<std::string, std::string> game_updates, std::map<std::string, std::string> team_a_updates,
             std::map<std::string, std::string> team_b_updates, std::string discription)
    : team_a_name(team_a_name), team_b_name(team_b_name), name(name),
      time(time), game_updates(game_updates), team_a_updates(team_a_updates),
      team_b_updates(team_b_updates), description(discription)
{
}

Event::~Event()
{
}

const std::string &Event::get_team_a_name() const
{
    return this->team_a_name;
}

const std::string &Event::get_team_b_name() const
{
    return this->team_b_name;
}

const std::string &Event::get_name() const
{
    return this->name;
}

int Event::get_time() const
{
    return this->time;
}

const std::map<std::string, std::string> &Event::get_game_updates() const
{
    return this->game_updates;
}

const std::map<std::string, std::string> &Event::get_team_a_updates() const
{
    return this->team_a_updates;
}

const std::map<std::string, std::string> &Event::get_team_b_updates() const
{
    return this->team_b_updates;
}

const std::string &Event::get_discription() const
{
    return this->description;
}

Event::Event(std::string frame_body) : team_a_name(""), team_b_name(""), name(""), time(0), game_updates(), team_a_updates(), team_b_updates(), description("")
{
    std::string::size_type pos = frame_body.find(':');
    team_a_name = frame_body.substr(pos+2);
    std::string::size_type pos1 = team_a_name.find('\n');
    team_a_name = team_a_name.substr(0,pos1);

	pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    //////////////////////////////////////////////////////
    pos = frame_body.find(':');
    team_b_name = frame_body.substr(pos+2);
    pos1 = team_b_name.find('\n');
    team_b_name = team_b_name.substr(0,pos1);

	pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    //////////////////////////////////////////////////////
    pos = frame_body.find(':');
    name= frame_body.substr(pos+2);
    pos1 = name.find('\n');
    name = name.substr(0,pos1);

	pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    //////////////////////////////////////////////////////
    pos = frame_body.find(':');
    std::string key;
    key= frame_body.substr(pos+2);
    pos1 = key.find('\n');
    key = key.substr(0,pos1);
    time = stoi(key);

	pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    //////////////////////////////////////////////////////
    pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    std::string val;
    std::string temp;
    while (frame_body.at(0) == '\t')
    {
        pos = frame_body.find('\n');
        temp = frame_body.substr(1,pos-1);
        frame_body = frame_body.substr(pos+1);

        pos = temp.find(':');
        key = temp.substr(0,pos);
        val = temp.substr(pos+2);

        game_updates[key]= val;
    }
    
    //////////////////////////////////////////////////////
    pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    while (frame_body.at(0) == '\t')
    {
        pos = frame_body.find('\n');
        temp = frame_body.substr(1,pos-1);
        frame_body = frame_body.substr(pos+1);

        pos = temp.find(':');
        key = temp.substr(0,pos);
        val = temp.substr(pos+2);

        team_a_updates[key]= val;
    }
    //////////////////////////////////////////////////////
    pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    while (frame_body.at(0) == '\t')
    {
        pos = frame_body.find('\n');
        temp = frame_body.substr(1,pos-1);
        frame_body = frame_body.substr(pos+1);

        pos = temp.find(':');
        key = temp.substr(0,pos);
        val = temp.substr(pos+2);

        team_b_updates[key]= val;
    }
    //////////////////////////////////////////////////////
    pos1 = frame_body.find('\n');
    frame_body = frame_body.substr(pos1+1);
    pos1 = frame_body.find('\n');
    description =frame_body.substr(0,pos1);
    
}

names_and_events parseEventsFile(std::string json_path)
{
    std::ifstream f(json_path);
    json data = json::parse(f);

    std::string team_a_name = data["team a"];
    std::string team_b_name = data["team b"];

    // run over all the events and convert them to Event objects
    std::vector<Event> events;
    for (auto &event : data["events"])
    {
        std::string name = event["event name"];
        int time = event["time"];
        std::string description = event["description"];
        std::map<std::string, std::string> game_updates;
        std::map<std::string, std::string> team_a_updates;
        std::map<std::string, std::string> team_b_updates;
        for (auto &update : event["general game updates"].items())
        {
            if (update.value().is_string())
                game_updates[update.key()] = update.value();
            else
                game_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team a updates"].items())
        {
            if (update.value().is_string())
                team_a_updates[update.key()] = update.value();
            else
                team_a_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team b updates"].items())
        {
            if (update.value().is_string())
                team_b_updates[update.key()] = update.value();
            else
                team_b_updates[update.key()] = update.value().dump();
        }
        
        events.push_back(Event(team_a_name, team_b_name, name, time, game_updates, team_a_updates, team_b_updates, description));
    }
    names_and_events events_and_names{team_a_name, team_b_name, events};

    return events_and_names;
}