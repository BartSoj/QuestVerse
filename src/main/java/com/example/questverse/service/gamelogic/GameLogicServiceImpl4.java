package com.example.questverse.service.gamelogic;


import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.GetNextTurnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GameLogicServiceImpl4 extends GameLogicService {
    private static final String GAME_NAME = "Random journey";

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
                        The AI should keep track of available quests. Often add new quests and ask player if he want to take a quest. The quest is completed if the player messages indicate that player completed the quest in any degree."
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
        gameStatus.setXp(100);
        gameStatus.setLevel(1);
        gameStatus.setHp(20);
        gameStatus.setCombatMode(false);
        gameStatus.getMessages().add("Welcome, brave adventurer! You are about to embark on an enthralling journey filled with mystery, challenges, and unexpected surprises. Are you ready to begin, or would you like to set the stage with your own imaginative start to this epic adventure?");
        return gameStatus;
    }

    public String getSystemMessage() {
        return SYSTEM_MESSAGE;
    }

    private String getTurnCircumstances(GameStatus gameStatus) {
        if (gameStatus.getHp() <= 0) {
            return "You have died. Game Over. You can not do any action.";
        }
        if (gameStatus.isCombatMode()) {
            return "Hostile forces are attacking the player. Precisely describe the enemy attack, what weapon or spell enemy used to attack and the number of hp point lost by the player. Do not allow player to avoid the attack. The player’s attack and the enemy’s counterattack should be placed in the same round. The combat ends only when enemies are killed";
        }
        return "Incorporate interactive elements such as objects, items, and non-playable characters (NPCs). Ensure the game world is vividly described, with rich details about the surroundings, atmosphere, and the personalities of the characters involved. Create a sense of urgency or mystery to engage the player and give the story depth and meaning.\n" +
                "Include a combat scenario with specific characters, ensuring the battle is challenging yet fair, with clear options for the player to choose from. Introduce random events or encounters that can influence the outcome of the story, adding an element of unpredictability to enhance the gaming experience. Provide choices that impact the game’s progression, ensuring that the player feels their decisions are significant and contribute to the development of the story.\n" +
                "Remember to maintain a balance between narrative and gameplay, keeping the player engaged and invested in the story while providing ample opportunities for action and decision-making. Ensure that the response encourages a feeling of progress, with rewards or advancements in the story as the player successfully navigates through challenges and quests.\n" +
                "Focus on creating an immersive and interactive gaming experience, catering to the player’s desire for adventure, exploration, and meaningful choices. Aim to leave the player eager for the next part of the story, intrigued by the world you’ve created, and satisfied with the sense of progress and achievement in the game.\n" +
                "The response should be short and concise";
    }

    @Override
    public GetNextTurnRequest convertToRequest(GameStatus gameStatus) {
        GetNextTurnRequest nextTurnRequest = super.convertToRequest(gameStatus);
        nextTurnRequest.nextTurnDescription = getTurnCircumstances(gameStatus);
        return nextTurnRequest;
    }
}
