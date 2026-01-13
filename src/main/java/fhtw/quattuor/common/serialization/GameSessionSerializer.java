package fhtw.quattuor.common.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.model.GameSession;

import java.util.Collections;
import java.util.List;

public class GameSessionSerializer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serializeSession(GameSession gameSession) {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(gameSession);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing player:" + e.getMessage());
        }
        return jsonString;
    }

    public GameSession deserializeSession(String jsonString) {
        GameSession session = null;
        try {
            session = objectMapper.readValue(jsonString, GameSession.class);
        } catch (JsonProcessingException e) {
            System.err.println("Error deserializing player:" + e.getMessage());
        }
        return session;
    }

    public String serializeSessions(List<GameSession> sessions) {
        String jsonString = "[]";
        try {
            jsonString = objectMapper.writeValueAsString(sessions);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing players list: " + e.getMessage());
        }
        return jsonString;
    }

    public List<GameSession> deserializeSessions(String jsonString) {
        // Needed to make a type of "List of Players"
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, GameSession.class);
        try {
            return objectMapper.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            System.err.println("Error deserializing players list: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
