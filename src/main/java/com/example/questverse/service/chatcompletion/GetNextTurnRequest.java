package com.example.questverse.service.chatcompletion;

import java.util.List;

public class GetNextTurnRequest {
    public String nextTurnDescription;
    public List<String> quests;
    public String time;
    public int day;
    public int xp;
    public int level;
    public int hp;
    public List<String> inventory;
}
