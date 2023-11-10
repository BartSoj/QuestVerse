package com.example.questverse.service.chatcompletion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class GetNextTurn {
    @JsonPropertyDescription("List of active quests, should be unchanged from the previous turn")
    @JsonProperty(required = true)
    public List<String> quests;
    @JsonPropertyDescription("true / false list for each quest, true if player messages show that he completed the quest in any degree, false otherwise")
    @JsonProperty(required = true)
    public List<Boolean> questCompleted;
    @JsonPropertyDescription("Current time of the day should be later the previous time of the day")
    @JsonProperty(required = true)
    public String timeOfDay;
    @JsonPropertyDescription("Current day, should change in accordance to the time of the day")
    @JsonProperty(required = true)
    public int dayNumber;
    @JsonPropertyDescription("New experience points gained by the user, value dependent on user action")
    @JsonProperty(required = true)
    public int gainedExperiencePoints;
    @JsonPropertyDescription("Players' hit points. Should change depending on user action")
    @JsonProperty(required = true)
    public int hp;
    @JsonPropertyDescription("True if the user is engaged in combat. If all the enemies are defeated, this should be false")
    public boolean engagedInCombat;
    @JsonPropertyDescription("Items in inventory")
    @JsonProperty(required = true)
    public List<String> inventory;
}
