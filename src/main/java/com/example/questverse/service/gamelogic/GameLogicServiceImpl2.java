package com.example.questverse.service.gamelogic;


import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.GetNextTurn;
import com.example.questverse.service.chatcompletion.GetNextTurnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Qualifier
public class GameLogicServiceImpl2 extends GameLogicService {
    private static final String GAME_NAME = "Realm of Eldoria";

    private final String SYSTEM_MESSAGE = """
            Please perform the function of a text adventure game, following the rules listed below:
            Character Stats:
            Hour of the day, number of the day, user XP, user level, user HP, combat mode, inventory, active quests, and completed quests are all important properties that the AI needs to manage.
            Time and Day:
            The game's world should have a dynamic day-night cycle. The AI should keep track of the current time and day, which can influence various events and interactions.
            User XP and Level:
            The AI should award XP for completing quests and defeating enemies. The AI should adjust the user's level accordingly and inform the player of any new abilities or advantages gained.
            User HP:
            User HP (Hit Points) represent the character's health. In combat, the AI should calculate damage received and update the user's HP accordingly. The player can with the use of items.
            Combat Mode:
            The AI should switch combat mode to "true" when the player is engaged in combat. During combat, the AI should provide the player with options for attacking, defending, or using special abilities. When combat ends, set combat mode to "false."
            Inventory:
            Maintain a list of items and equipment in the player's inventory. Allow the player to view, equip, use, or drop items. Inventory management should influence the character's stats and abilities.
            Quests:
            The AI should keep track of available quests. Never add new quests. The quest is completed if the player messages indicate that player completed the quest in any degree."
            User Input Handling:
            The AI should be able to process a wide range of user inputs. It should recognize and respond to commands related to movement, interaction with objects, initiating combat, using items, accepting quests, and more.
            Narrative and Descriptions:
            The AI should be able to generate rich descriptions of the game world, characters, and events to immerse the player in the story. Descriptions should adapt based on the time of day, location, and player progress.
            Conversational AI:
            Enable the AI to engage in dialogues with non-player characters (NPCs) or provide hints and guidance to the player. It should be able to generate responses that fit the personalities and roles of different NPCs.
            Random Events:
            Introduce random events, encounters, or choices to keep the gameplay unpredictable and exciting. The AI should respond to these events based on the player's choices.
            Restrictions:
            The response should consist solely of description of the game world, the player's surroundings, and the player's actions. The AI should not provide any information about the game's mechanics, such as the player's stats, the number of enemies, or the amount of damage dealt.
            The response short and concise, it must be 2 sentences long. The AI should not provide a wall of text.
            """;

    public String getGameName() {
        return GAME_NAME;
    }

    public GameStatus initGameStatus() {
        GameStatus gameStatus = new GameStatus();
        gameStatus.setGameMode(GAME_NAME);
        gameStatus.setRound(1);
        gameStatus.setTime("Early morning");
        gameStatus.setDay(1);
        gameStatus.setXp(0);
        gameStatus.setLevel(1);
        gameStatus.setHp(20);
        gameStatus.setCombatMode(false);
        gameStatus.setQuests(List.of("Explore deeper into the forest"));
        gameStatus.getMessages().add("Welcome, brave adventurer, to the mystical realm of Eldoria, a land shrouded in mystery and peril. As you open your eyes, you find yourself standing at the edge of a dense, ancient forest. The emerald leaves of towering oak trees whisper secrets of times long past, and the gentle breeze carries the scent of wildflowers in bloom. Your first step in this journey is crucial. You must decide whether to venture into the forest or take a different path. The choices you make will shape your destiny. What would you like to do?");
        return gameStatus;
    }

    public String getSystemMessage() {
        return SYSTEM_MESSAGE;
    }

    private String getTurnCircumstances(GameStatus gameStatus) {
        if (gameStatus.getQuests().isEmpty()) {
            return "Congratulations. You have completed all the quests. You have won the game.";
        }
        if (gameStatus.isCombatMode()) {
            return "Precisely describe the enemy attack, what weapon or spell enemy used to attack and the number of hp point lost by the user. Do not allow user to avoid the attack. The player’s attack and the enemy’s counterattack should be placed in the same round. If enemy is defeated or enemy or player has retreated, combat mode is false.";
        }
        switch (gameStatus.getCompletedQuests().size()) {
            case 0 -> {
                return """
                        The "explore deeper into the forest" quest is completed as soon as player steps into the forest.
                        Description of player surroundings: The forest before the player is a realm of enchantment and mystery. As you step onto the path that winds into the heart of the woods, you are immediately enveloped by a lush canopy of ancient oaks, their gnarled branches intertwining to form a natural archway. Shafts of golden sunlight filter through the leaves, dappling the ground with a warm, ethereal glow.
                        """;
            }
            case 1 -> {
                return """
                        If the user decides to look for the temple for the first he should be attacked be the enemy. The enemy should be described as a monster. If player look for temple for the next time and he defeated the enemy can can see the temple in front of him and the quest is completed.
                        The quest is completed as soon as player messages indicate that player found the temple. Inside the temple the user finds the scroll. Do not give the content of the scroll.
                        """;
            }
            case 2 -> {
                return """
                        if a player inspects the scroll, he sees: this riddle:
                        '"I'm born in darkness, but thrive in the light,
                                     A symbol of knowledge, with pages so bright.
                                     I'm a key to the past, a window to time,
                                     In libraries, I'm found, in prose and in rhyme.
                                     What am I?'
                        Do not give the answer to the user. User must write the answer himself.
                        the quest is completed if the player answers 'book' or 'books' and this was the last quest so the player won the game and it's game over.
                        """;
            }
        }
        return "You have completed all the quests. You have won the game.";
    }

    @Override
    public GetNextTurnRequest convertToRequest(GameStatus gameStatus) {
        GetNextTurnRequest nextTurnRequest = super.convertToRequest(gameStatus);
        nextTurnRequest.nextTurnDescription = getTurnCircumstances(gameStatus);
        return nextTurnRequest;
    }

    @Override
    public GameStatus updateGameStatus(GameStatus gameStatus, GetNextTurn nextTurn) {
        GameStatus newGameStatus = super.updateGameStatus(gameStatus, nextTurn);
        switch (newGameStatus.getCompletedQuests().size()) {
            case 0 -> newGameStatus.setQuests(List.of("Explore deeper into the forest"));
            case 1 -> newGameStatus.setQuests(List.of("Find the ancient temple"));
            case 2 -> newGameStatus.setQuests(List.of("Decipher the scroll and uncover its secrets."));
            default -> newGameStatus.setQuests(List.of());
        }
        return newGameStatus;
    }
}
