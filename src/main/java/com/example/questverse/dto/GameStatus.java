package com.example.questverse.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GameStatus {
    private String gameMode;
    private int round;
    private String time;
    private int day;
    private int xp;
    private int level;
    private int hp;
    private boolean combatMode;
    private List<String> inventory = new ArrayList<>();
    private List<String> quests = new ArrayList<>();
    private List<String> completedQuests = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
}
