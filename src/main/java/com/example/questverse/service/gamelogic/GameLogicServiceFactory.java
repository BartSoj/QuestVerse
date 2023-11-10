package com.example.questverse.service.gamelogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameLogicServiceFactory {

    private final Map<String, GameLogicService> gameLogicServices;

    @Autowired
    public GameLogicServiceFactory(Map<String, GameLogicService> gameLogicServices) {
        List<String> gameModes = new LinkedList<>(gameLogicServices.keySet());
        for (String gameMode : gameModes) {
            GameLogicService gameLogicService = gameLogicServices.get(gameMode);
            gameLogicServices.put(gameLogicService.getGameName(), gameLogicService);
            gameLogicServices.remove(gameMode);
        }
        this.gameLogicServices = gameLogicServices;
    }

    public GameLogicService getGameLogicService(String gameMode) {
        GameLogicService gameLogicService = gameLogicServices.get(gameMode);
        if (gameLogicService == null) {
            throw new IllegalArgumentException("No game logic service for game mode: " + gameMode);
        }
        return gameLogicService;
    }
}
