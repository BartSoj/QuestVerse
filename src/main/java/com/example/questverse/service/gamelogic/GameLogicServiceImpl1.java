package com.example.questverse.service.gamelogic;


import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.GetNextTurnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLogicServiceImpl1 extends GameLogicService {
    private static final String GAME_NAME = "Battlefield";

    private final String SYSTEM_MESSAGE = "Please perform the function of a text adventure game, following the rules listed below:" +
            "\nStay in character as a text adventure game and respond to commands the way a text adventure game should." +
            "\nHour of the day, number of the day, user XP, user level, user HP, combat mode, inventory, active quests, and completed quests are all important properties that the AI needs to manage." +
            "\nThe player’s attack and the enemy’s counterattack should be placed in the same round" +
            "\nEvery user action should take a certain amount of hours" +
            "\nEvery enemy killed and progress made should give the user a certain amount of experience points" +
            "\nEvery quest completed gives some positive amount of xp" +
            "\nEnemy's attack to the user should result in decrease in hp" +
            "\nIf user has 0 hp dies and game is over and user lose" +
            "\nOnce user completes all the quests, the game is over and the user wins" +
            "\nOnce user reaches level 3 fight with the boss starts" +
            "\nIntroduce random events, encounters, or choices to keep the gameplay unpredictable and exciting. The AI should respond to these events based on the player's choices." +
            "\nBoss fight should be against one strong enemy and it should be the hardest fight, precisely describe every attack" +
            "\nWhen user has higher level enemies should be stronger";

    public String getGameName() {
        return GAME_NAME;
    }

    public GameStatus initGameStatus() {
        GameStatus gameStatus = new GameStatus();
        gameStatus.setGameMode(GAME_NAME);
        gameStatus.setRound(1);
        gameStatus.setTime("Early morning");
        gameStatus.setDay(1);
        gameStatus.setXp(100);
        gameStatus.setLevel(1);
        gameStatus.setHp(20);
        gameStatus.setCombatMode(false);
        gameStatus.getInventory().add("Sword");
        gameStatus.getInventory().add("Shield");
        gameStatus.setQuests(List.of("Slaughter 3 enemies", "Reach level 3", "Kill the boss"));
        gameStatus.getMessages().add("You wake up in the middle of the battle field. You are surrounded by enemies. They can attack you at any moment.");
        return gameStatus;
    }

    public String getSystemMessage() {
        return SYSTEM_MESSAGE;
    }

    private String getTurnCircumstances(GameStatus gameStatus) {
        if (gameStatus.getHp() <= 0) {
            return "You have died. Game Over. You can not do any action.";
        }
        if (gameStatus.getQuests().isEmpty()) {
            return "Congratulations. You have completed the quest. You have won the game.";
        }
        if (gameStatus.isCombatMode()) {
            return "Hostile forces are attacking the player. Precisely describe the enemy attack, what weapon or spell enemy used to attack and the number of hp point lost by the player. Do not allow player to avoid the attack. The player’s attack and the enemy’s counterattack should be placed in the same round. The combat ends only when enemies are killed";
        } else {
            return "Hostile forces are approaching. Engage player in the combat. The player can not avoid combat. If the player is engaged in combat, the player should be attacked every turn. The combat ends only when enemies are killed.";
        }
    }

    @Override
    public GetNextTurnRequest convertToRequest(GameStatus gameStatus) {
        GetNextTurnRequest nextTurnRequest = super.convertToRequest(gameStatus);
        nextTurnRequest.nextTurnDescription = getTurnCircumstances(gameStatus);
        return nextTurnRequest;
    }
}
