package com.example.questverse.service.chatcompletion;

import com.example.questverse.dto.GameStatus;
import com.example.questverse.service.gamelogic.GameLogicService;
import com.example.questverse.service.gamelogic.GameLogicServiceFactory;
import com.example.questverse.utils.OpenAiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class ChatCompletionService {
    private final OpenAiService service;

    private final GameLogicServiceFactory gameLogicServiceFactory;

    /**
     * Constructor for the ChatCompletionService class.
     *
     * @param gameLogicServiceFactory The GameLogicServiceFactory instance.
     */
    @Autowired
    public ChatCompletionService(GameLogicServiceFactory gameLogicServiceFactory, OpenAiConfig openAiConfig) {
        String token = openAiConfig.getApiKey();
        service = new OpenAiService(token, Duration.ofSeconds(30));
        this.gameLogicServiceFactory = gameLogicServiceFactory;
    }

    private GameLogicService getGameLogicService(String gameMode) {
        return gameLogicServiceFactory.getGameLogicService(gameMode);
    }

    /**
     * Converts a list of messages into ChatMessage objects for interaction with the chat model.
     *
     * @param gameStatus The current game status.
     * @return A list of ChatMessage objects.
     */
    private List<ChatMessage> convertToChatMessages(GameStatus gameStatus) {
        List<ChatMessage> chatMessages = new LinkedList<>();
        chatMessages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), getGameLogicService(gameStatus.getGameMode()).getSystemMessage()));
        List<String> messages = gameStatus.getMessages();
        boolean isAssistant = true;
        for (String message : messages) {
            chatMessages.add(new ChatMessage(isAssistant ? ChatMessageRole.ASSISTANT.value() : ChatMessageRole.USER.value(), message));
            isAssistant = !isAssistant;
        }
        return chatMessages;
    }

    /**
     * Generates a ChatMessage representation of the game status.
     *
     * @param gameStatus The current game status.
     * @return Formatted ChatMessage.
     */
    private ChatMessage getFunctionMessage(GameStatus gameStatus) {
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        String arguments;
        try {
            GetNextTurnRequest request = getGameLogicService(gameStatus.getGameMode()).convertToRequest(gameStatus);
            arguments = mapper.readValue(mapper.writeValueAsString(request), JsonNode.class).toPrettyString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new ChatMessage(ChatMessageRole.FUNCTION.value(), arguments, "get_game_status");
    }

    /**
     * Retrieves a Flowable of description for the provided game status.
     *
     * @param gameStatus The GameStatus instance for which description is needed.
     * @return A Flowable of description for the given game status.
     */
    public Flowable<String> getFlowableDescription(GameStatus gameStatus) {
        List<ChatMessage> messages = convertToChatMessages(gameStatus);
        messages.add(getFunctionMessage(gameStatus));

        ChatCompletionRequest descriptionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .logitBias(new HashMap<>())
                .build();

        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(descriptionRequest);

        return service.mapStreamToAccumulator(flowable)
                .filter(accumulator -> accumulator.getMessageChunk().getContent() != null)
                .map(accumulator -> accumulator.getMessageChunk().getContent());
    }

    /**
     * Advances the game status to the next state based on the provided game status.
     *
     * @param gameStatus The current GameStatus instance to progress from.
     * @return The next GameStatus instance after a game state transition.
     */
    public GameStatus getNewGameStatus(GameStatus gameStatus) {
        List<ChatMessage> messages = convertToChatMessages(gameStatus);
        messages.add(messages.size() - 2, getFunctionMessage(gameStatus));

        FunctionExecutor functionExecutor = new FunctionExecutor(List.of(ChatFunction.builder()
                .name("get_last_turn_info")
                .description("get the information about the last turn")
                .executor(GetNextTurn.class, x -> gameLogicServiceFactory.getGameLogicService(gameStatus.getGameMode()).updateGameStatus(gameStatus, x))
                .build()));

        ChatCompletionRequest completionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .functions(functionExecutor.getFunctions())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("get_last_turn_info"))
                .n(1)
                .logitBias(new HashMap<>())
                .build();

        ChatMessage responseMessage = service.createChatCompletion(completionRequest).getChoices().get(0).getMessage();
        if (responseMessage.getFunctionCall() == null) {
            throw new RuntimeException("No function call returned");
        }
        ChatFunctionCall responseMessageFunctionCall = responseMessage.getFunctionCall();
        GameStatus newGameStatus = functionExecutor.execute(responseMessageFunctionCall);
        log.debug("GameStatus: {}", newGameStatus);
        return newGameStatus;
    }
}
