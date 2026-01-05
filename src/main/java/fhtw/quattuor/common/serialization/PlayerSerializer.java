package fhtw.quattuor.common.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.model.Player;

import java.util.Collections;
import java.util.List;

public class PlayerSerializer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serializePlayer(Player player) {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(player);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing player:" + e.getMessage());
        }
        return jsonString;
    }

    public Player deserializePlayer(String jsonString) {
        Player player = null;
        try {
            player = objectMapper.readValue(jsonString, Player.class);
        } catch (JsonProcessingException e) {
            System.err.println("Error deserializing player:" + e.getMessage());
        }
        return player;
    }

    public String serializePlayers(List<Player> players) {
        String jsonString = "[]";
        try {
            jsonString = objectMapper.writeValueAsString(players);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing players list: " + e.getMessage());
        }
        return jsonString;
    }

    public List<Player> deserializePlayers(String jsonString) {
        // Needed to make a type of "List of Players"
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Player.class);
        try {
            return objectMapper.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            System.err.println("Error deserializing players list: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
