package com.example.questverse.service.gamelogic;

import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.GetNextTurn;
import com.example.questverse.service.chatcompletion.GetNextTurnRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GameLogicService {

    /**
     * Retrieves the name of the game.
     *
     * @return The name of the game.
     */
    public abstract String getGameName();

    /**
     * Initializes and returns a new GameStatus instance representing the game's initial state.
     *
     * @return A GameStatus instance representing the initial game state.
     */
    public abstract GameStatus initGameStatus();

    /**
     * Retrieves the system message for the game.
     *
     * @return The system message used in the game.
     */
    public abstract String getSystemMessage();

    /**
     * Converts a GameStatus object into a GetNextTurnRequest for interaction with the chat model.
     *
     * @param gameStatus The GameStatus to convert into a request.
     * @return The GetNextTurnRequest based on the provided GameStatus.
     */
    public GetNextTurnRequest convertToRequest(GameStatus gameStatus) {
        GetNextTurnRequest request = new GetNextTurnRequest();
        request.time = gameStatus.getTime();
        request.day = gameStatus.getDay();
        request.xp = gameStatus.getXp();
        request.level = gameStatus.getLevel();
        request.hp = gameStatus.getHp();
        request.inventory = gameStatus.getInventory();
        request.quests = gameStatus.getQuests();
        return request;
    }

    /**
     * Updates the game status based on the information from a GetNextTurn response.
     *
     * @param gameStatus The current GameStatus.
     * @param nextTurn   The GetNextTurn response containing updates.
     * @return The updated GameStatus after processing the next turn.
     */
    public GameStatus updateGameStatus(GameStatus gameStatus, GetNextTurn nextTurn) {
        GameStatus newGameStatus = new GameStatus();
        newGameStatus.setGameMode(gameStatus.getGameMode());
        newGameStatus.setRound(gameStatus.getRound() + 1);
        newGameStatus.setTime(nextTurn.timeOfDay);
        newGameStatus.setDay(nextTurn.dayNumber);
        int xp = gameStatus.getXp() + nextTurn.gainedExperiencePoints;
        if (xp > 1000) {
            newGameStatus.setXp(xp - 1000);
            newGameStatus.setLevel(gameStatus.getLevel() + 1);
            newGameStatus.setHp(20);
        } else {
            newGameStatus.setXp(xp);
            newGameStatus.setLevel(gameStatus.getLevel());
        }
        newGameStatus.setHp(nextTurn.hp);
        newGameStatus.setCombatMode(nextTurn.engagedInCombat);
        newGameStatus.setInventory(nextTurn.inventory);
        newGameStatus.setCompletedQuests(gameStatus.getCompletedQuests());
        for (int i = 0; i < nextTurn.quests.size(); i++) {
            if (nextTurn.questCompleted.get(i)) {
                newGameStatus.getCompletedQuests().add(nextTurn.quests.get(i));
            } else {
                newGameStatus.getQuests().add(nextTurn.quests.get(i));
            }
        }
        newGameStatus.setMessages(gameStatus.getMessages());
        return newGameStatus;
    }
}
