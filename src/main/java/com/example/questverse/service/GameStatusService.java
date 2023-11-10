package com.example.questverse.service;

import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.chatcompletion.ChatCompletionService;
import com.example.questverse.service.gamelogic.GameLogicService;
import com.example.questverse.service.gamelogic.GameLogicServiceFactory;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameStatusService {
    private final ChatCompletionService completionService;

    private final GameLogicServiceFactory gameLogicServiceFactory;

    @Autowired
    public GameStatusService(ChatCompletionService completionService, GameLogicServiceFactory gameLogicServiceFactory) {
        this.completionService = completionService;
        this.gameLogicServiceFactory = gameLogicServiceFactory;
    }

    public GameStatus getNewGameStatus(String gameMode) {
        GameLogicService gameLogicService = gameLogicServiceFactory.getGameLogicService(gameMode);
        return gameLogicService.initGameStatus();
    }


    public Flowable<String> getFlowableDescription(GameStatus gameStatus) {
        return completionService.getFlowableDescription(gameStatus);
    }

    public GameStatus getNextGameStatus(GameStatus gameStatus) {
        return completionService.getNewGameStatus(gameStatus);
    }
}
