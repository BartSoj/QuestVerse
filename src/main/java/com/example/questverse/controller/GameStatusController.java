package com.example.questverse.controller;

import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.GameStatusService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@RestController
public class GameStatusController {

    private final GameStatusService gameStatusService;

    @Autowired
    public GameStatusController(GameStatusService gameStatusService) {
        this.gameStatusService = gameStatusService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping(value = "/streamTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamTest() {
        Flowable<String> stringFlowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(tick -> "Data at second: " + tick);

        return Flux.from(stringFlowable.toObservable().toFlowable(BackpressureStrategy.BUFFER));
    }

    @GetMapping("/newGameStatus")
    @ResponseBody
    public GameStatus newGameStatus(@RequestParam String gameMode) {
        return gameStatusService.getNewGameStatus(gameMode);
    }

    @GetMapping(value = "/streamGameDescription", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamGameDescription(@RequestBody GameStatus gameMode) {
        Flowable<String> flowableDescription = gameStatusService.getFlowableDescription(gameMode);

        return Flux.from(flowableDescription.toObservable().toFlowable(BackpressureStrategy.BUFFER));
    }


    @GetMapping("/nextGameStatus")
    @ResponseBody
    public GameStatus nextGameStatus(@RequestBody GameStatus gameStatus) {
        return gameStatusService.getNextGameStatus(gameStatus);
    }
}
