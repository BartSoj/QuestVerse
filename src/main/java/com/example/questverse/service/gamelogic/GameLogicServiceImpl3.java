package com.example.questverse.service.gamelogic;

import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.GetNextTurnRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GameLogicServiceImpl3 extends GameLogicService {
    private static final String GAME_NAME = "Path of Illumination";
    private static final String RIDDLES = """
            "Riddles List":(
            1.The Whispering Trees:
            I stand in the forest, tall and old,
            My leaves are green, my bark is cold.
            I never speak, but secrets I've told,
            Of kings and queens, and stories untold.
            What am I?

            2.Eternal Darkness:
            I'm not alive, but I can grow;
            I don't have lungs, but I need air;
            I don't have a mouth, but water kills me.
            What am I?
                        
            3.The 7th Month:
            I am taken from a mine and shut up in a wooden case,
            from which I am never released,
            and yet I am used by almost every person.
            What am I?

            4.The Two Sisters:
            Two sisters are we, one is dark and one is fair,
            In twin towers dwelling, we're quite a striking pair.
            One from land, one from sea, and different tales we weave,
            But we both cast a spell, making all believe.
            What are we?

            5.The Four-Legged Sunrise:
            I come out of the earth, I am sold in the market.
            People use me for their nourishment daily.
            The sunrise would be so beautiful without me.
            What am I?

            6.The Versatile Wordplay:
            I can be cracked, made, told, and played,
            I'm sometimes written, yet often betrayed.
            I can be long, or I can be short,
            I can be simple, or I can be complex.
            What am I?

            7.The Enigmatic Mirror:
            I'm a portal to another world, yet I never move.
            Reflecting all that passes, secrets I may prove.
            I show your true self, with no disguise or mask.
            What am I?

            8.The Bridge of Lies:
            I can only live where there is light,
            But I die if the light shines too bright.
            You see me often when you cross the way,
            But in the darkness, I cannot stay.
            What am I?

            9.The Silent Musician:
            I have keys but can't open locks,
            I can play music, but not in a band.
            I can predict the future, but not tell the time.
            What am I?

            10.The Vanishing Act:
            I disappear as soon as u say my name.
            What am I?)

            Answers:(
            1.Book
            2.Fire
            3.Pencil
            4.Day and Night
            5.Coffee
            6.Joke
            7.Mirror
            8.Shadow
            9.Piano
            10.Silence)
            """;

    private final String SYSTEM_MESSAGE = """
            Please perform the function of a text adventure game, following the rules listed below:
            User Input Handling:
            The AI should be able to process a wide range of user inputs. It should recognize and respond to commands related to movement, interaction with objects, using items, accepting quests, and more.
            Narrative and Descriptions:
            The AI should be able to generate rich descriptions of the game world, characters, and events to immerse the player in the story. Descriptions should adapt based on the time of day, location, and player progress.
            Conversational AI:
            Enable The AI to impersonate librarian of the magic library that is giving puzzles to the player.
            Enable the AI to engage in dialogues with non-player characters (NPCs) or provide hints and guidance to the player. It should be able to generate responses that fit the personalities and roles of different NPCs.
            Character Stats:
            Hour of the day, number of the day, user XP, user level, user HP, inventory, quests, and completed quests are all important properties that the AI needs to manage.
            Time and Day:
            As the plot is happening in the magic library time does not need to be linear.
            The game's world should have a dynamic day-night cycle. The AI should keep track of the current time and day, which can influence various events and interactions.
            User XP and Level:
            The AI should award XP for completing quests. The AI should adjust the user's level accordingly to number of puzzles solved.
            User HP:
            User HP (Hit Points) represent the character's health.
            Inventory:
            Maintain a list of items and equipment in the player's inventory. Allow the player to view, equip, use, or drop items. Inventory management should influence the character's stats.
            Quests:
            The AI should keep track of available quests. Keep all the quests do not remove them. Never add new quests. The quest is completed if the player messages indicate that player completed the quest in any degree."
            Random Events:
            Introduce random events, encounters, or choices to keep the gameplay unpredictable and exciting. The AI should respond to these events based on the player's choices.
            Restrictions:
            Player should not leave the library.
            The response should consist solely of description of the game world, the player's surroundings, and the player's actions. The AI should not provide any information about the game's mechanics, such as the player's stats.
            The response short and concise, it must be 2 sentences long. The AI should not provide a wall of text.
            The AI as librarian should tell puzzle requested by the user from "Riddles List". For every wrong answer player should lose 5 hp. The AI should warn player about losing hp when giving wrong answer.
            The AI should not give away the answers to the riddles or puzzles.
            """ + RIDDLES;

    public String getGameName() {
        return GAME_NAME;
    }

    public GameStatus initGameStatus() {
        GameStatus gameStatus = new GameStatus();
        gameStatus.setGameMode(GAME_NAME);
        gameStatus.setRound(1);
        gameStatus.setTime("Morning");
        gameStatus.setDay(1);
        gameStatus.setXp(0);
        gameStatus.setLevel(1);
        gameStatus.setHp(20);
        gameStatus.setCombatMode(false);
        gameStatus.setQuests(List.of("The Whispering Trees",
                "Eternal Darkness",
                "The 7th Month",
                "The Two Sisters",
                "The Four-Legged Sunrise",
                "The Versatile Wordplay",
                "The Enigmatic Mirror",
                "The Bridge of Lies",
                "The Silent Musician",
                "The Vanishing Act"));
        gameStatus.getMessages().add("\n" +
                "You've entered a magical library, transcending time and space, filled with centuries-old knowledge and an endless array of books, each with its own tale. The shelves stretch into a starry sky above.\n" +
                "\n" +
                "Welcome, knowledge seeker, to the library's heart, where the universe's secrets await the worthy. Embark on your journey with a desire for truth, wisdom, and a noble spirit. Face the challenges with an open heart and clear mind to unlock the library's profound wisdom. Best of luck on your enlightening journey.");
        return gameStatus;
    }

    public String getSystemMessage() {
        return SYSTEM_MESSAGE;
    }

    private String getTurnCircumstances(GameStatus gameStatus) {
        if (gameStatus.getQuests().isEmpty()) {
            return "Congratulations. You have completed all the quests. You have won the game. Librarian gives you a key to the library you can come back and learn everything you have dreamed of.";
        }
        if (gameStatus.getHp() <= 0) {
            return "You have lost, you have failed the test. Game Over. You can not do any action.";
        }
        return "";
    }

    @Override
    public GetNextTurnRequest convertToRequest(GameStatus gameStatus) {
        GetNextTurnRequest nextTurnRequest = super.convertToRequest(gameStatus);
        nextTurnRequest.nextTurnDescription = getTurnCircumstances(gameStatus);
        return nextTurnRequest;
    }
}
